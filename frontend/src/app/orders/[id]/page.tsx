'use client';

import { useOrder } from '@/hooks/useOrders';
import OrderStatusBadge from '@/components/OrderStatusBadge';
import Link from 'next/link';
import { useParams } from 'next/navigation';

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: order, isLoading, isError } = useOrder(Number(id));

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-8 space-y-4 animate-pulse">
        <div className="h-8 bg-gray-200 rounded w-1/2" />
        <div className="card h-48 bg-gray-100" />
        <div className="card h-48 bg-gray-100" />
      </div>
    );
  }

  if (isError || !order) {
    return (
      <div className="text-center py-20">
        <p className="text-red-500 text-xl">Order not found</p>
        <Link href="/orders" className="text-blue-600 hover:underline mt-4 inline-block">← Back to Orders</Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link href="/orders" className="text-blue-600 hover:text-blue-800 text-sm font-medium mb-6 inline-block">
        ← Back to Orders
      </Link>

      <div className="flex items-center gap-4 mb-8">
        <h1 className="text-2xl font-bold text-gray-900 font-mono">{order.orderNumber}</h1>
        <OrderStatusBadge status={order.status} />
      </div>

      <div className="space-y-6">
        {/* Details card */}
        <div className="card">
          <h2 className="text-lg font-bold text-gray-900 mb-4">Order Details</h2>
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
            <div>
              <dt className="text-gray-500 mb-0.5">Order ID</dt>
              <dd className="font-medium text-gray-900">#{order.id}</dd>
            </div>
            <div>
              <dt className="text-gray-500 mb-0.5">Date</dt>
              <dd className="font-medium text-gray-900">
                {new Date(order.createdAt).toLocaleString('fr-FR')}
              </dd>
            </div>
            <div>
              <dt className="text-gray-500 mb-0.5">Customer</dt>
              <dd className="font-medium text-gray-900">{order.customerEmail}</dd>
            </div>
            <div>
              <dt className="text-gray-500 mb-0.5">Shipping Address</dt>
              <dd className="font-medium text-gray-900">{order.shippingAddress}</dd>
            </div>
          </dl>
        </div>

        {/* Items */}
        <div className="card">
          <h2 className="text-lg font-bold text-gray-900 mb-4">Items ({order.items.length})</h2>
          <div className="divide-y divide-gray-100">
            {order.items.map((item) => (
              <div key={item.id} className="py-3 flex justify-between items-center">
                <div>
                  <p className="font-medium text-gray-900">{item.productName}</p>
                  <p className="text-sm text-gray-500">
                    {item.quantity} × €{item.unitPrice.toFixed(2)}
                  </p>
                </div>
                <span className="font-bold text-gray-900">€{item.totalPrice.toFixed(2)}</span>
              </div>
            ))}
          </div>
          <div className="border-t pt-4 flex justify-between font-bold text-lg text-gray-900 mt-2">
            <span>Total</span>
            <span className="text-blue-600">€{order.totalAmount.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
