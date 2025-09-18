# DeepSeek API 配置說明

## 概述
本應用使用 DeepSeek API 提供智能解籤服務。為了確保上架後 API 功能正常運作，請按照以下步驟進行配置和測試。

## 配置步驟

### 1. 更新 API 基礎 URL
在 `app/build.gradle.kts` 文件中，將 `API_BASE_URL` 更新為真實的 DeepSeek API 端點：

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://api.deepseek.com/\"")
```

**注意**：請將 `https://api.deepseek.com/` 替換為您實際的 DeepSeek API 端點。

### 2. 配置 API 密鑰（如需要）
如果 DeepSeek API 需要認證，請在 `ServiceLocator.kt` 中添加相應的認證邏輯：

```kotlin
// 在 OkHttpClient.Builder 中添加認證攔截器
.addInterceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer YOUR_API_KEY")
        .build()
    chain.proceed(request)
}
```

### 3. 測試 API 連接
應用已內建測試功能：
- 在首頁點擊「測試 DeepSeek API」按鈕
- 系統會自動測試 API 連接並顯示結果

## API 端點說明

### 主要端點
- `POST /chat` - 解籤對話
- `GET /draw` - 隨機抽籤
- `GET /fortunes/{id}` - 獲取指定籤文
- `GET /cup/result` - 擲杯結果

### 請求格式
```json
{
  "fortuneId": 1,
  "question": "請解讀這支籤的含義"
}
```

### 回應格式
```json
{
  "messages": [
    {
      "role": "assistant",
      "content": "根據籤文內容，這支籤表示..."
    }
  ]
}
```

## 測試清單

### 基本連接測試
- [ ] API 端點可達性
- [ ] 認證機制（如需要）
- [ ] 基本請求/回應

### 功能測試
- [ ] 抽籤功能
- [ ] 解籤對話
- [ ] 錯誤處理
- [ ] 網路超時處理

### 性能測試
- [ ] 回應時間（建議 < 5 秒）
- [ ] 並發請求處理
- [ ] 記憶體使用

## 故障排除

### 常見問題
1. **網路連接失敗**
   - 檢查網路權限
   - 確認 API 端點可達性

2. **認證失敗**
   - 檢查 API 密鑰
   - 確認認證格式

3. **回應超時**
   - 調整網路超時設定
   - 檢查 API 服務狀態

### 日誌查看
應用使用 `WtsLogger` 記錄 API 調用日誌，可在 Logcat 中查看詳細資訊。

## 上架前檢查清單

- [ ] API 端點配置正確
- [ ] 認證機制正常
- [ ] 所有 API 端點測試通過
- [ ] 錯誤處理機制完善
- [ ] 網路權限已添加
- [ ] 離線模式備用方案

## 聯繫支援
如遇到 API 相關問題，請聯繫：
- DeepSeek 技術支援
- 應用開發團隊

---
**重要提醒**：確保在正式上架前完成所有 API 測試，避免用戶體驗問題。
