export interface Product {
  id: number;
  name: string;
  description: string;
  sku: string;
  price: number;
  stockQuantity: number;
  category: string;
  imageUrl?: string;
  active: boolean;
  createdAt: string;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'PAYPAL' | 'BANK_TRANSFER';
export type PaymentStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';

export interface Order {
  id: number;
  orderNumber: string;
  customerId: string;
  customerEmail: string;
  status: OrderStatus;
  items: OrderItem[];
  totalAmount: number;
  shippingAddress: string;
  createdAt: string;
  updatedAt: string;
}

export interface Payment {
  id: number;
  paymentReference: string;
  orderId: number;
  customerId: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionId?: string;
  failureReason?: string;
  createdAt: string;
  processedAt?: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface ApiPage<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface CreateOrderRequest {
  items: { productId: number; quantity: number }[];
  shippingAddress: string;
}

export interface PaymentRequest {
  orderId: number;
  amount: number;
  method: PaymentMethod;
  cardLastFour?: string;
}
