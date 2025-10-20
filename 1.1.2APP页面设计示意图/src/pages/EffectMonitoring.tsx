export default function EffectMonitoring() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">效果監控</h1>
        <p className="mt-1 text-sm text-gray-500">
          監控描述優化後的下載量、評分和用戶反饋變化
        </p>
      </div>
      
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="bg-white shadow rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              下載量趨勢
            </h3>
            <div className="mt-4">
              <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
                <p className="text-gray-500">圖表區域 - 下載量變化趨勢</p>
              </div>
            </div>
          </div>
        </div>
        
        <div className="bg-white shadow rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              評分變化
            </h3>
            <div className="mt-4">
              <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
                <p className="text-gray-500">圖表區域 - 評分變化趨勢</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            用戶反饋分析
          </h3>
          <div className="mt-4 space-y-4">
            <div className="border-l-4 border-green-400 bg-green-50 p-4">
              <div className="flex">
                <div className="ml-3">
                  <p className="text-sm text-green-700">
                    <span className="font-medium">正面反饋：</span>
                    描述更清晰，更容易理解應用程式的功能
                  </p>
                </div>
              </div>
            </div>
            <div className="border-l-4 border-yellow-400 bg-yellow-50 p-4">
              <div className="flex">
                <div className="ml-3">
                  <p className="text-sm text-yellow-700">
                    <span className="font-medium">建議改進：</span>
                    可以加入更多關於靈簽準確性的說明
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}