# 🧠 DeepSeek解籤功能優化提示詞

## 📋 任務描述

你是一位 Android App 工程師，負責優化「DS黃大仙靈籤」App 中的 DeepSeek 解籤功能。請根據以下限制與目標進行開發：

## 🔒 修改限制

1. **僅限修改 DeepSeek 解籤模組**，不得改動其他模組（如紫微排盤、UI流程、用戶資料結構）
2. **所有資料結構不可刪除或重構**（如 ZiweiAnalysis、QuestionAnalysis、PersonalizedResponse），僅可擴充
3. **所有修改需封裝在 `DeepSeekInterpreter` 類別中**，其他模組透過此類別取得回覆
4. **保持現有API接口不變**，確保向後兼容性

## 🧩 功能目標

### 1. 問題語意分析 → 命盤邏輯連接
- 根據 NLP 分析結果，觸發對應紫微宮位與星曜模組
- 建立「問題類型 → 宮位星曜」的映射表
- 確保每個問題都能對應具體命理解釋

**具體要求：**
```kotlin
// 必須實現的映射邏輯
class QuestionToZiweiMapper {
    fun mapQuestionToZiwei(analysis: QuestionAnalysis): ZiweiMapping {
        return when (analysis.category) {
            QuestionCategory.CAREER -> ZiweiMapping(
                primaryPalaces = listOf(PalaceType.OFFICIAL, PalaceType.MING),
                keyStars = listOf(StarType.HUA_LU, StarType.TIAN_JI)
            )
            QuestionCategory.LOVE -> ZiweiMapping(
                primaryPalaces = listOf(PalaceType.SPOUSE, PalaceType.CHILDREN),
                keyStars = listOf(StarType.HUA_KE, StarType.TAO_HUA)
            )
            // ... 其他分類
        }
    }
}
```

### 2. 命盤數據 → 語意映射
- 建立「星曜組合 → 解釋語句」的模板邏輯
- 例如：官祿宮 + 化祿 → "事業運勢良好，有升職機會"
- 回覆需具備語氣風格（鼓勵式、警示式等）

**具體要求：**
```kotlin
// 必須實現的語意映射
class ZiweiSemanticMapper {
    fun generateInterpretation(
        palace: PalaceAnalysis, 
        context: AnalysisContext
    ): String {
        return when {
            palace.hasHuaLu() -> "事業運勢良好，有升職機會"
            palace.hasHuaJi() -> "事業上可能遇到阻力，需要謹慎"
            else -> "事業發展需要耐心，穩步前進"
        }
    }
}
```

### 3. DeepSeek 回覆 → 語料反饋機制
- 收集高評分回覆 → 提取語意結構 → 儲存為本地模板
- 建立學習循環 → 持續優化 → 降低 API 依賴

**具體要求：**
```kotlin
// 必須實現的學習機制
class FeedbackLearningSystem {
    fun processHighQualityResponse(
        response: String,
        rating: Float,
        context: AnalysisContext
    ) {
        if (rating > 4.5f) {
            val patterns = extractResponsePatterns(response)
            updateLocalTemplates(patterns, context)
        }
    }
}
```

## ⚠️ 技術要求

### 模組接口標準
- 所有模組需實作 `AnalysisModule` 接口
- 使用統一的 `AnalysisContext` 作為資料容器
- 每個模組需具備錯誤處理與回退邏輯

### 錯誤處理機制
```kotlin
// 必須實現的錯誤處理
class ModuleErrorHandler {
    fun processWithFallback(module: AnalysisModule, context: AnalysisContext): AnalysisResult {
        return try {
            module.process(context)
        } catch (e: Exception) {
            when (module) {
                is QuestionAnalyzer -> useKeywordMatchingFallback(context)
                is ZiweiCalculator -> useDefaultZiweiExplanation(context)
                is ResponseGenerator -> useTemplateFallback(context)
                else -> createErrorResult(e)
            }
        }
    }
}
```

### 性能要求
- 回覆生成時間 < 3 秒
- NLP分析時間 < 800ms
- 資料一致性需通過驗收測試

## 🏗️ 核心架構要求

### DeepSeekInterpreter 類別設計
```kotlin
class DeepSeekInterpreter {
    // 主要入口點
    suspend fun interpretFortune(
        question: String,
        userProfile: UserProfile,
        ziweiData: ZiweiAnalysis
    ): PersonalizedResponse
    
    // 內部模組
    private val questionAnalyzer: QuestionAnalyzer
    private val ziweiMapper: QuestionToZiweiMapper
    private val semanticMapper: ZiweiSemanticMapper
    private val responseGenerator: ResponseGenerator
    private val learningSystem: FeedbackLearningSystem
}
```

### 模組間協作流程
1. **問題分析**：QuestionAnalyzer 分析用戶問題
2. **命盤映射**：QuestionToZiweiMapper 將問題映射到命盤
3. **語意生成**：ZiweiSemanticMapper 生成命理解釋
4. **回答組合**：ResponseGenerator 組合最終回答
5. **學習反饋**：FeedbackLearningSystem 處理用戶反饋

## ✅ 驗收標準

### 功能驗收
- [ ] 回覆是否針對問題類型與命盤資料生成
- [ ] 回覆語氣是否符合用戶特徵
- [ ] 回覆是否具備命理解釋力與實用性
- [ ] 所有修改是否封裝於 DeepSeekInterpreter 類別中

### 技術驗收
- [ ] 模組間協作正常
- [ ] 錯誤處理機制有效
- [ ] 性能指標達標
- [ ] 資料一致性通過測試

### 質量驗收
- [ ] 回答相關性 > 4.0/5.0
- [ ] 用戶滿意度 > 4.5/5.0
- [ ] 個性化程度 > 80%
- [ ] 命理解釋準確性 > 90%

## 🚀 實施步驟

### 步驟一：建立核心架構
1. 創建 `DeepSeekInterpreter` 類別
2. 實現 `AnalysisModule` 接口
3. 建立 `AnalysisContext` 資料容器

### 步驟二：實現映射邏輯
1. 實現 `QuestionToZiweiMapper`
2. 實現 `ZiweiSemanticMapper`
3. 建立語氣適配機制

### 步驟三：建立學習系統
1. 實現 `FeedbackLearningSystem`
2. 建立本地模板庫
3. 實現持續學習機制

### 步驟四：測試與優化
1. 單元測試
2. 整合測試
3. 性能測試
4. 用戶驗收測試

## 📝 輸出要求

請提供以下內容：

1. **完整的 Kotlin 程式架構**
2. **模組互動流程圖**
3. **錯誤處理策略**
4. **性能優化方案**
5. **測試計劃**

## ⚠️ 注意事項

- 所有修改必須在 `DeepSeekInterpreter` 類別內完成
- 不得修改現有的資料結構定義
- 確保向後兼容性
- 重視錯誤處理和降級策略
- 專注於提升回覆品質和實用性

---

**使用說明：** 將此提示詞直接提供給 Cursor，它將專注於 DeepSeek 解籤功能的優化，而不會影響 App 的其他部分。
