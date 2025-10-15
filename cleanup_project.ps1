# WTS Asking for Signature 專案清理腳本
# 安全刪除不必要的檔案和目錄

Write-Host "🧹 開始清理 WTS Asking for Signature 專案..." -ForegroundColor Green

# 定義要刪除的目錄和檔案
$itemsToDelete = @(
    # 舊的設計文檔專案
    "1.1.2APP页面设计示意图",
    
    # 備份目錄
    "backup_2025-08-27_17-18-45",
    "backup_2025-08-27_17-18-45.zip",
    "backup_2025-08-28_14-52-23_before_language_refactor",
    "backup_language_system_2025-08-28_14-29-56",
    "emergency_backup_20250918_0947",
    
    # 測試和上傳包
    "aab_upload_package_0917_1357",
    "hidden_test_v4_0917_1351",
    "upload_package_0917_1346",
    
    # 各種報告和日誌檔案
    "aab_build_report_0917_1357.txt",
    "build_consistency_0917_1336.txt",
    "build_consistency_0917_1339.txt",
    "build_consistency_0917_1345.txt",
    "build_verification_0917_1240.txt",
    "content_verification_0917_1341.txt",
    "csv_validation_0917_1250.txt",
    "cup_userdata_check_0917_1301.txt",
    "deepseek_dependency_report_0917_1253.txt",
    "deepseek_dependency_report_0917_1254.txt",
    "deepseek_dependency_report_0917_1255.txt",
    "encoding_analysis_0917_1335.txt",
    "final_packaging_report_0917_1346.txt",
    "hidden_test_v4_report_0917_1351.txt",
    "inconsistency_analysis_0917_1327.txt",
    "inconsistency_analysis_0917_1331.txt",
    
    # 文檔檔案
    "API_CONFIGURATION.md",
    "APK手動安裝指南.md",
    "BUILD_CONSISTENCY_GUIDE.md",
    "DEEPSEEK_API_CHECKLIST.md",
    "EXTREME_TOPICS_TEST.md",
    "FINAL_SOLUTION_SUMMARY.md",
    "INTERNATIONALIZATION_GUIDE.md",
    "INTERNATIONALIZATION_TESTING_GUIDE.md",
    "LEGAL_DEMONSTRATION.md",
    "LONG_TERM_SOLUTION.md",
    "README_MultiLanguageFortune.md",
    "SENSITIVE_TOPICS_DEMONSTRATION.md",
    "SOLUTION_OPTIONS.md",
    "上架前核心功能测试.md",
    "交付摘要_擲筊界面修復_v4.2.md",
    "快速安裝指南.md",
    "模擬器問題解決指南.md",
    "版本8完成報告.md",
    "版本控制問題解決.md",
    "程式編碼邏輯檢查文件_v4.2.md",
    "第11-20籤更新完成報告.md",
    "第4-10籤更新完成報告.md",
    "籤文1-100全面檢查報告.md",
    "籤文內容模板庫.md",
    "籤文內容補充模板.md",
    "籤文更新完成報告.md",
    "籤文驗證工具使用說明.md",
    "自動跳轉修復說明_v7.1.md",
    "重新開始指南_v7.md",
    "隱閉測試修復報告_v4.1.md",
    "需求文件",
    
    # 測試和工具檔案
    "MyFirstApp",
    "test_api.kt",
    "test_csv_parsing.kt",
    "test_resource_manager.kt",
    "testers_extended.csv",
    "testers.csv",
    "JavaBackendService.java",
    "tatus",
    
    # 密碼和敏感檔案
    "master_password_backup.txt",
    "复制终极密码.docx",
    "太好了.docx",
    
    # Python 腳本
    "快速修復籤文82.py",
    "快速更新籤文.bat",
    "批量籤文修復工具.py",
    "籤文內容驗證工具.py",
    "籤文對比工具.py",
    
    # 其他檔案
    "fix_package_name.ps1",
    "擲筊界面彈出問題修復報告_v4.2.md"
)

# 統計要刪除的項目
$totalItems = $itemsToDelete.Count
$deletedCount = 0
$notFoundCount = 0

Write-Host "📊 準備刪除 $totalItems 個項目..." -ForegroundColor Yellow

foreach ($item in $itemsToDelete) {
    if (Test-Path $item) {
        try {
            if (Test-Path $item -PathType Container) {
                Remove-Item -Path $item -Recurse -Force
                Write-Host "✅ 已刪除目錄: $item" -ForegroundColor Green
            } else {
                Remove-Item -Path $item -Force
                Write-Host "✅ 已刪除檔案: $item" -ForegroundColor Green
            }
            $deletedCount++
        } catch {
            Write-Host "❌ 刪除失敗: $item - $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "⚠️  未找到: $item" -ForegroundColor Yellow
        $notFoundCount++
    }
}

Write-Host "`n📈 清理完成統計:" -ForegroundColor Cyan
Write-Host "   ✅ 成功刪除: $deletedCount 個項目" -ForegroundColor Green
Write-Host "   ⚠️  未找到: $notFoundCount 個項目" -ForegroundColor Yellow
Write-Host "   📊 總計處理: $totalItems 個項目" -ForegroundColor Blue

Write-Host "`n🎉 專案清理完成！現在專案結構更加清晰。" -ForegroundColor Green
Write-Host "💡 建議：定期清理不必要的檔案，保持專案整潔。" -ForegroundColor Cyan
