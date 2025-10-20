import { Outlet, Link, useLocation } from 'react-router-dom'
import { Home, Settings, BarChart3, Zap, Search, Monitor } from 'lucide-react'

export function Layout() {
  const location = useLocation()

  const navigation = [
    { name: '數據儀表板', href: '/dashboard', icon: Home },
    { name: 'API 連接', href: '/api-connection', icon: Zap },
    { name: '描述優化器', href: '/description-optimizer', icon: Search },
    { name: '效果監控', href: '/effect-monitoring', icon: Monitor },
    { name: '競爭分析', href: '/competitor-analysis', icon: BarChart3 },
    { name: '系統設定', href: '/settings', icon: Settings },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">
                DS 黃大仙靈簽 AI Agent
              </h1>
            </div>
            <nav className="hidden md:flex space-x-8">
              {navigation.map((item) => {
                const Icon = item.icon
                const isActive = location.pathname === item.href
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`flex items-center space-x-2 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive
                        ? 'bg-blue-100 text-blue-700'
                        : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                    }`}
                  >
                    <Icon className="h-4 w-4" />
                    <span>{item.name}</span>
                  </Link>
                )
              })}
            </nav>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <Outlet />
      </main>
    </div>
  )
}