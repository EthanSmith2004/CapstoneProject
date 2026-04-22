
export function LoadScreen({ message }: { message?: string }) {
  return (
    <div className="flex items-center justify-center h-screen bg-gray-100 spys-body">
      <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500"></div>
      <div className="ml-4 text-gray-700 text-lg">
        {message}
      </div>
    </div>
  );
}