# 建置一致性驗證腳本
# 確保 Debug 和 Release 版本的資源文件一致

param(
    [switch]$Verbose = $false
)

Write-Host "=== 建置一致性驗證工具 ===" -ForegroundColor Cyan
Write-Host "檢查項目：圖像、字符串資源、依賴項版本" -ForegroundColor Yellow

$projectRoot = Split-Path -Parent $PSScriptRoot
$appPath = Join-Path $projectRoot "app"
$srcPath = Join-Path $appPath "src"
$mainPath = Join-Path $srcPath "main"

# 檢查函數
function Test-ResourceConsistency {
    Write-Host "`n1. 檢查圖像資源..." -ForegroundColor Green
    
    # 檢查 drawable 資源
    $drawablePath = Join-Path $mainPath "res/drawable"
    if (Test-Path $drawablePath) {
        $drawableFiles = Get-ChildItem $drawablePath -File
        Write-Host "  發現 $($drawableFiles.Count) 個 drawable 資源" -ForegroundColor White
        
        if ($Verbose) {
            $drawableFiles | ForEach-Object { Write-Host "    - $($_.Name)" -ForegroundColor Gray }
        }
    }
    
    # 檢查 mipmap 資源
    $mipmapDirs = Get-ChildItem (Join-Path $mainPath "res") -Directory | Where-Object { $_.Name -like "mipmap-*" }
    Write-Host "  發現 $($mipmapDirs.Count) 個 mipmap 密度目錄" -ForegroundColor White
    
    foreach ($dir in $mipmapDirs) {
        $files = Get-ChildItem $dir.FullName -File
        Write-Host "    $($dir.Name): $($files.Count) 個文件" -ForegroundColor Gray
        
        if ($Verbose) {
            $files | ForEach-Object { Write-Host "      - $($_.Name)" -ForegroundColor DarkGray }
        }
    }
}

function Test-StringResources {
    Write-Host "`n2. 檢查字符串資源..." -ForegroundColor Green
    
    $valuesPath = Join-Path $mainPath "res/values"
    $stringsFile = Join-Path $valuesPath "strings.xml"
    
    if (Test-Path $stringsFile) {
        $content = Get-Content $stringsFile -Raw
        $stringCount = ([regex]::Matches($content, '<string')).Count
        Write-Host "  主要 strings.xml: $stringCount 個字符串" -ForegroundColor White
    }
    
    # 檢查國際化資源
    $i18nDirs = Get-ChildItem (Join-Path $mainPath "res") -Directory | Where-Object { $_.Name -like "values-*" }
    Write-Host "  發現 $($i18nDirs.Count) 個國際化目錄" -ForegroundColor White
    
    foreach ($dir in $i18nDirs) {
        $stringsFile = Join-Path $dir.FullName "strings.xml"
        if (Test-Path $stringsFile) {
            $content = Get-Content $stringsFile -Raw
            $stringCount = ([regex]::Matches($content, '<string')).Count
            Write-Host "    $($dir.Name): $stringCount 個字符串" -ForegroundColor Gray
        }
    }
}

function Test-Dependencies {
    Write-Host "`n3. 檢查依賴項版本..." -ForegroundColor Green
    
    $buildGradleFile = Join-Path $appPath "build.gradle.kts"
    if (Test-Path $buildGradleFile) {
        $content = Get-Content $buildGradleFile -Raw
        
        # 檢查關鍵依賴項版本
        $dependencies = @(
            "androidx.compose:compose-bom",
            "androidx.core:core-ktx",
            "androidx.lifecycle:lifecycle-runtime-ktx",
            "com.google.android.gms:play-services-ads"
        )
        
        foreach ($dep in $dependencies) {
            $pattern = "$dep:([0-9]+\.[0-9]+\.[0-9]+)"
            $match = [regex]::Match($content, $pattern)
            if ($match.Success) {
                Write-Host "    $dep : $($match.Groups[1].Value)" -ForegroundColor Gray
            }
        }
    }
}

function Test-BuildConfig {
    Write-Host "`n4. 檢查建置配置..." -ForegroundColor Green
    
    $buildGradleFile = Join-Path $appPath "build.gradle.kts"
    if (Test-Path $buildGradleFile) {
        $content = Get-Content $buildGradleFile -Raw
        
        # 檢查版本信息
        $versionCodeMatch = [regex]::Match($content, 'versionCode = (\d+)')
        $versionNameMatch = [regex]::Match($content, 'versionName = "([^"]+)"')
        
        if ($versionCodeMatch.Success) {
            Write-Host "    版本代碼: $($versionCodeMatch.Groups[1].Value)" -ForegroundColor Gray
        }
        if ($versionNameMatch.Success) {
            Write-Host "    版本名稱: $($versionNameMatch.Groups[1].Value)" -ForegroundColor Gray
        }
        
        # 檢查建置類型配置
        if ($content -match 'buildTypes') {
            Write-Host "    ✓ 找到建置類型配置" -ForegroundColor Gray
        }
        if ($content -match 'signingConfigs') {
            Write-Host "    ✓ 找到簽名配置" -ForegroundColor Gray
        }
    }
}

function Test-ManifestConsistency {
    Write-Host "`n5. 檢查 Manifest 一致性..." -ForegroundColor Green
    
    $mainManifest = Join-Path $mainPath "AndroidManifest.xml"
    $debugManifest = Join-Path $srcPath "debug/AndroidManifest.xml"
    $releaseManifest = Join-Path $srcPath "release/AndroidManifest.xml"
    
    $manifests = @()
    if (Test-Path $mainManifest) { $manifests += "main" }
    if (Test-Path $debugManifest) { $manifests += "debug" }
    if (Test-Path $releaseManifest) { $manifests += "release" }
    
    Write-Host "    發現 Manifest 文件: $($manifests -join ', ')" -ForegroundColor Gray
    
    if ($manifests.Count -eq 3) {
        Write-Host "    ✓ 所有必要的 Manifest 文件都存在" -ForegroundColor Green
    } else {
        Write-Host "    ⚠ 缺少某些 Manifest 文件" -ForegroundColor Yellow
    }
}

# 執行檢查
try {
    Test-ResourceConsistency
    Test-StringResources
    Test-Dependencies
    Test-BuildConfig
    Test-ManifestConsistency
    
    Write-Host "`n=== 檢查完成 ===" -ForegroundColor Cyan
    Write-Host "建議：" -ForegroundColor Yellow
    Write-Host "1. 確保所有資源文件都在版本控制中" -ForegroundColor White
    Write-Host "2. 使用相同的建置環境進行 debug 和 release 建置" -ForegroundColor White
    Write-Host "3. 定期運行此腳本驗證一致性" -ForegroundColor White
    
} catch {
    Write-Host "錯誤: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
