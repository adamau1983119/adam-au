export default function Dashboard() {
  return (
    <div className="space-y-8">
      {/* 歡迎區域 */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl p-8 text-white">
        <h1 className="text-3xl font-bold mb-2">歡迎使用 DS 黃大仙靈簽 AI Agent</h1>
        <p className="text-blue-100 text-lg">
          智能優化您的 App Store 描述，提升下載量和用戶轉換率
        </p>
      </div>
      
      {/* 快速操作卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-blue-500">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <span className="text-2xl">📱</span>
            </div>
            <div className="ml-4">
              <h3 className="text-lg font-semibold text-gray-900">描述優化</h3>
              <p className="text-sm text-gray-500">AI 智能優化</p>
            </div>
          </div>
          <p className="text-gray-600 text-sm mb-4">
            使用 AI 技術優化您的 App Store 描述，吸引更多目標用戶
          </p>
          <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
            開始優化
          </button>
        </div>
        
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-green-500">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <span className="text-2xl">📊</span>
            </div>
            <div className="ml-4">
              <h3 className="text-lg font-semibold text-gray-900">數據分析</h3>
              <p className="text-sm text-gray-500">實時監控</p>
            </div>
          </div>
          <p className="text-gray-600 text-sm mb-4">
            監控下載量、評分和用戶反饋，了解優化效果
          </p>
          <button className="w-full bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors">
            查看數據
          </button>
        </div>
        
        <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-purple-500">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <span className="text-2xl">🔍</span>
            </div>
            <div className="ml-4">
              <h3 className="text-lg font-semibold text-gray-900">競爭分析</h3>
              <p className="text-sm text-gray-500">市場洞察</p>
            </div>
          </div>
          <p className="text-gray-600 text-sm mb-4">
            分析競爭對手策略，找出優化機會
          </p>
          <button className="w-full bg-purple-600 text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition-colors">
            開始分析
          </button>
        </div>
      </div>
      
      {/* 數據統計 */}
      <div className="bg-white rounded-xl shadow-lg p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">關鍵指標</h2>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-600 mb-2">12,345</div>
            <div className="text-sm text-gray-500">總下載量</div>
            <div className="text-xs text-green-600 mt-1">↗ +12.5%</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-green-600 mb-2">4.8</div>
            <div className="text-sm text-gray-500">平均評分</div>
            <div className="text-xs text-green-600 mt-1">↗ +0.3</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-yellow-600 mb-2">1,234</div>
            <div className="text-sm text-gray-500">日活躍用戶</div>
            <div className="text-xs text-green-600 mt-1">↗ +8.2%</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-purple-600 mb-2">$2,345</div>
            <div className="text-sm text-gray-500">月收入</div>
            <div className="text-xs text-green-600 mt-1">↗ +15.3%</div>
          </div>
        </div>
      </div>
    </div>
  )
}