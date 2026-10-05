'use client';

import { useProduct } from '@/hooks/useProducts';
import { useCartStore } from '@/store/cartStore';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { useState } from 'react';

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: product, isLoading, isError } = useProduct(Number(id));
  const addItem = useCartStore((s) => s.addItem);
  const [quantity, setQuantity] = useState(1);
  const [added, setAdded] = useState(false);

  const handleAddToCart = () => {
    if (!product) return;
    addItem(product, quantity);
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 animate-pulse">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="h-80 bg-gray-200 rounded-2xl" />
          <div className="space-y-4">
            <div className="h-8 bg-gray-200 rounded w-3/4" />
            <div className="h-4 bg-gray-200 rounded w-1/3" />
            <div className="h-20 bg-gray-200 rounded" />
            <div className="h-10 bg-gray-200 rounded" />
          </div>
        </div>
      </div>
    );
  }

  if (isError || !product) {
    return (
      <div className="text-center py-20">
        <p className="text-red-500 text-xl">Product not found</p>
        <Link href="/products" className="text-blue-600 hover:underline mt-4 inline-block">
          ← Back to Products
        </Link>
      </div>
    );
  }

  const maxQty = Math.min(10, product.stockQuantity);

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link href="/products" className="text-blue-600 hover:text-blue-800 text-sm font-medium mb-6 inline-block">
        ← Back to Products
      </Link>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        {/* Image */}
        <div className="h-80 bg-gradient-to-br from-blue-100 to-indigo-200 rounded-2xl flex items-center justify-center">
          <span className="text-8xl font-bold text-blue-300 uppercase">
            {product.category?.[0] || '?'}
          </span>
        </div>

        {/* Info */}
        <div className="space-y-4">
          <span className="badge bg-blue-100 text-blue-700">{product.category}</span>
          <h1 className="text-3xl font-bold text-gray-900">{product.name}</h1>
          <p className="text-sm text-gray-400">SKU: {product.sku}</p>
          <p className="text-gray-600 leading-relaxed">{product.description}</p>

          <div className="text-3xl font-extrabold text-blue-600">€{product.price.toFixed(2)}</div>

          {/* Stock */}
          <div className="flex items-center gap-2">
            <div className={`w-2 h-2 rounded-full ${product.stockQuantity > 0 ? 'bg-green-500' : 'bg-red-500'}`} />
            <span className={`text-sm font-medium ${product.stockQuantity > 0 ? 'text-green-700' : 'text-red-600'}`}>
              {product.stockQuantity === 0
                ? 'Out of Stock'
                : product.stockQuantity < 5
                ? `Only ${product.stockQuantity} left!`
                : `In Stock (${product.stockQuantity} available)`}
            </span>
          </div>

          {/* Quantity */}
          {product.stockQuantity > 0 && (
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium text-gray-700">Quantity:</label>
              <select
                value={quantity}
                onChange={(e) => setQuantity(Number(e.target.value))}
                className="input w-20"
              >
                {Array.from({ length: maxQty }, (_, i) => i + 1).map((q) => (
                  <option key={q} value={q}>{q}</option>
                ))}
              </select>
            </div>
          )}

          <button
            onClick={handleAddToCart}
            disabled={product.stockQuantity === 0}
            className={`w-full py-3 rounded-xl font-bold text-lg transition-all ${
              added
                ? 'bg-green-500 text-white'
                : product.stockQuantity === 0
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-blue-600 hover:bg-blue-700 text-white'
            }`}
          >
            {added ? '✓ Added to Cart!' : '🛒 Add to Cart'}
          </button>

          <Link href="/cart" className="block text-center text-sm text-blue-600 hover:underline">
            View Cart
          </Link>
        </div>
      </div>
    </div>
  );
}
