'use client';

import { useCartStore } from '@/store/cartStore';
import { useSession } from 'next-auth/react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { TrashIcon } from '@heroicons/react/24/outline';

export default function CartPage() {
  const { data: session } = useSession();
  const router = useRouter();
  const { items, removeItem, updateQuantity, clearCart, totalPrice, totalItems } = useCartStore();

  const handleCheckout = () => {
    if (!session) {
      router.push('/auth/signin');
    } else {
      router.push('/checkout');
    }
  };

  if (items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <div className="text-6xl mb-4">🛒</div>
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Your cart is empty</h1>
        <p className="text-gray-500 mb-6">Looks like you haven't added anything yet.</p>
        <Link href="/products" className="btn-primary inline-block">
          Browse Products
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Shopping Cart</h1>
        <button onClick={clearCart} className="text-sm text-red-500 hover:text-red-700 font-medium">
          Clear all
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Items list */}
        <div className="lg:col-span-2 space-y-4">
          {items.map(({ product, quantity }) => (
            <div key={product.id} className="card flex gap-4 items-center">
              {/* Product image placeholder */}
              <div className="h-20 w-20 flex-shrink-0 bg-gradient-to-br from-blue-100 to-indigo-200 rounded-xl flex items-center justify-center">
                <span className="text-2xl font-bold text-blue-400 uppercase">
                  {product.category?.[0]}
                </span>
              </div>

              <div className="flex-1 min-w-0">
                <h3 className="font-semibold text-gray-900 truncate">{product.name}</h3>
                <p className="text-sm text-gray-500">{product.category}</p>
                <p className="text-blue-600 font-bold">€{product.price.toFixed(2)}</p>
              </div>

              {/* Quantity */}
              <div className="flex items-center gap-2">
                <button
                  onClick={() => updateQuantity(product.id, quantity - 1)}
                  className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-100 font-bold"
                >
                  −
                </button>
                <span className="w-8 text-center font-semibold">{quantity}</span>
                <button
                  onClick={() => updateQuantity(product.id, quantity + 1)}
                  disabled={quantity >= product.stockQuantity}
                  className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-100 font-bold disabled:opacity-40"
                >
                  +
                </button>
              </div>

              {/* Line total */}
              <div className="text-right min-w-[70px]">
                <p className="font-bold text-gray-900">€{(product.price * quantity).toFixed(2)}</p>
              </div>

              {/* Remove */}
              <button
                onClick={() => removeItem(product.id)}
                className="text-gray-400 hover:text-red-500 transition-colors"
              >
                <TrashIcon className="h-5 w-5" />
              </button>
            </div>
          ))}
        </div>

        {/* Order summary */}
        <div>
          <div className="card sticky top-24">
            <h2 className="text-lg font-bold text-gray-900 mb-4">Order Summary</h2>
            <div className="space-y-2 text-sm text-gray-600 mb-4">
              <div className="flex justify-between">
                <span>Items ({totalItems()})</span>
                <span>€{totalPrice().toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping</span>
                <span className="text-green-600 font-medium">Free</span>
              </div>
              <div className="border-t pt-2 flex justify-between font-bold text-gray-900 text-base">
                <span>Total</span>
                <span>€{totalPrice().toFixed(2)}</span>
              </div>
            </div>

            <button onClick={handleCheckout} className="btn-primary w-full py-3 text-base">
              {session ? 'Proceed to Checkout →' : 'Sign In to Checkout →'}
            </button>

            <Link href="/products" className="block text-center text-sm text-blue-600 hover:underline mt-3">
              Continue Shopping
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
