// API Response Types
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message?: string
  error?: string
}

// Google Play Console API Types
export interface GooglePlayConsoleData {
  downloads: DownloadData[]
  ratings: RatingData[]
  reviews: ReviewData[]
  crashes: CrashData[]
  revenue: RevenueData[]
}

export interface DownloadData {
  date: string
  downloads: number
  installs: number
  uninstalls: number
  updates: number
}

export interface RatingData {
  date: string
  averageRating: number
  totalRatings: number
  ratingDistribution: {
    '1': number
    '2': number
    '3': number
    '4': number
    '5': number
  }
}

export interface ReviewData {
  id: string
  date: string
  rating: number
  comment: string
  language: string
  sentiment: 'positive' | 'negative' | 'neutral'
}

export interface CrashData {
  date: string
  crashes: number
  anrs: number
  crashRate: number
  anrRate: number
}

export interface RevenueData {
  date: string
  revenue: number
  currency: string
  transactions: number
}

// Play Integrity API Types
export interface PlayIntegrityData {
  deviceIntegrity: {
    score: number
    status: 'MEETS_BASIC_INTEGRITY' | 'MEETS_DEVICE_INTEGRITY' | 'MEETS_STRONG_INTEGRITY'
  }
  accountIntegrity: {
    score: number
    status: 'MEETS_BASIC_INTEGRITY' | 'MEETS_DEVICE_INTEGRITY' | 'MEETS_STRONG_INTEGRITY'
  }
  recentActivity: {
    score: number
    status: 'MEETS_BASIC_INTEGRITY' | 'MEETS_DEVICE_INTEGRITY' | 'MEETS_STRONG_INTEGRITY'
  }
}

// App Store Description Types
export interface AppStoreDescription {
  id: string
  title: string
  subtitle: string
  description: string
  keywords: string[]
  version: string
  createdAt: string
  updatedAt: string
  performance: {
    conversionRate: number
    rating: number
    ranking: number
  }
}

export interface DescriptionOptimization {
  id: string
  originalDescription: AppStoreDescription
  optimizedDescription: AppStoreDescription
  testResults: {
    conversionRate: number
    rating: number
    downloads: number
    improvement: number
  }
  status: 'draft' | 'testing' | 'completed' | 'failed'
  createdAt: string
  updatedAt: string
}

// A/B Testing Types
export interface ABTest {
  id: string
  name: string
  description: string
  variants: {
    A: AppStoreDescription
    B: AppStoreDescription
  }
  trafficSplit: {
    A: number
    B: number
  }
  status: 'draft' | 'running' | 'completed' | 'paused'
  startDate: string
  endDate?: string
  results: {
    A: TestResults
    B: TestResults
    winner?: 'A' | 'B'
    confidence: number
  }
  createdAt: string
  updatedAt: string
}

export interface TestResults {
  conversionRate: number
  rating: number
  downloads: number
  views: number
  clicks: number
}

// Competitor Analysis Types
export interface Competitor {
  id: string
  name: string
  packageName: string
  downloads: number
  rating: number
  reviews: number
  ranking: number
  lastUpdated: string
}

export interface CompetitorAnalysis {
  id: string
  competitors: Competitor[]
  keywords: KeywordAnalysis[]
  marketShare: MarketShareData[]
  trends: TrendData[]
  createdAt: string
  updatedAt: string
}

export interface KeywordAnalysis {
  keyword: string
  ourRanking: number
  competitorRankings: Record<string, number>
  searchVolume: number
  competition: 'low' | 'medium' | 'high'
  opportunity: number
}

export interface MarketShareData {
  competitor: string
  share: number
  trend: 'up' | 'down' | 'stable'
}

export interface TrendData {
  date: string
  downloads: number
  rating: number
  ranking: number
}

// Effect Monitoring Types
export interface EffectMonitoring {
  id: string
  metric: string
  value: number
  previousValue: number
  change: number
  changePercentage: number
  trend: 'up' | 'down' | 'stable'
  status: 'good' | 'warning' | 'critical'
  timestamp: string
}

export interface Alert {
  id: string
  type: 'conversion_rate' | 'rating' | 'downloads' | 'ranking' | 'crashes'
  severity: 'low' | 'medium' | 'high' | 'critical'
  message: string
  value: number
  threshold: number
  status: 'active' | 'acknowledged' | 'resolved'
  createdAt: string
  updatedAt: string
}

// System Settings Types
export interface UserSettings {
  id: string
  language: string
  timezone: string
  theme: 'light' | 'dark' | 'auto'
  notifications: {
    email: boolean
    push: boolean
    alerts: boolean
  }
  dataRetention: number
  syncFrequency: number
  createdAt: string
  updatedAt: string
}

export interface ApiConnection {
  id: string
  name: string
  type: 'google_play_console' | 'play_integrity' | 'firebase' | 'google_analytics'
  status: 'connected' | 'disconnected' | 'error'
  lastSync: string
  config: Record<string, any>
  createdAt: string
  updatedAt: string
}

// Chart Data Types
export interface ChartData {
  labels: string[]
  datasets: {
    label: string
    data: number[]
    backgroundColor?: string | string[]
    borderColor?: string | string[]
    borderWidth?: number
    fill?: boolean
  }[]
}

export interface TimeSeriesData {
  date: string
  value: number
  label?: string
}

// Form Types
export interface FormField {
  name: string
  label: string
  type: 'text' | 'email' | 'password' | 'number' | 'select' | 'textarea' | 'checkbox' | 'radio'
  required?: boolean
  placeholder?: string
  options?: { value: string; label: string }[]
  validation?: {
    min?: number
    max?: number
    pattern?: string
    message?: string
  }
}

// Navigation Types
export interface NavItem {
  id: string
  label: string
  href: string
  icon: string
  badge?: number
  children?: NavItem[]
}

// Loading States
export interface LoadingState {
  isLoading: boolean
  error?: string
  data?: any
}

// Pagination
export interface Pagination {
  page: number
  limit: number
  total: number
  totalPages: number
}

export interface PaginatedResponse<T> {
  data: T[]
  pagination: Pagination
}

// Error Types
export interface AppError {
  code: string
  message: string
  details?: Record<string, any>
  timestamp: string
}

// Theme Types
export interface Theme {
  name: string
  colors: {
    primary: string
    secondary: string
    success: string
    warning: string
    danger: string
    background: string
    surface: string
    text: string
  }
}

// Device Types
export interface Device {
  id: string
  name: string
  type: 'mobile' | 'tablet' | 'desktop'
  lastActive: string
  status: 'online' | 'offline'
  userAgent: string
  ipAddress: string
}

// Export Types
export interface ExportOptions {
  format: 'csv' | 'excel' | 'pdf' | 'json'
  dateRange: {
    start: string
    end: string
  }
  includeCharts: boolean
  includeCompetitors: boolean
  language: string
  timezone: string
}
