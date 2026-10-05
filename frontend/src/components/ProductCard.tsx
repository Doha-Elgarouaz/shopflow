'use client';

import { Product } from '@/lib/types';
import { useCartStore } from '@/store/cartStore';
import Link from 'next/link';
import { useState } from 'react';
import clsx from 'clsx';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const addItem = useCartStore((s) => s.addItem);
  const [added, setAdded] = useState(false);

  const handleAddToCart = () => {
    addItem(product, 1);
    setAdded(true);
    setTimeout(() => setAdded(false), 1500);
  };

  const stockStatus =
    product.stockQuantity === 0
      ? { label: 'Out of Stock', color: 'text-red-600 bg-red-50' }
      : product.stockQuantity < 5
      ? { label: `Only ${product.stockQuantity} left`, color: 'text-orange-600 bg-orange-50' }
      : { label: 'In Stock', color: 'text-green-600 bg-green-50' };

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow overflow-hidden flex flex-col">
      {/* Image placeholder */}
      <Link href={`/products/${product.id}`}>
        <div className="h-48 bg-gradient-to-br from-blue-100 to-indigo-200 flex items-center justify-center">
          <span className="text-5xl font-bold text-blue-400 uppercase">
            {product.category?.[0] || '?'}
          </span>
        </div>
      </Link>

      <div className="p-4 flex flex-col flex-1">
        {/* Category badge */}
        <span className="badge bg-blue-100 text-blue-700 mb-2 self-start">
          {product.category}
        </span>

        {/* Name */}
        <Link href={`/products/${product.id}`}>
          <h3 className="font-semibold text-gray-900 hover:text-blue-600 transition-colors line-clamp-2 mb-1">
            {product.name}
          </h3>
        </Link>

        {/* SKU */}
        <p className="text-xs text-gray-400 mb-3">SKU: {product.sku}</p>

        {/* Price */}
        <div className="mt-auto space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-xl font-bold text-blue-600">
              €{product.price.toFixed(2)}
            </span>
            <span className={clsx('badge text-xs', stockStatus.color)}>
              {stockStatus.label}
            </span>
          </div>

          <button
            onClick={handleAddToCart}
            disabled={product.stockQuantity === 0}
            className={clsx(
              'w-full py-2 px-4 rounded-lg font-semibold text-sm transition-all duration-200',
              added
                ? 'bg-green-500 text-white'
                : product.stockQuantity === 0
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-blue-600 hover:bg-blue-700 text-white'
            )}
          >
            {added ? '✓ Added!' : '+ Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  );
}
