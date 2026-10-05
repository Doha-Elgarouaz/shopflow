'use client';

import { signIn } from 'next-auth/react';

export default function SignInPage() {
  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card max-w-md w-full text-center">
        {/* Logo */}
        <div className="mb-8">
          <h1 className="text-4xl font-extrabold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent mb-2">
            ShopFlow
          </h1>
          <p className="text-gray-500">Sign in to your account to continue</p>
        </div>

        <button
          onClick={() => signIn('keycloak')}
          className="w-full flex items-center justify-center gap-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-xl transition-colors text-lg"
        >
          <svg viewBox="0 0 24 24" className="w-6 h-6 fill-current">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9V8h2v8zm4 0h-2V8h2v8z"/>
          </svg>
          Continue with Keycloak
        </button>

        <div className="mt-6 p-4 bg-blue-50 rounded-xl text-sm text-blue-700">
          <p className="font-semibold mb-1">Demo credentials:</p>
          <p>👤 <code className="bg-blue-100 px-1 rounded">customer</code> / <code className="bg-blue-100 px-1 rounded">customer123</code></p>
          <p>🔑 <code className="bg-blue-100 px-1 rounded">admin</code> / <code className="bg-blue-100 px-1 rounded">admin123</code></p>
        </div>

        <p className="mt-4 text-xs text-gray-400">
          You will be redirected to Keycloak (port 8180) for secure authentication.
        </p>
      </div>
    </div>
  );
}
