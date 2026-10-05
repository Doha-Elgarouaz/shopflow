'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { useMyOrders } from '@/hooks/useOrders';
import OrderStatusBadge from '@/components/OrderStatusBadge';
import Link from 'next/link';

export default function OrdersPage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [page, setPage] = useState(0);
  const { data, isLoading } = useMyOrders(page);

  useEffect(() => {
    if (status === 'unauthenticated') router.push('/auth/signin');
  }, [status, router]);

  if (status === 'loading' || isLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8 space-y-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="card animate-pulse h-20 bg-gray-100" />
        ))}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-1">My Orders</h1>
        <p className="text-gray-500">Logged in as {session?.user?.email}</p>
      </div>

      {!data?.content.length ? (
        <div className="text-center py-20">
          <div className="text-6xl mb-4">📭</div>
          <p className="text-gray-500 text-lg">No orders yet.</p>
          <Link href="/products" className="btn-primary inline-block mt-4">Start Shopping</Link>
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {data.content.map((order) => (
              <Link key={order.id} href={`/orders/${order.id}`}>
                <div className="card hover:shadow-md transition-shadow cursor-pointer">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div>
                      <div className="flex items-center gap-3 mb-1">
                        <span className="font-mono font-bold text-gray-900">{order.orderNumber}</span>
                        <OrderStatusBadge status={order.status} />
                      </div>
                      <p className="text-sm text-gray-500">
                        {order.items.length} item{order.items.length !== 1 ? 's' : ''} ·{' '}
                        {new Date(order.createdAt).toLocaleDateString('fr-FR', {
                          day: '2-digit', month: 'short', year: 'numeric'
                        })}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-xl font-bold text-blue-600">€{order.totalAmount.toFixed(2)}</p>
                      <p className="text-xs text-blue-500">View details →</p>
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>

          {data.totalPages > 1 && (
            <div className="flex justify-center items-center gap-4 mt-8">
              <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0} className="btn-secondary disabled:opacity-40">← Prev</button>
              <span className="text-sm text-gray-600">Page {page + 1} / {data.totalPages}</span>
              <button onClick={() => setPage((p) => p + 1)} disabled={data.last} className="btn-secondary disabled:opacity-40">Next →</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
