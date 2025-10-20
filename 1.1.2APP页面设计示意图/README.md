# DS黃大仙靈簽 AI Agent WebApp

基於 Google Play Console 數據優化 App Store 簡介的 AI Agent WebApp 管理介面。

## 🚀 功能特色

- **數據儀表板**：實時顯示 Google Play Console 數據
- **簡介優化**：AI 驅動的 App Store 簡介優化
- **A/B 測試**：科學的版本對比測試
- **效果監控**：轉換率和評分監控
- **競品分析**：市場競爭分析
- **系統設定**：完整的系統配置管理

## 🛠️ 技術棧

### 前端
- **React 18** - 現代化 UI 框架
- **TypeScript** - 類型安全
- **Vite** - 快速構建工具
- **Tailwind CSS** - 實用優先的 CSS 框架
- **React Query** - 數據獲取和狀態管理
- **React Router** - 路由管理
- **Chart.js** - 數據視覺化
- **Framer Motion** - 動畫庫

### 後端 API
- **Google Play Console API** - App 數據獲取
- **Play Integrity API** - 安全檢測
- **Firebase Analytics** - 用戶行為分析
- **Google Analytics** - 網站分析

## 📦 安裝與運行

### 環境要求
- Node.js >= 16.0.0
- npm >= 8.0.0

### 安裝依賴
```bash
npm install
```

### 環境配置
1. 複製環境變數範例文件：
```bash
cp env.example .env
```

2. 配置環境變數：
```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8000/api
VITE_GOOGLE_PLAY_CONSOLE_API_URL=https://androidpublisher.googleapis.com
VITE_PLAY_INTEGRITY_API_URL=https://playintegrity.googleapis.com

# Google Play Console API
VITE_GOOGLE_PLAY_CONSOLE_PROJECT_ID=your-project-id
VITE_GOOGLE_PLAY_CONSOLE_SERVICE_ACCOUNT_EMAIL=your-service-account@your-project.iam.gserviceaccount.com
VITE_GOOGLE_PLAY_CONSOLE_PRIVATE_KEY=your-private-key

# Play Integrity API
VITE_PLAY_INTEGRITY_API_KEY=your-play-integrity-api-key
```

### 開發模式
```bash
npm run dev
```

### 構建生產版本
```bash
npm run build
```

### 預覽生產版本
```bash
npm run preview
```

## 🧪 測試

### 運行測試
```bash
npm run test
```

### 測試覆蓋率
```bash
npm run test:coverage
```

### 測試 UI
```bash
npm run test:ui
```

## 🔧 開發工具

### 代碼檢查
```bash
npm run lint
```

### 自動修復
```bash
npm run lint:fix
```

### 類型檢查
```bash
npm run type-check
```

## 📁 項目結構

```
src/
├── components/          # 可重用組件
│   ├── ui/             # 基礎 UI 組件
│   ├── charts/         # 圖表組件
│   ├── forms/          # 表單組件
│   └── layout/         # 佈局組件
├── pages/              # 頁面組件
│   ├── Dashboard.tsx
│   ├── ApiConnection.tsx
│   ├── DescriptionOptimizer.tsx
│   ├── EffectMonitoring.tsx
│   ├── CompetitorAnalysis.tsx
│   └── SystemSettings.tsx
├── hooks/              # 自定義 Hooks
├── services/           # API 服務
├── store/              # 狀態管理
├── utils/              # 工具函數
├── types/              # TypeScript 類型定義
├── assets/             # 靜態資源
├── App.tsx             # 主應用組件
├── main.tsx            # 應用入口
└── index.css           # 全局樣式
```

## 🎨 設計系統

### 色彩系統
- **主色調**：深藍色 (#1E40AF)
- **輔助色**：綠色 (#10B981)
- **警告色**：橙色 (#F59E0B)
- **錯誤色**：紅色 (#EF4444)

### 字體系統
- **字體**：Inter
- **標題**：18px, 粗體
- **副標題**：16px, 中等
- **正文**：14px, 常規
- **說明**：12px, 細體

### 間距系統
- **小間距**：8px
- **中間距**：16px
- **大間距**：24px
- **超大間距**：32px

## 📱 響應式設計

### 斷點
- **手機**：320px - 768px
- **平板**：768px - 1024px
- **桌面**：1024px+

### 設計原則
- **Mobile First**：手機優先設計
- **觸控優化**：按鈕最小 44px
- **內容優先**：重要資訊優先顯示
- **載入優化**：圖片懶加載，代碼分割

## 🔐 安全設計

### 數據安全
- **前端安全**：敏感資料不暴露
- **API 安全**：JWT 認證，HTTPS 傳輸
- **權限控制**：基於角色的存取控制
- **隱私保護**：最小資料收集原則

### 環境變數
- 所有敏感資訊使用環境變數
- 不在代碼中硬編碼 API 金鑰
- 使用 `.env` 文件管理配置

## 🚀 部署

### 構建優化
- **代碼分割**：按路由和功能分割
- **懶加載**：頁面和組件懶加載
- **壓縮**：Gzip 壓縮
- **快取**：靜態資源快取

### 部署環境
- **開發環境**：localhost:3000
- **測試環境**：staging.example.com
- **生產環境**：app.example.com

## 📊 監控與分析

### 錯誤追蹤
- **Sentry**：錯誤監控和追蹤
- **Console**：開發環境錯誤日誌

### 性能監控
- **Web Vitals**：核心網頁指標
- **Lighthouse**：性能評分
- **Bundle Analyzer**：打包分析

### 用戶分析
- **Google Analytics**：用戶行為分析
- **Firebase Analytics**：App 使用分析

## 🤝 貢獻指南

### 代碼規範
- 使用 TypeScript
- 遵循 ESLint 規則
- 使用 Prettier 格式化
- 編寫單元測試

### 提交規範
- feat: 新功能
- fix: 修復問題
- docs: 文檔更新
- style: 代碼格式
- refactor: 重構
- test: 測試相關
- chore: 構建過程或輔助工具的變動

## 📄 許可證

MIT License

## 📞 支援

如有問題或建議，請聯繫開發團隊。

---

**DS黃大仙靈簽 AI Agent WebApp** - 讓數據驅動 App 優化決策
