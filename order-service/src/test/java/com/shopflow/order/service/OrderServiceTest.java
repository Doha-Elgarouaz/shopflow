package com.shopflow.order.service;

import com.shopflow.order.client.ProductClient;
import com.shopflow.order.client.ProductInfo;
import com.shopflow.order.dto.*;
import com.shopflow.order.entity.Order;
import com.shopflow.order.entity.OrderStatus;
import com.shopflow.order.exception.OrderNotFoundException;
import com.shopflow.order.kafka.OrderEventProducer;
import com.shopflow.order.kafka.event.OrderCreatedEvent;
import com.shopflow.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderEventProducer orderEventProducer;
    @Mock private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private CreateOrderRequest createRequest;
    private ProductInfo sampleProductInfo;

    @BeforeEach
    void setUp() {
        sampleOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-ABC12345")
                .customerId("user-123")
                .customerEmail("test@shopflow.com")
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("149.99"))
                .shippingAddress("123 Main St, Paris")
                .items(new ArrayList<>())
                .build();

        createRequest = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2)),
                "123 Main St, Paris"
        );

        sampleProductInfo = new ProductInfo(1L, "Nike Air Max", new BigDecimal("74.995"), 50);
    }

    @Test
    @DisplayName("createOrder — should create order, publish Kafka event, return response")
    void createOrder_ShouldCreateOrderAndPublishEvent() {
        when(productClient.getProductsByIds(List.of(1L))).thenReturn(List.of(sampleProductInfo));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        OrderResponse response = orderService.createOrder(createRequest, "user-123", "test@shopflow.com");

        assertThat(response).isNotNull();
        assertThat(response.customerId()).isEqualTo("user-123");
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventProducer, times(1)).publishOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("getOrderById — should return order for correct customer")
    void getOrderById_WhenCustomerMatches_ShouldReturnResponse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        OrderResponse response = orderService.getOrderById(1L, "user-123", false);

        assertThat(response.orderNumber()).isEqualTo("ORD-ABC12345");
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("getOrderById — should throw when customer doesn't own the order")
    void getOrderById_WhenCustomerMismatch_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThatThrownBy(() -> orderService.getOrderById(1L, "other-user", false))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("getOrderById — admin should access any order")
    void getOrderById_WhenAdmin_ShouldReturnAnyOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        OrderResponse response = orderService.getOrderById(1L, "admin-user", true);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getOrderById — should throw when order not found")
    void getOrderById_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(999L, "user-123", false))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("cancelOrder — should update status to CANCELLED and publish event")
    void cancelOrder_ShouldCancelAndPublishEvent() {
        when(orderRepository.findByIdAndCustomerId(1L, "user-123"))
                .thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        OrderResponse response = orderService.cancelOrder(1L, "user-123");

        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.CANCELLED));
        verify(orderEventProducer).publishOrderCancelled(any());
    }

    @Test
    @DisplayName("cancelOrder — should throw when order not found")
    void cancelOrder_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findByIdAndCustomerId(999L, "user-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(999L, "user-123"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("getAllOrders — should return paginated results")
    void getAllOrders_ShouldReturnPageResponse() {
        Page<Order> page = new PageImpl<>(List.of(sampleOrder), PageRequest.of(0, 10), 1);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<OrderResponse> response = orderService.getAllOrders(0, 10);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("updateOrderStatus — should update status (admin operation)")
    void updateOrderStatus_ShouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.SHIPPED));
    }
}
