import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import { Providers } from './providers';
import Navbar from '@/components/Navbar';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'ShopFlow — Modern E-Commerce Platform',
  description: 'A distributed microservices e-commerce platform',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Providers>
          <div className="min-h-screen flex flex-col">
            <Navbar />
            <main className="flex-1">{children}</main>
            <footer className="bg-gray-800 text-gray-400 py-6 text-center text-sm">
              <p>© 2026 ShopFlow. Built with Spring Boot · Kafka · Redis · Next.js</p>
            </footer>
          </div>
        </Providers>
      </body>
    </html>
  );
}
