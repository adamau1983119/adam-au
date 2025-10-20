import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full text-center">
        <div className="mb-8">
          <h1 className="text-9xl font-bold text-gray-300">404</h1>
          <h2 className="text-2xl font-bold text-gray-900 mt-4">頁面未找到</h2>
          <p className="text-gray-600 mt-2">
            抱歉，您訪問的頁面不存在或已被移動。
          </p>
        </div>
        
        <div className="space-y-4">
          <Link
            to="/dashboard"
            className="inline-block bg-blue-600 text-white px-6 py-3 rounded-md font-medium hover:bg-blue-700 transition-colors"
          >
            返回首頁
          </Link>
          
          <div className="text-sm text-gray-500">
            <p>或者嘗試以下連結：</p>
            <div className="mt-2 space-x-4">
              <Link to="/api-connection" className="text-blue-600 hover:underline">
                API 連接
              </Link>
              <Link to="/description-optimizer" className="text-blue-600 hover:underline">
                描述優化器
              </Link>
              <Link to="/effect-monitoring" className="text-blue-600 hover:underline">
                效果監控
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}