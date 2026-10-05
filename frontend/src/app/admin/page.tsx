'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Order, Product, ApiPage } from '@/lib/types';
import OrderStatusBadge from '@/components/OrderStatusBadge';

export default function AdminDashboardPage() {
  const { data: session, status } = useSession();
  const router = useRouter();

  const [activeTab, setActiveTab] = useState<'products' | 'orders' | 'services'>('products');
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // New product form state
  const [newProduct, setNewProduct] = useState({
    name: '',
    description: '',
    sku: '',
    price: '',
    stockQuantity: '',
    category: 'Electronics',
  });

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/auth/signin');
    }
  }, [status, router]);

  useEffect(() => {
    if (session) {
      loadData();
    }
  }, [session, activeTab]);

  const loadData = async () => {
    setIsLoading(true);
    setMessage(null);
    try {
      if (activeTab === 'products') {
        const res = await api.get<ApiPage<Product>>('/api/products?page=0&size=50');
        setProducts(res.data.content);
      } else if (activeTab === 'orders') {
        const res = await api.get<ApiPage<Order>>('/api/orders?page=0&size=50');
        setOrders(res.data.content);
      }
    } catch (err: any) {
      console.error(err);
      setMessage({
        type: 'error',
        text: 'Failed to load data. Ensure you are signed in with an ADMIN account (admin@shopflow.com).',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/products', {
        name: newProduct.name,
        description: newProduct.description,
        sku: newProduct.sku,
        price: parseFloat(newProduct.price),
        stockQuantity: parseInt(newProduct.stockQuantity, 10),
        category: newProduct.category,
      });

      setMessage({ type: 'success', text: 'Product created successfully!' });
      setNewProduct({
        name: '',
        description: '',
        sku: '',
        price: '',
        stockQuantity: '',
        category: 'Electronics',
      });
      loadData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Error creating product' });
    }
  };

  const handleUpdateStock = async (productId: number, newStock: number) => {
    try {
      await api.patch(`/api/products/${productId}/stock`, null, {
        params: { quantity: newStock },
      });
      setMessage({ type: 'success', text: `Stock updated for product #${productId}` });
      loadData();
    } catch (err: any) {
      setMessage({ type: 'error', text: 'Failed to update stock.' });
    }
  };

  const handleUpdateOrderStatus = async (orderId: number, newStatus: string) => {
    try {
      await api.patch(`/api/orders/${orderId}/status`, `"${newStatus}"`, {
        headers: { 'Content-Type': 'application/json' },
      });
      setMessage({ type: 'success', text: `Order #${orderId} status changed to ${newStatus}` });
      loadData();
    } catch (err: any) {
      setMessage({ type: 'error', text: 'Failed to update order status.' });
    }
  };

  if (status === 'loading') {
    return <div className="text-center py-20 text-gray-500">Loading admin panel...</div>;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4 border-b pb-6">
        <div>
          <div className="flex items-center gap-2">
            <span className="badge bg-purple-100 text-purple-800 text-xs uppercase font-bold">Admin Panel</span>
            <h1 className="text-3xl font-extrabold text-gray-900">ShopFlow Management</h1>
          </div>
          <p className="text-gray-500 text-sm mt-1">
            Logged in as <span className="font-semibold text-gray-700">{session?.user?.email}</span> (ROLE_ADMIN access)
          </p>
        </div>

        {/* Tab switches */}
        <div className="flex gap-2 bg-gray-100 p-1 rounded-xl">
          {(['products', 'orders', 'services'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-2 rounded-lg text-sm font-semibold capitalize transition-all ${
                activeTab === tab ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {tab === 'services' ? '⚡ Infrastructure' : tab}
            </button>
          ))}
        </div>
      </div>

      {message && (
        <div
          className={`p-4 rounded-xl mb-6 text-sm flex justify-between items-center ${
            message.type === 'success' ? 'bg-green-50 text-green-800 border border-green-200' : 'bg-red-50 text-red-800 border border-red-200'
          }`}
        >
          <span>{message.text}</span>
          <button onClick={() => setMessage(null)} className="font-bold ml-4">✕</button>
        </div>
      )}

      {/* ─── TAB 1: PRODUCTS & STOCK ─── */}
      {activeTab === 'products' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Add product form */}
          <div className="card h-fit">
            <h2 className="text-lg font-bold text-gray-900 mb-4">➕ Add New Product</h2>
            <form onSubmit={handleCreateProduct} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Name</label>
                <input
                  required
                  type="text"
                  placeholder="e.g. AirPods Max"
                  value={newProduct.name}
                  onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                  className="input text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">SKU</label>
                <input
                  required
                  type="text"
                  placeholder="e.g. APPLE-AIRPODS-MAX"
                  value={newProduct.sku}
                  onChange={(e) => setNewProduct({ ...newProduct, sku: e.target.value })}
                  className="input text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Price (€)</label>
                  <input
                    required
                    type="number"
                    step="0.01"
                    placeholder="549.99"
                    value={newProduct.price}
                    onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
                    className="input text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">Stock</label>
                  <input
                    required
                    type="number"
                    placeholder="25"
                    value={newProduct.stockQuantity}
                    onChange={(e) => setNewProduct({ ...newProduct, stockQuantity: e.target.value })}
                    className="input text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Category</label>
                <select
                  value={newProduct.category}
                  onChange={(e) => setNewProduct({ ...newProduct, category: e.target.value })}
                  className="input text-sm"
                >
                  <option value="Electronics">Electronics</option>
                  <option value="Clothing">Clothing</option>
                  <option value="Books">Books</option>
                  <option value="Home">Home</option>
                  <option value="Sports">Sports</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Description</label>
                <textarea
                  rows={2}
                  placeholder="Short description..."
                  value={newProduct.description}
                  onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
                  className="input text-sm"
                />
              </div>

              <button type="submit" className="btn-primary w-full text-sm py-2">
                Create Product (POST /api/products)
              </button>
            </form>
          </div>

          {/* Product list with stock editor */}
          <div className="lg:col-span-2 card">
            <h2 className="text-lg font-bold text-gray-900 mb-4">Inventory & Product Catalog</h2>
            {isLoading ? (
              <p className="text-gray-400 text-sm">Loading catalog...</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead className="bg-gray-50 text-gray-600 text-xs uppercase">
                    <tr>
                      <th className="p-3">ID</th>
                      <th className="p-3">Product</th>
                      <th className="p-3">Price</th>
                      <th className="p-3">Stock</th>
                      <th className="p-3">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {products.map((p) => (
                      <tr key={p.id} className="hover:bg-gray-50">
                        <td className="p-3 font-mono text-xs">{p.id}</td>
                        <td className="p-3">
                          <div className="font-semibold text-gray-900">{p.name}</div>
                          <div className="text-xs text-gray-400">{p.sku} · {p.category}</div>
                        </td>
                        <td className="p-3 font-bold text-blue-600">€{p.price.toFixed(2)}</td>
                        <td className="p-3">
                          <span
                            className={`badge ${
                              p.stockQuantity === 0
                                ? 'bg-red-100 text-red-800'
                                : p.stockQuantity < 5
                                ? 'bg-orange-100 text-orange-800'
                                : 'bg-green-100 text-green-800'
                            }`}
                          >
                            {p.stockQuantity} in stock
                          </span>
                        </td>
                        <td className="p-3">
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => handleUpdateStock(p.id, p.stockQuantity + 10)}
                              className="px-2 py-1 bg-gray-100 hover:bg-gray-200 text-xs rounded font-medium"
                              title="Restock +10"
                            >
                              +10
                            </button>
                            <button
                              onClick={() => handleUpdateStock(p.id, Math.max(0, p.stockQuantity - 5))}
                              className="px-2 py-1 bg-gray-100 hover:bg-gray-200 text-xs rounded font-medium"
                              title="Decrease -5"
                            >
                              -5
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ─── TAB 2: GLOBAL ORDERS ─── */}
      {activeTab === 'orders' && (
        <div className="card">
          <h2 className="text-lg font-bold text-gray-900 mb-4">All Customer Orders (Global View)</h2>
          {isLoading ? (
            <p className="text-gray-400 text-sm">Loading orders...</p>
          ) : orders.length === 0 ? (
            <p className="text-gray-500 py-8 text-center">No orders placed yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-gray-50 text-gray-600 text-xs uppercase">
                  <tr>
                    <th className="p-3">Order #</th>
                    <th className="p-3">Customer</th>
                    <th className="p-3">Date</th>
                    <th className="p-3">Total</th>
                    <th className="p-3">Status</th>
                    <th className="p-3">Change Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {orders.map((o) => (
                    <tr key={o.id} className="hover:bg-gray-50">
                      <td className="p-3 font-mono font-bold text-xs">{o.orderNumber}</td>
                      <td className="p-3 text-xs">{o.customerEmail || o.customerId}</td>
                      <td className="p-3 text-xs text-gray-500">
                        {new Date(o.createdAt).toLocaleDateString()}
                      </td>
                      <td className="p-3 font-bold text-blue-600">€{o.totalAmount.toFixed(2)}</td>
                      <td className="p-3">
                        <OrderStatusBadge status={o.status} />
                      </td>
                      <td className="p-3">
                        <select
                          value={o.status}
                          onChange={(e) => handleUpdateOrderStatus(o.id, e.target.value)}
                          className="text-xs border rounded p-1"
                        >
                          <option value="PENDING">PENDING</option>
                          <option value="CONFIRMED">CONFIRMED</option>
                          <option value="PAID">PAID</option>
                          <option value="SHIPPED">SHIPPED</option>
                          <option value="DELIVERED">DELIVERED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* ─── TAB 3: INFRASTRUCTURE & QUICK LINKS ─── */}
      {activeTab === 'services' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[
            { name: 'Grafana Dashboards', port: '3001', path: '', desc: 'JVM, HTTP latency & system metrics', icon: '📊' },
            { name: 'Jaeger Distributed Traces', port: '16686', path: '', desc: 'End-to-end traceId spans across microservices', icon: '🔍' },
            { name: 'Kafka UI', port: '8090', path: '', desc: 'Inspect topics: order-events, payment-events, stock-events', icon: '📨' },
            { name: 'Keycloak IAM Admin', port: '8180', path: '/admin', desc: 'Realm shopflow: roles, clients, users', icon: '🔐' },
            { name: 'Prometheus Targets', port: '9090', path: '/targets', desc: 'Actuator scrape endpoints status', icon: '🎯' },
            { name: 'Product Service Swagger', port: '8081', path: '/swagger-ui/index.html', desc: 'Interactive OpenAPI docs', icon: '📖' },
            { name: 'Order Service Swagger', port: '8082', path: '/swagger-ui/index.html', desc: 'Interactive OpenAPI docs (Saga)', icon: '📖' },
            { name: 'Payment Service Swagger', port: '8083', path: '/swagger-ui/index.html', desc: 'Interactive OpenAPI docs (Idempotency)', icon: '📖' },
            { name: 'Spring Cloud Gateway', port: '8080', path: '/actuator/gateway/routes', desc: 'Dynamic routes & rate-limiting filters', icon: '🌐' },
          ].map((svc) => (
            <a
              key={svc.name}
              href={`http://localhost:${svc.port}${svc.path}`}
              target="_blank"
              rel="noopener noreferrer"
              className="card hover:shadow-md hover:border-blue-300 transition-all flex flex-col justify-between"
            >
              <div>
                <div className="text-3xl mb-2">{svc.icon}</div>
                <h3 className="font-bold text-gray-900 text-base">{svc.name}</h3>
                <p className="text-xs text-gray-500 mt-1">{svc.desc}</p>
              </div>
              <div className="mt-4 pt-3 border-t flex justify-between items-center text-xs font-mono text-blue-600">
                <span>localhost:{svc.port}</span>
                <span>Open ↗</span>
              </div>
            </a>
          ))}
        </div>
      )}
    </div>
  );
}
