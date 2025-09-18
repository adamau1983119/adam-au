# Git Pre-commit 檢查腳本
# 確保提交前的代碼質量

Write-Host "=== Pre-commit 檢查 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()

# 1. 檢查建置配置一致性
Write-Host "`n1. 檢查建置配置..." -ForegroundColor Green
$verifyScript = Join-Path $PSScriptRoot "verify_build_consistency.ps1"
if (Test-Path $verifyScript) {
    try {
        & $verifyScript -Verbose:$false
    } catch {
        $errors += "建置配置檢查失敗"
    }
} else {
    $errors += "找不到建置一致性檢查腳本"
}

# 2. 檢查重要文件是否存在
Write-Host "`n2. 檢查重要文件..." -ForegroundColor Green
$requiredFiles = @(
    "app/build.gradle.kts",
    "app/proguard-rules.pro",
    "app/src/main/AndroidManifest.xml",
    "app/src/debug/AndroidManifest.xml",
    "app/src/release/AndroidManifest.xml",
    "wts-release-key.keystore"
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path $file)) {
        $errors += "缺少重要文件: $file"
        Write-Host "    ⚠ 缺少: $file" -ForegroundColor Yellow
    } else {
        Write-Host "    ✓ 存在: $file" -ForegroundColor Gray
    }
}

# 3. 檢查版本號一致性
Write-Host "`n3. 檢查版本號..." -ForegroundColor Green
$buildGradleFile = "app/build.gradle.kts"
if (Test-Path $buildGradleFile) {
    $content = Get-Content $buildGradleFile -Raw
    $versionCodeMatch = [regex]::Match($content, 'versionCode = (\d+)')
    $versionNameMatch = [regex]::Match($content, 'versionName = "([^"]+)"')
    
    if ($versionCodeMatch.Success -and $versionNameMatch.Success) {
        Write-Host "    ✓ 版本代碼: $($versionCodeMatch.Groups[1].Value)" -ForegroundColor Gray
        Write-Host "    ✓ 版本名稱: $($versionNameMatch.Groups[1].Value)" -ForegroundColor Gray
    } else {
        $errors += "無法解析版本信息"
    }
}

# 4. 檢查資源文件
Write-Host "`n4. 檢查資源文件..." -ForegroundColor Green
$resourceDirs = @(
    "app/src/main/res/drawable",
    "app/src/main/res/values",
    "app/src/main/assets"
)

foreach ($dir in $resourceDirs) {
    if (Test-Path $dir) {
        $fileCount = (Get-ChildItem $dir -Recurse -File).Count
        Write-Host "    ✓ $dir : $fileCount 個文件" -ForegroundColor Gray
    } else {
        $errors += "資源目錄不存在: $dir"
    }
}

# 5. 檢查 ProGuard 配置
Write-Host "`n5. 檢查 ProGuard 配置..." -ForegroundColor Green
$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $content = Get-Content $proguardFile -Raw
    if ($content -match "com\.wts\.dsfortune") {
        Write-Host "    ✓ ProGuard 包名配置正確" -ForegroundColor Gray
    } else {
        $errors += "ProGuard 包名配置錯誤"
    }
} else {
    $errors += "ProGuard 配置文件不存在"
}

# 結果報告
Write-Host "`n=== 檢查結果 ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "✓ 所有檢查通過，可以提交" -ForegroundColor Green
    exit 0
} else {
    Write-Host "發現 $($errors.Count) 個問題：" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    Write-Host "`n請修復以上問題後再提交" -ForegroundColor Yellow
    exit 1
}
