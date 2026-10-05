'use client';

import { useState } from 'react';
import { useProducts, useProductsByCategory } from '@/hooks/useProducts';
import ProductCard from '@/components/ProductCard';
import LoadingSkeleton from '@/components/LoadingSkeleton';

const CATEGORIES = ['All', 'Electronics', 'Clothing', 'Books', 'Home', 'Sports'];

export default function ProductsPage() {
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState('All');

  const allProducts = useProducts(page, 12);
  const byCategory = useProductsByCategory(category, page, 12);

  const { data, isLoading, isError } = category === 'All' ? allProducts : byCategory;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Products</h1>
        <p className="text-gray-500">
          {data ? `${data.totalElements} products found` : 'Browse our catalog'}
        </p>
      </div>

      {/* Category Filter */}
      <div className="flex gap-2 flex-wrap mb-8">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => { setCategory(cat); setPage(0); }}
            className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
              category === cat
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-600 border border-gray-200 hover:border-blue-300 hover:text-blue-600'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Products Grid */}
      {isLoading ? (
        <LoadingSkeleton count={12} />
      ) : isError ? (
        <div className="text-center py-20">
          <p className="text-red-500 text-lg">⚠️ Failed to load products. Is the API running?</p>
          <p className="text-gray-400 text-sm mt-2">Make sure docker-compose is up at localhost:8080</p>
        </div>
      ) : data?.content.length === 0 ? (
        <div className="text-center py-20 text-gray-500">
          <p className="text-5xl mb-4">📭</p>
          <p className="text-xl font-medium">No products found</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {data?.content.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {/* Pagination */}
          {data && data.totalPages > 1 && (
            <div className="flex justify-center items-center gap-4 mt-10">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="btn-secondary disabled:opacity-40"
              >
                ← Previous
              </button>
              <span className="text-sm text-gray-600">
                Page {page + 1} of {data.totalPages}
              </span>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={data.last}
                className="btn-secondary disabled:opacity-40"
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
