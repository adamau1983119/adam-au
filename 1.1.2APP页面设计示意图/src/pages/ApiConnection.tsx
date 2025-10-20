export default function ApiConnection() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">API 連接</h1>
        <p className="mt-1 text-sm text-gray-500">
          管理 Google Play Console 和 Play Integrity API 連接
        </p>
      </div>
      
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Google Play Console API
          </h3>
          <div className="mt-2 max-w-xl text-sm text-gray-500">
            <p>連接狀態：<span className="text-green-600 font-medium">已連接</span></p>
            <p>最後更新：2024-01-15 14:30</p>
          </div>
          <div className="mt-5">
            <button className="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700">
              重新連接
            </button>
          </div>
        </div>
      </div>
      
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Play Integrity API
          </h3>
          <div className="mt-2 max-w-xl text-sm text-gray-500">
            <p>連接狀態：<span className="text-green-600 font-medium">已連接</span></p>
            <p>API 金鑰：AIzaSyDSgstr7km_iFsUJokIfv8F3S9ERWzWad8</p>
          </div>
          <div className="mt-5">
            <button className="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700">
              測試連接
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}