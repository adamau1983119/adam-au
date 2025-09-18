# 多語言簽文系統使用指南

## 🎯 系統概述

本系統為您的黃大仙靈簽應用程式提供完整的多語言支援，包括：

- **繁體中文** (TRADITIONAL_CHINESE)
- **簡體中文** (SIMPLIFIED_CHINESE)  
- **英文** (ENGLISH)

## 📁 檔案結構

```
app/src/main/java/com/example/wtsaskingforsignature/
├── data/
│   ├── loader/
│   │   └── MultiLanguageFortuneLoader.kt          # 多語言簽文載入器
│   └── model/
│       └── FortuneStick.kt                        # 簽文數據模型
├── ui/
│   └── components/
│       ├── LanguageSwitcher.kt                    # 語言切換器組件
│       └── FortuneStickDisplay.kt                 # 簽文顯示組件
└── util/
    ├── FortuneStickResourceManager.kt             # 簽文資源管理器
    ├── FortuneSystemTest.kt                       # 系統測試工具
    └── MultiLanguageFortuneUsageGuide.kt          # 使用指南
```

## 🚀 快速開始

### 1. 初始化系統

```kotlin
// 在您的Activity或Fragment中
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化簽文系統
        FortuneStickResourceManager.initialize(this)
    }
}
```

### 2. 切換語言

```kotlin
// 切換到簡體中文
FortuneStickResourceManager.switchLanguage(LanguageManager.Language.SIMPLIFIED_CHINESE)

// 切換到英文
FortuneStickResourceManager.switchLanguage(LanguageManager.Language.ENGLISH)

// 切換到繁體中文
FortuneStickResourceManager.switchLanguage(LanguageManager.Language.TRADITIONAL_CHINESE)
```

### 3. 獲取簽文

```kotlin
// 獲取當前語言的簽文
val fortune = FortuneStickResourceManager.getFortuneStick(1)

// 獲取特定語言的簽文
val fortune = FortuneStickResourceManager.getFortuneStick(1, LanguageManager.Language.ENGLISH)
```

## 🔧 詳細使用方法

### 多語言簽文載入器

```kotlin
val loader = MultiLanguageFortuneLoader(context)

// 載入所有語言的簽文
val allLanguages = loader.loadAllLanguages()

// 載入特定語言的簽文
val traditionalFortunes = loader.loadLanguageFortunes(LanguageManager.Language.TRADITIONAL_CHINESE)
val simplifiedFortunes = loader.loadLanguageFortunes(LanguageManager.Language.SIMPLIFIED_CHINESE)
val englishFortunes = loader.loadLanguageFortunes(LanguageManager.Language.ENGLISH)
```

### 簽文資源管理器

```kotlin
// 初始化
FortuneStickResourceManager.initialize(context)

// 切換語言
FortuneStickResourceManager.switchLanguage(LanguageManager.Language.SIMPLIFIED_CHINESE)

// 獲取簽文
val fortune = FortuneStickResourceManager.getFortuneStick(1)

// 獲取當前語言
val currentLanguage = FortuneStickResourceManager.getCurrentLanguage()

// 監聽語言變化
FortuneStickResourceManager.addLanguageChangeListener { newLanguage ->
    // 處理語言變化
    updateUI(newLanguage)
}
```

### 語言切換器組件

```kotlin
@Composable
fun MyScreen() {
    LanguageSwitcher(
        onLanguageChanged = { newLanguage ->
            // 處理語言切換
            FortuneStickResourceManager.switchLanguage(newLanguage)
        }
    )
}
```

### 簽文顯示組件

```kotlin
@Composable
fun FortuneDisplay(fortuneId: Int) {
    FortuneStickDisplay(
        fortuneId = fortuneId,
        modifier = Modifier.fillMaxSize()
    )
}
```

## 📊 簽文數據結構

### FortuneStick 模型

```kotlin
data class FortuneStick(
    val id: Int,                                    // 簽文編號 (1-100)
    val title: Map<Language, String>,               // 3語言標題
    val poem: Map<Language, String>,                // 3語言籤詩
    val interpretation: Map<Language, String>,      // 3語言解讀
    val fortune: Map<Language, FortuneAnalysis>,    // 3語言運勢分析
    val category: Map<Language, String>,            // 3語言分類
    val summary: Map<Language, String>              // 3語言摘要
)
```

### FortuneAnalysis 模型

```kotlin
data class FortuneAnalysis(
    val marriage: Map<Language, String>,            // 婚姻運
    val career: Map<Language, String>,              // 事業運
    val wealth: Map<Language, String>,              // 財運
    val health: Map<Language, String>,              // 健康運
    val family: Map<Language, String>,              // 家庭運
    val love: Map<Language, String>,                // 愛情運
    val travel: Map<Language, String>,              // 旅行運
    val reputation: Map<Language, String>,          // 名譽運
    val friendship: Map<Language, String>,          // 友誼運
    val studies: Map<Language, String>,             // 學業運
    val yearFortune: Map<Language, String>,         // 流年運
    // ... 其他運勢項目
)
```

## 🧪 測試和驗證

### 系統測試

```kotlin
// 測試多語言簽文系統
FortuneSystemTest.testMultiLanguageSystem(context)

// 測試特定簽文
FortuneSystemTest.testSpecificFortune(context, 1)
```

### 使用指南測試

```kotlin
// 基本使用示例
MultiLanguageFortuneUsageGuide.basicUsageExample(context)

// 預載入所有語言
MultiLanguageFortuneUsageGuide.preloadAllLanguages(context)

// 獲取特定語言內容
val content = MultiLanguageFortuneUsageGuide.getFortuneContent(fortune, LanguageManager.Language.ENGLISH)
```

## 📝 簽文檔案格式

### CSV 檔案結構

您的簽文檔案應包含以下列：

```csv
id,title,summary,content
1,第一籤,上上籤,"籤詩內容..."
2,第二籤,上籤,"籤詩內容..."
...
```

### 支援的檔案

- `fortunes_source.csv` - 繁體中文版本
- `fortunes_cn.csv` - 簡體中文版本 ✅
- `fortunes_en2.csv` - 英文版本 ✅

## ⚠️ 注意事項

1. **初始化順序**: 必須先調用 `FortuneStickResourceManager.initialize(context)`
2. **語言切換**: 語言切換是異步操作，需要等待完成
3. **記憶體管理**: 系統會自動緩存簽文數據，無需手動管理
4. **錯誤處理**: 所有操作都包含完善的錯誤處理機制

## 🔍 故障排除

### 常見問題

1. **簽文載入失敗**
   - 檢查assets目錄中的CSV檔案是否存在
   - 確認CSV檔案格式是否正確
   - 檢查檔案編碼是否為UTF-8

2. **語言切換無效**
   - 確認已調用初始化方法
   - 檢查語言代碼是否正確
   - 查看Logcat中的錯誤信息

3. **UI不更新**
   - 確認已添加語言變化監聽器
   - 檢查Composable函數是否正確重組
   - 驗證狀態管理是否正確

### 調試工具

```kotlin
// 啟用詳細日誌
FortuneStickResourceManager.setDebugMode(true)

// 檢查語言支援狀態
val supportStatus = MultiLanguageFortuneUsageGuide.checkLanguageSupport(context)
Log.d("LanguageSupport", "Support status: $supportStatus")
```

## 📞 技術支援

如果您在使用過程中遇到問題，可以：

1. 查看Logcat中的錯誤信息
2. 使用測試工具驗證系統功能
3. 檢查檔案路徑和格式
4. 確認所有必要的權限已設置

## 🎉 總結

您的多語言簽文系統現在已經完全準備就緒！系統支援：

- ✅ 3種語言版本
- ✅ 即時語言切換
- ✅ 智能緩存管理
- ✅ 完整的簽文內容
- ✅ 多語言UI支援
- ✅ 完善的錯誤處理

祝您使用愉快！🎊
