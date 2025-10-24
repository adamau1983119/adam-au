# Version 21 安全標籤化工作流程

param(
    [string]$Action = "start"
)

switch ($Action) {
    "start" {
        Write-Host "🚀 開始 Version 21 標籤化工作..."
        
        # 1. 備份當前文件
        Write-Host "📦 步驟1: 創建備份..."
        .\scripts\backup_fortunes_v21.ps1
        
        # 2. 創建工作文件
        Write-Host "📝 步驟2: 創建工作文件..."
        $workingFile = "app\src\main\assets\fortunes_source_v21_working.csv"
        Copy-Item "app\src\main\assets\fortunes_source_v21.csv" $workingFile
        Write-Host "✅ 工作文件已創建: $workingFile"
        
        # 3. 顯示當前狀態
        Write-Host "📊 當前狀態:"
        $content = Get-Content $workingFile -Head 5
        $hasTags = $content -match '\[A[1-3]\]|\[[B-P]\]'
        if ($hasTags) {
            Write-Host "🏷️ 工作文件包含標籤"
        } else {
            Write-Host "⚠️ 工作文件無標籤，需要添加"
        }
        
        Write-Host ""
        Write-Host "📋 下一步操作:"
        Write-Host "1. 編輯工作文件: $workingFile"
        Write-Host "2. 完成後運行: .\scripts\safe_tagging_workflow.ps1 -Action save"
        Write-Host "3. 測試通過後運行: .\scripts\safe_tagging_workflow.ps1 -Action deploy"
    }
    
    "save" {
        Write-Host "💾 保存階段性工作..."
        
        # 生成版本號
        $version = Get-Date -Format "yyyyMMdd_HHmmss"
        $versionFile = "app\src\main\assets\fortunes_source_v21_v$version.csv"
        
        # 保存工作文件為版本文件
        Copy-Item "app\src\main\assets\fortunes_source_v21_working.csv" $versionFile
        Write-Host "✅ 版本文件已保存: $versionFile"
        
        # 顯示統計信息
        $content = Get-Content $versionFile
        $taggedLines = ($content | Select-String '\[A[1-3]\]|\[[B-P]\]').Count
        Write-Host "🏷️ 標籤行數: $taggedLines"
    }
    
    "deploy" {
        Write-Host "🚀 部署到主文件..."
        
        # 1. 最後備份
        .\scripts\backup_fortunes_v21.ps1
        
        # 2. 複製工作文件到主文件
        Copy-Item "app\src\main\assets\fortunes_source_v21_working.csv" "app\src\main\assets\fortunes_source_v21.csv"
        Write-Host "✅ 主文件已更新"
        
        # 3. 編譯測試
        Write-Host "🔨 編譯測試..."
        .\gradlew assembleDebug
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ 編譯成功！"
        } else {
            Write-Host "❌ 編譯失敗，請檢查文件"
        }
    }
    
    "status" {
        Write-Host "📊 Version 21 文件狀態:"
        
        $mainFile = "app\src\main\assets\fortunes_source_v21.csv"
        $workingFile = "app\src\main\assets\fortunes_source_v21_working.csv"
        
        if (Test-Path $mainFile) {
            $mainInfo = Get-Item $mainFile
            Write-Host "📁 主文件: $($mainInfo.Name) ($($mainInfo.Length) bytes)"
        }
        
        if (Test-Path $workingFile) {
            $workingInfo = Get-Item $workingFile
            Write-Host "📝 工作文件: $($workingInfo.Name) ($($workingInfo.Length) bytes)"
        } else {
            Write-Host "⚠️ 工作文件不存在"
        }
    }
}
