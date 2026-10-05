'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { useCartStore } from '@/store/cartStore';
import { useCreateOrder } from '@/hooks/useOrders';
import { PaymentMethod } from '@/lib/types';
import Link from 'next/link';

export default function CheckoutPage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const { items, totalPrice, clearCart } = useCartStore();
  const createOrder = useCreateOrder();

  const [shippingAddress, setShippingAddress] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CREDIT_CARD');
  const [success, setSuccess] = useState<{ orderNumber: string } | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (status === 'unauthenticated') router.push('/auth/signin');
  }, [status, router]);

  if (status === 'loading') {
    return <div className="flex justify-center items-center min-h-96"><div className="text-gray-500">Loading...</div></div>;
  }

  if (success) {
    return (
      <div className="max-w-lg mx-auto px-4 py-20 text-center">
        <div className="text-6xl mb-4">🎉</div>
        <h1 className="text-2xl font-bold text-green-600 mb-2">Order Placed!</h1>
        <p className="text-gray-600 mb-2">Your order has been successfully created.</p>
        <p className="text-lg font-mono font-bold text-gray-800 bg-gray-100 rounded-lg px-4 py-2 inline-block mb-6">
          {success.orderNumber}
        </p>
        <p className="text-sm text-gray-500 mb-8">
          Payment is being processed automatically. You'll receive a notification once confirmed.
        </p>
        <div className="flex gap-3 justify-center">
          <Link href="/orders" className="btn-primary">View My Orders</Link>
          <Link href="/products" className="btn-secondary">Continue Shopping</Link>
        </div>
      </div>
    );
  }

  if (items.length === 0) {
    return (
      <div className="text-center py-20">
        <p className="text-gray-500 text-lg">Your cart is empty.</p>
        <Link href="/products" className="text-blue-600 hover:underline mt-2 inline-block">Browse Products</Link>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const order = await createOrder.mutateAsync({
        items: items.map((i) => ({ productId: i.product.id, quantity: i.quantity })),
        shippingAddress,
      });
      clearCart();
      setSuccess({ orderNumber: order.orderNumber });
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(message || 'Failed to place order. Please try again.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Checkout</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="card">
            <h2 className="text-lg font-bold text-gray-900 mb-4">Shipping Information</h2>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Shipping Address</label>
              <textarea
                required
                value={shippingAddress}
                onChange={(e) => setShippingAddress(e.target.value)}
                rows={3}
                placeholder="123 Main St, Paris, France 75001"
                className="input"
              />
            </div>
          </div>

          <div className="card">
            <h2 className="text-lg font-bold text-gray-900 mb-4">Payment Method</h2>
            <div className="space-y-3">
              {(['CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER'] as PaymentMethod[]).map((method) => (
                <label key={method} className="flex items-center gap-3 cursor-pointer p-3 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors">
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method}
                    checked={paymentMethod === method}
                    onChange={() => setPaymentMethod(method)}
                    className="text-blue-600"
                  />
                  <span className="font-medium text-gray-700">
                    {method === 'CREDIT_CARD' ? '💳 Credit Card'
                      : method === 'DEBIT_CARD' ? '🏦 Debit Card'
                      : method === 'PAYPAL' ? '🅿️ PayPal'
                      : '🏛️ Bank Transfer'}
                  </span>
                </label>
              ))}
            </div>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-red-700 text-sm">
              ⚠️ {error}
            </div>
          )}

          <button
            type="submit"
            disabled={createOrder.isPending}
            className="btn-primary w-full py-3 text-base"
          >
            {createOrder.isPending ? 'Placing Order...' : `Place Order — €${totalPrice().toFixed(2)}`}
          </button>
        </form>

        {/* Order Summary */}
        <div className="card h-fit">
          <h2 className="text-lg font-bold text-gray-900 mb-4">Order Summary</h2>
          <div className="space-y-3 mb-4">
            {items.map(({ product, quantity }) => (
              <div key={product.id} className="flex justify-between text-sm">
                <span className="text-gray-700 truncate mr-4">{product.name} × {quantity}</span>
                <span className="font-medium text-gray-900 flex-shrink-0">
                  €{(product.price * quantity).toFixed(2)}
                </span>
              </div>
            ))}
          </div>
          <div className="border-t pt-3 flex justify-between font-bold text-gray-900">
            <span>Total</span>
            <span>€{totalPrice().toFixed(2)}</span>
          </div>
          <p className="text-xs text-gray-400 mt-3">
            Payment is processed automatically via Kafka event after order creation. 80% success rate simulated.
          </p>
        </div>
      </div>
    </div>
  );
}
