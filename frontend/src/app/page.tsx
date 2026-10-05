import Link from 'next/link';

export default function HomePage() {
  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-blue-600 via-blue-700 to-indigo-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 text-center">
          <div className="mb-6">
            <span className="inline-block bg-blue-500 bg-opacity-40 text-blue-100 text-sm font-semibold px-4 py-1.5 rounded-full uppercase tracking-wider">
              Microservices Architecture · Spring Boot · Kafka · Redis
            </span>
          </div>
          <h1 className="text-5xl md:text-6xl font-extrabold mb-6 leading-tight">
            Shop<span className="text-blue-200">Flow</span>
          </h1>
          <p className="text-xl md:text-2xl text-blue-100 mb-10 max-w-2xl mx-auto">
            The modern distributed e-commerce platform. Fast, secure, and scalable.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              href="/products"
              className="bg-white text-blue-700 font-bold py-3 px-8 rounded-xl hover:bg-blue-50 transition-colors text-lg shadow-lg"
            >
              🛍️ Browse Products
            </Link>
            <Link
              href="/auth/signin"
              className="border-2 border-white text-white font-bold py-3 px-8 rounded-xl hover:bg-white hover:text-blue-700 transition-colors text-lg"
            >
              Sign In
            </Link>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="bg-white border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <dl className="grid grid-cols-1 gap-6 sm:grid-cols-3 text-center">
            {[
              { label: 'Products', value: '10K+', emoji: '📦' },
              { label: 'Happy Customers', value: '50K+', emoji: '😊' },
              { label: 'Uptime', value: '99.9%', emoji: '⚡' },
            ].map((stat) => (
              <div key={stat.label} className="bg-gray-50 rounded-2xl p-6">
                <div className="text-4xl mb-2">{stat.emoji}</div>
                <dd className="text-3xl font-extrabold text-blue-600">{stat.value}</dd>
                <dt className="text-gray-600 mt-1 font-medium">{stat.label}</dt>
              </div>
            ))}
          </dl>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
          Why ShopFlow?
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            {
              emoji: '🚀',
              title: 'Event-Driven Architecture',
              desc: 'Built on Apache Kafka for real-time, async processing of orders, payments, and notifications.',
            },
            {
              emoji: '🔒',
              title: 'Secure by Design',
              desc: 'OAuth2/OIDC with Keycloak, JWT validation at every service boundary, rate limiting on the gateway.',
            },
            {
              emoji: '📊',
              title: 'Full Observability',
              desc: 'Distributed tracing with Jaeger, metrics with Prometheus & Grafana, centralized logging.',
            },
            {
              emoji: '⚡',
              title: 'Redis Caching',
              desc: 'Product catalog cached in Redis for sub-millisecond reads. Cache invalidation on updates.',
            },
            {
              emoji: '🛡️',
              title: 'Resilience4j',
              desc: 'Circuit breakers, retries, and time limiters protect inter-service communication.',
            },
            {
              emoji: '🐳',
              title: 'Docker Ready',
              desc: 'All 15+ services containerized and orchestrated with a single docker-compose up command.',
            },
          ].map((f) => (
            <div key={f.title} className="card hover:shadow-md transition-shadow">
              <div className="text-4xl mb-4">{f.emoji}</div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">{f.title}</h3>
              <p className="text-gray-600 text-sm leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Tech Stack */}
      <section className="bg-gray-800 text-white py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl font-bold mb-8 text-gray-300">Tech Stack</h2>
          <div className="flex flex-wrap justify-center gap-3">
            {[
              'Java 21', 'Spring Boot 3.3', 'Spring Cloud Gateway',
              'Apache Kafka', 'Redis', 'PostgreSQL', 'Keycloak',
              'Resilience4j', 'OpenTelemetry', 'Jaeger', 'Prometheus',
              'Grafana', 'Docker', 'Next.js 14', 'TypeScript',
            ].map((tech) => (
              <span
                key={tech}
                className="bg-gray-700 text-gray-200 px-3 py-1.5 rounded-lg text-sm font-medium"
              >
                {tech}
              </span>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
