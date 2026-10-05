'use client';

import { useSearchParams } from 'next/navigation';
import Link from 'next/link';

const ERROR_MESSAGES: Record<string, string> = {
  Configuration: 'There is a problem with the server configuration.',
  AccessDenied: 'You do not have permission to sign in.',
  Verification: 'The sign-in link is no longer valid.',
  OAuthSignin: 'Could not connect to Keycloak. Make sure Keycloak is running on port 8180.',
  OAuthCallback: 'Error during Keycloak callback. Check your Keycloak configuration.',
  Default: 'An unexpected authentication error occurred.',
};

export default function AuthErrorPage() {
  const searchParams = useSearchParams();
  const error = searchParams.get('error') ?? 'Default';
  const message = ERROR_MESSAGES[error] ?? ERROR_MESSAGES.Default;

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card max-w-md w-full text-center">
        <div className="text-5xl mb-4">⚠️</div>
        <h1 className="text-2xl font-bold text-red-600 mb-2">Authentication Error</h1>
        <p className="text-gray-600 mb-2">{message}</p>
        <code className="block text-xs text-gray-400 bg-gray-50 rounded px-3 py-2 mb-6">
          Error code: {error}
        </code>

        <div className="bg-blue-50 rounded-xl p-4 text-sm text-blue-700 text-left mb-6">
          <p className="font-semibold mb-2">Checklist:</p>
          <ul className="space-y-1 list-disc list-inside">
            <li>Keycloak is running at <code className="bg-blue-100 px-1 rounded">localhost:8180</code></li>
            <li>Realm <code className="bg-blue-100 px-1 rounded">shopflow</code> is imported</li>
            <li>Client <code className="bg-blue-100 px-1 rounded">shopflow-frontend</code> exists</li>
            <li><code className="bg-blue-100 px-1 rounded">.env.local</code> is configured</li>
          </ul>
        </div>

        <div className="flex gap-3 justify-center">
          <Link href="/auth/signin" className="btn-primary">Try Again</Link>
          <Link href="/" className="btn-secondary">Go Home</Link>
        </div>
      </div>
    </div>
  );
}
