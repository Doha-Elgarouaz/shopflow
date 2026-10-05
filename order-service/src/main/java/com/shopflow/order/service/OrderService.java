package com.shopflow.order.service;

import com.shopflow.order.client.ProductClient;
import com.shopflow.order.client.ProductInfo;
import com.shopflow.order.dto.*;
import com.shopflow.order.entity.Order;
import com.shopflow.order.entity.OrderItem;
import com.shopflow.order.entity.OrderStatus;
import com.shopflow.order.exception.OrderNotFoundException;
import com.shopflow.order.kafka.OrderEventProducer;
import com.shopflow.order.kafka.event.OrderCancelledEvent;
import com.shopflow.order.kafka.event.OrderCreatedEvent;
import com.shopflow.order.kafka.event.OrderItemEvent;
import com.shopflow.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ProductClient productClient;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String customerId, String customerEmail) {
        List<Long> productIds = request.items().stream().map(OrderItemRequest::productId).toList();
        List<ProductInfo> products = productClient.getProductsByIds(productIds);

        Map<Long, ProductInfo> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo::productId, p -> p));

        Order order = Order.builder()
                .customerId(customerId)
                .customerEmail(customerEmail)
                .shippingAddress(request.shippingAddress())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> items = request.items().stream().map(itemReq -> {
            ProductInfo product = productMap.get(itemReq.productId());
            BigDecimal unitPrice = product != null ? product.price() : BigDecimal.ZERO;
            String productName = product != null ? product.name() : "Unknown Product";
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = OrderItem.builder()
                    .productId(itemReq.productId())
                    .productName(productName)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .order(order)
                    .build();
            return item;
        }).toList();

        order.getItems().addAll(items);

        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created: {}", savedOrder.getOrderNumber());

        List<OrderItemEvent> itemEvents = savedOrder.getItems().stream()
                .map(i -> new OrderItemEvent(i.getProductId(), i.getQuantity(), i.getUnitPrice()))
                .toList();

        orderEventProducer.publishOrderCreated(new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerId(),
                savedOrder.getCustomerEmail(),
                itemEvents,
                savedOrder.getTotalAmount()
        ));

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String customerId, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (!isAdmin && !order.getCustomerId().equals(customerId)) {
            throw new OrderNotFoundException("Order not found or access denied");
        }
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrdersByCustomer(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        List<OrderResponse> content = orders.getContent().stream().map(OrderResponse::from).toList();
        return new PageResponse<>(content, page, size, orders.getTotalElements(), orders.getTotalPages(), orders.isLast());
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findAll(pageable);
        List<OrderResponse> content = orders.getContent().stream().map(OrderResponse::from).toList();
        return new PageResponse<>(content, page, size, orders.getTotalElements(), orders.getTotalPages(), orders.isLast());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, String customerId) {
        Order order = orderRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        orderEventProducer.publishOrderCancelled(new OrderCancelledEvent(
                order.getId(), order.getOrderNumber(), order.getCustomerId(),
                order.getCustomerEmail(), "Cancelled by customer"));

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }
}
