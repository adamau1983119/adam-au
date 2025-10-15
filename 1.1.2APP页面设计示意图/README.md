# 黃大仙靈簽 Android APP v1.1.3

一個整合 Play Integrity API 的傳統占卜應用程式，提供安全的抽籤和擲杯功能。

**最低支援版本：Android 4.4 (API 19)**

## 功能特色

### 主要功能
- **直接抽籤**：快速抽籤，可依所求事項分類
- **摘杯抽籤**：以擲杯驗證籤意的抽籤流程（強制擲杯）
- **每日一籤**：每天限一次的簽文抽取
- **瀏覽籤文**：瀏覽1~100號所有籤文

### 安全特性
- **Play Integrity API 整合**：確保應用程式在正版Android設備上運行
- **完整性驗證**：在關鍵操作前驗證應用程式完整性
- **防濫用保護**：防止惡意用戶重複抽籤或操縱結果
- **後端驗證**：籤文隨機性和每日限制由後端控制

## 技術架構

### 前端技術
- **Kotlin**：主要開發語言
- **Jetpack Compose**：現代化UI框架
- **Material Design 3**：遵循Google設計規範
- **Navigation Component**：頁面導航管理
- **ViewModel + StateFlow**：狀態管理

### 安全技術
- **Play Integrity API**：應用程式完整性驗證
- **Google Play Services**：Google服務整合
- **ProGuard**：程式碼混淆保護
- **後端API驗證**：伺服器端安全驗證

### 網路技術
- **Retrofit**：HTTP客戶端
- **OkHttp**：網路請求處理
- **Gson**：JSON序列化
- **Coroutines**：異步處理

## Play Integrity API 整合

### 整合目的
1. **防止濫用**：避免惡意用戶重複抽籤或操縱結果
2. **保護商業邏輯**：確保籤文隨機性和每日一籤限制
3. **防止逆向工程**：保護籤文內容和AI對話功能
4. **確保真實性**：驗證APP在正版Android設備上運行

### 實作細節

#### 1. PlayIntegrityManager
```kotlin
class PlayIntegrityManager(private val context: Context) {
    suspend fun requestIntegrityToken(nonce: String): String?
    suspend fun verifyIntegrity(operation: String): IntegrityResult
    fun isPlayIntegrityAvailable(): Boolean
}
```

#### 2. IntegrityVerificationService
```kotlin
class IntegrityVerificationService(private val context: Context) {
    suspend fun initializeIntegrity()
    suspend fun verifyOperation(operation: String): Boolean
    fun getStatusDescription(): String
}
```

#### 3. 驗證流程
1. 應用程式啟動時初始化完整性驗證
2. 在關鍵操作前調用 `verifyOperation()`
3. 生成隨機數並請求完整性令牌
4. 將令牌發送到後端進行驗證
5. 根據驗證結果決定是否允許操作

### 配置要求

#### 1. Google Cloud Console 設定
- 建立 Google Cloud 專案
- 啟用 Play Integrity API
- 取得 API 金鑰和專案編號

#### 2. Google Play Console 設定
- 在 Play Console 中設定 Play Integrity API
- 連結 Google Cloud 專案
- 設定應用程式簽名

#### 3. 應用程式配置
```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.play.integrity.API_KEY"
    android:value="@string/play_integrity_api_key" />
```

## 專案結構

```
app/
├── src/main/java/com/wongtaisim/lingqian/
│   ├── integrity/                    # Play Integrity API 整合
│   │   ├── PlayIntegrityManager.kt
│   │   └── IntegrityVerificationService.kt
│   ├── data/                         # 資料層
│   │   ├── model/                    # 資料模型
│   │   ├── repository/               # 資料庫存庫
│   │   └── service/                  # API服務
│   ├── ui/                          # UI層
│   │   ├── screens/                 # 畫面
│   │   ├── viewmodel/               # ViewModel
│   │   ├── navigation/              # 導航
│   │   └── theme/                   # 主題
│   └── MainActivity.kt
├── src/test/                        # 單元測試
└── build.gradle
```

## 安裝和設定

### 1. 環境要求
- Android Studio Arctic Fox 或更新版本
- Android SDK API 19 或更高（最低支援Android 4.4）
- Kotlin 1.9.10 或更高
- Google Play Services（Play Integrity API需要API 21+）

### 2. 依賴項目
```gradle
implementation 'com.google.android.play:integrity:1.2.0'
implementation 'com.google.android.gms:play-services-auth:20.7.0'
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
```

### 3. 設定步驟
1. 複製專案到本地
2. 在 Google Cloud Console 設定 Play Integrity API
3. 更新 `strings.xml` 中的 API 金鑰
4. 設定後端 API 網址
5. 編譯並執行

## 安全性考量

### 1. 應用程式完整性
- 使用 Play Integrity API 驗證應用程式完整性
- 防止應用程式被篡改或逆向工程
- 確保在正版 Android 設備上運行

### 2. 資料保護
- 敏感資料使用 ProGuard 混淆
- 網路請求使用 HTTPS
- 本地資料加密存儲

### 3. 業務邏輯保護
- 籤文隨機性由後端控制
- 每日一籤限制由後端驗證
- 擲杯結果由後端驗證

## 測試

### 單元測試
```bash
./gradlew test
```

### 整合測試
```bash
./gradlew connectedAndroidTest
```

### Play Integrity API 測試
- 在 Google Play Console 中設定測試帳戶
- 使用內部測試軌道進行測試
- 驗證完整性驗證功能

## 部署

### 1. 簽名配置
- 使用 Google Play 應用程式簽名
- 設定發布金鑰
- 配置 ProGuard 規則

### 2. 發布流程
1. 在 Google Play Console 建立應用程式
2. 上傳簽名的 APK/AAB
3. 設定 Play Integrity API
4. 提交審核

## 維護和更新

### 1. 定期更新
- 更新 Play Integrity API 版本
- 更新依賴項目
- 修復安全漏洞

### 2. 監控
- 監控完整性驗證失敗率
- 監控應用程式崩潰率
- 監控用戶反饋

## 授權

本專案採用 MIT 授權條款。

## 聯絡資訊

如有問題或建議，請聯絡開發團隊。

---

**注意**：本專案僅供學習和參考用途，實際部署前請確保遵守相關法律法規和平台政策。
