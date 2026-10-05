export default function LoadingSkeleton({ count = 8 }: { count?: number }) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="bg-white rounded-xl border border-gray-200 overflow-hidden animate-pulse">
          <div className="h-48 bg-gray-200" />
          <div className="p-4 space-y-3">
            <div className="h-3 bg-gray-200 rounded w-1/3" />
            <div className="h-4 bg-gray-200 rounded w-3/4" />
            <div className="h-3 bg-gray-200 rounded w-1/4" />
            <div className="flex justify-between items-center mt-4">
              <div className="h-6 bg-gray-200 rounded w-1/3" />
              <div className="h-6 bg-gray-200 rounded w-1/4" />
            </div>
            <div className="h-9 bg-gray-200 rounded-lg" />
          </div>
        </div>
      ))}
    </div>
  );
}
