# Real Module Test Script
# Test if the new modules actually work in the existing app

Write-Host "=== REAL MODULE TEST ===" -ForegroundColor Green
Write-Host ""

# Check if APK is compiled
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    Write-Host "APK compiled: $apkPath" -ForegroundColor Green
} else {
    Write-Host "APK not found, please compile first" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== TEST INSTRUCTIONS ===" -ForegroundColor Yellow
Write-Host "1. Run the app in Android Studio"
Write-Host "2. Go to any fortune (e.g., Fortune #40)"
Write-Host "3. Click 'Deep Seek解籤' button"
Write-Host "4. You will see TWO buttons: '測試' and '發送'"
Write-Host "5. Click '測試' button to test if new modules work"
Write-Host "6. Check the result in the chat interface"
Write-Host ""

Write-Host "=== EXPECTED TEST RESULTS ===" -ForegroundColor Cyan
Write-Host "If new modules work:"
Write-Host "  - Should see '調用成功'"
Write-Host "  - Should see '包含籤文ID: true'"
Write-Host "  - Should see '包含時間分析: true'"
Write-Host "  - Should see '包含專業術語: true'"
Write-Host ""
Write-Host "If new modules DON'T work:"
Write-Host "  - Will see '調用失敗' or '測試執行失敗'"
Write-Host "  - Will fall back to old system"
Write-Host ""

Write-Host "=== LOG CHECK ===" -ForegroundColor Yellow
Write-Host "Use this command to view logs:"
Write-Host "adb logcat | findstr 'WtsLogger'"
Write-Host ""
Write-Host "Look for these log messages:"
Write-Host "  - 'LocalAIRepository: 開始增強分析'"
Write-Host "  - 'DeepSeekInterpreter: 開始解籤'"
Write-Host "  - 'QuestionAnalyzer: 分析問題'"
Write-Host "  - 'FortuneDataIntegrator: 讀取籤文'"
Write-Host ""

Write-Host "=== REAL TEST READY ===" -ForegroundColor Green
Write-Host "This will prove if the new modules actually work!"
