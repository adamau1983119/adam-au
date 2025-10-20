export default function CompetitorAnalysis() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">競爭分析</h1>
        <p className="mt-1 text-sm text-gray-500">
          分析競爭對手的 App Store 描述和策略，找出優化機會
        </p>
      </div>
      
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            競爭對手分析
          </h3>
          <div className="mt-4 space-y-4">
            <div className="border rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-medium text-gray-900">靈簽大師</h4>
                  <p className="text-sm text-gray-500">評分：4.5/5 | 下載量：8,234</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-600">關鍵字：靈簽、占卜、運勢</p>
                </div>
              </div>
            </div>
            
            <div className="border rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-medium text-gray-900">香港靈簽</h4>
                  <p className="text-sm text-gray-500">評分：4.3/5 | 下載量：5,678</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-600">關鍵字：香港、傳統、文化</p>
                </div>
              </div>
            </div>
            
            <div className="border rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-medium text-gray-900">黃大仙靈簽</h4>
                  <p className="text-sm text-gray-500">評分：4.7/5 | 下載量：12,345</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-600">關鍵字：黃大仙、準確、靈驗</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            AI 分析建議
          </h3>
          <div className="mt-4 space-y-4">
            <div className="p-4 bg-blue-50 rounded-lg">
              <h4 className="font-medium text-blue-900">機會點</h4>
              <p className="text-sm text-blue-700 mt-1">
                競爭對手較少使用「準確」、「靈驗」等情感詞彙，這是我們的優勢
              </p>
            </div>
            <div className="p-4 bg-yellow-50 rounded-lg">
              <h4 className="font-medium text-yellow-900">改進建議</h4>
              <p className="text-sm text-yellow-700 mt-1">
                可以參考「黃大仙靈簽」的成功策略，強調準確性和靈驗度
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}