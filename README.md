# WTSaskingforsignature

本專案為 Jetpack Compose（Kotlin）Android App 範例，對應 `需求文件` 及 `1.1.2APP页面设计示意图`。

## 功能頁面（對應檔案）
- 首頁：`HomeScreen`
- 直接抽籤：`DirectDrawScreen`
- 摘杯抽籤：`CupDrawScreen`
- 每日一籤：`DailyScreen`
- 瀏覽籤文：`BrowseScreen`
- 選擇占卜類別：`CategoryScreen`
- 求籤步驟：`StepsScreen`
- 簽文預覽（含擲杯入口）：`PreviewScreen`
- 擲杯：`CupScreen`
- 擲杯結果：`CupResultScreen`
- 籤文內容：`ContentScreen`
- 對話界面：`ChatScreen`

## 開發環境
- Android Studio Ladybug+ / AGP 8.6+ / Kotlin 1.9.24
- compileSdk 34, minSdk 24

## 開啟與執行
1. 以 Android Studio 開啟此資料夾。
2. 同步 Gradle，等待依賴下載完畢。
3. 選擇 `app` 執行配置，點擊 Run。

## 說明
- 畫面為示意與導覽骨架，邏輯（例如擲杯結果、每日一籤限制、DeepSeek 對接）以假資料/簡易狀態示意。
- 可依後端 API 替換 `CupScreen`、`CupResultScreen`、`ContentScreen`、`ChatScreen` 的資料來源與互動邏輯。
