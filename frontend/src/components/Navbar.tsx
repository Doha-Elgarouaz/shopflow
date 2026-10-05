'use client';

import { useSession, signOut, signIn } from 'next-auth/react';
import Link from 'next/link';
import { useCartStore } from '@/store/cartStore';
import { ShoppingCartIcon, UserCircleIcon } from '@heroicons/react/24/outline';
import { useState } from 'react';

export default function Navbar() {
  const { data: session } = useSession();
  const totalItems = useCartStore((s) => s.totalItems);
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-2">
            <span className="text-2xl font-extrabold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
              ShopFlow
            </span>
          </Link>

          {/* Desktop Nav */}
          <div className="hidden md:flex items-center gap-6">
            <Link href="/products" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">
              Products
            </Link>
            {session && (
              <Link href="/orders" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">
                My Orders
              </Link>
            )}
            {session?.user?.email?.toLowerCase().includes('admin') && (
              <Link href="/admin" className="text-purple-600 hover:text-purple-800 font-bold transition-colors flex items-center gap-1 bg-purple-50 px-2.5 py-1 rounded-lg">
                🛡️ Admin
              </Link>
            )}
          </div>

          {/* Right side */}
          <div className="flex items-center gap-4">
            {/* Cart */}
            <Link href="/cart" className="relative p-2 text-gray-600 hover:text-blue-600 transition-colors">
              <ShoppingCartIcon className="h-6 w-6" />
              {totalItems() > 0 && (
                <span className="absolute -top-1 -right-1 bg-blue-600 text-white text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center">
                  {totalItems()}
                </span>
              )}
            </Link>

            {/* Auth */}
            {session ? (
              <div className="flex items-center gap-3">
                <div className="hidden md:flex items-center gap-2 text-sm text-gray-700">
                  <UserCircleIcon className="h-5 w-5 text-gray-400" />
                  <span className="font-medium">{session.user?.email}</span>
                </div>
                <button
                  onClick={() => signOut()}
                  className="btn-secondary text-sm py-1.5 px-3"
                >
                  Sign Out
                </button>
              </div>
            ) : (
              <button
                onClick={() => signIn('keycloak')}
                className="btn-primary text-sm py-1.5 px-4"
              >
                Sign In
              </button>
            )}

            {/* Mobile menu button */}
            <button
              className="md:hidden p-2 text-gray-600"
              onClick={() => setMenuOpen(!menuOpen)}
            >
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d={menuOpen ? 'M6 18L18 6M6 6l12 12' : 'M4 6h16M4 12h16M4 18h16'} />
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {menuOpen && (
          <div className="md:hidden border-t border-gray-100 py-3 space-y-2">
            <Link href="/products" className="block px-2 py-2 text-gray-700 hover:text-blue-600 font-medium">
              Products
            </Link>
            {session && (
              <Link href="/orders" className="block px-2 py-2 text-gray-700 hover:text-blue-600 font-medium">
                My Orders
              </Link>
            )}
            {session?.user?.email?.toLowerCase().includes('admin') && (
              <Link href="/admin" className="block px-2 py-2 text-purple-700 font-bold hover:text-purple-900">
                🛡️ Admin Dashboard
              </Link>
            )}
          </div>
        )}
      </div>
    </nav>
  );
}
