import { Routes, Route } from 'react-router-dom'
import { Suspense, lazy } from 'react'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { Layout } from '@/components/Layout'

// Lazy load pages for better performance
const Dashboard = lazy(() => import('@/pages/Dashboard'))
const ApiConnection = lazy(() => import('@/pages/ApiConnection'))
const DescriptionOptimizer = lazy(() => import('@/pages/DescriptionOptimizer'))
const EffectMonitoring = lazy(() => import('@/pages/EffectMonitoring'))
const CompetitorAnalysis = lazy(() => import('@/pages/CompetitorAnalysis'))
const SystemSettings = lazy(() => import('@/pages/SystemSettings'))
const NotFound = lazy(() => import('@/pages/NotFound'))

function App() {
  return (
    <Layout>
      <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/api-connection" element={<ApiConnection />} />
          <Route path="/description-optimizer" element={<DescriptionOptimizer />} />
          <Route path="/effect-monitoring" element={<EffectMonitoring />} />
          <Route path="/competitor-analysis" element={<CompetitorAnalysis />} />
          <Route path="/settings" element={<SystemSettings />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Suspense>
    </Layout>
  )
}

export default App
