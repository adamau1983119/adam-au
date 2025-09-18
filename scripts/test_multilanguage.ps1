# 多语言功能测试脚本
# 测试WTS灵签APP的5种语言支持

Write-Host "🌍 WTS灵签APP多语言功能测试" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# 检查语言资源文件
Write-Host "📁 检查语言资源文件..." -ForegroundColor Yellow

$resourceDirs = @(
    "app/src/main/res/values",
    "app/src/main/res/values-zh-rTW", 
    "app/src/main/res/values-zh-rCN",
    "app/src/main/res/values-ja",
    "app/src/main/res/values-ko"
)

$languages = @{
    "values" = "English (英文)"
    "values-zh-rTW" = "繁體中文 (Traditional Chinese)"
    "values-zh-rCN" = "简体中文 (Simplified Chinese)" 
    "values-ja" = "日本語 (Japanese)"
    "values-ko" = "한국어 (Korean)"
}

Write-Host "`n🔍 语言资源目录检查:" -ForegroundColor Green
foreach ($dir in $resourceDirs) {
    if (Test-Path $dir) {
        $stringsFile = Join-Path $dir "strings.xml"
        if (Test-Path $stringsFile) {
            $dirName = $dir.Split('\')[-1]
            $languageName = $languages[$dirName]
            Write-Host "  ✅ $languageName - $dir" -ForegroundColor Green
        } else {
            $dirName = $dir.Split('\')[-1]
            $languageName = $languages[$dirName]
            Write-Host "  ❌ $languageName - strings.xml 缺失" -ForegroundColor Red
        }
    } else {
        Write-Host "  ❌ $dir 目录不存在" -ForegroundColor Red
    }
}

# 检查关键字符串翻译
Write-Host "`n📝 关键字符串翻译检查:" -ForegroundColor Yellow

$keyStrings = @(
    "app_name",
    "main_title", 
    "settings",
    "language",
    "direct_draw",
    "cup_draw",
    "ai_interpretation"
)

foreach ($dir in $resourceDirs) {
    $stringsFile = Join-Path $dir "strings.xml"
    if (Test-Path $stringsFile) {
        $dirName = $dir.Split('\')[-1]
        $languageName = $languages[$dirName]
        Write-Host "`n  🌐 $languageName:" -ForegroundColor Cyan
        
        foreach ($key in $keyStrings) {
            $content = Get-Content $stringsFile -Raw
            if ($content -match "name=`"$key`".*?>(.*?)</string>") {
                $translation = $matches[1]
                Write-Host "    ✅ $key`: $translation" -ForegroundColor Green
            } else {
                Write-Host "    ❌ $key`: 缺失翻译" -ForegroundColor Red
            }
        }
    }
}

# 检查语言管理器代码
Write-Host "`n🔧 语言管理器代码检查:" -ForegroundColor Yellow

$languageManagerFile = "app/src/main/java/com/example/wtsaskingforsignature/util/LanguageManager.kt"
if (Test-Path $languageManagerFile) {
    Write-Host "  ✅ LanguageManager.kt 存在" -ForegroundColor Green
    
    $content = Get-Content $languageManagerFile -Raw
    if ($content -match "enum class Language") {
        Write-Host "  ✅ Language枚举类定义完整" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Language枚举类定义不完整" -ForegroundColor Red
    }
    
    if ($content -match "setLanguage") {
        Write-Host "  ✅ setLanguage方法存在" -ForegroundColor Green
    } else {
        Write-Host "  ❌ setLanguage方法缺失" -ForegroundColor Red
    }
} else {
    Write-Host "  ❌ LanguageManager.kt 缺失" -ForegroundColor Red
}

# 检查语言设置界面
Write-Host "`n🎨 语言设置界面检查:" -ForegroundColor Yellow

$languageSettingsFile = "app/src/main/java/com/example/wtsaskingforsignature/ui/screens/LanguageSettingsScreen.kt"
if (Test-Path $languageSettingsFile) {
    Write-Host "  ✅ LanguageSettingsScreen.kt 存在" -ForegroundColor Green
    
    $content = Get-Content $languageSettingsFile -Raw
    if ($content -match "LanguageSettingsScreen") {
        Write-Host "  ✅ 语言设置界面组件定义完整" -ForegroundColor Green
    } else {
        Write-Host "  ❌ 语言设置界面组件定义不完整" -ForegroundColor Red
    }
} else {
    Write-Host "  ❌ LanguageSettingsScreen.kt 缺失" -ForegroundColor Red
}

# 检查导航配置
Write-Host "`n🧭 导航配置检查:" -ForegroundColor Yellow

$navigationFiles = @(
    "app/src/main/java/com/example/wtsaskingforsignature/ui/screens/Screens.kt",
    "app/src/main/java/com/example/wtsaskingforsignature/ui/navigation/Navigation.kt"
)

foreach ($file in $navigationFiles) {
    if (Test-Path $file) {
        $fileName = Split-Path $file -Leaf
        Write-Host "  ✅ $fileName 存在" -ForegroundColor Green
    } else {
        $fileName = Split-Path $file -Leaf
        Write-Host "  ❌ $fileName 缺失" -ForegroundColor Red
    }
}

# 编译测试
Write-Host "`n🔨 编译测试:" -ForegroundColor Yellow

try {
    Write-Host "  正在执行编译测试..." -ForegroundColor White
    $buildResult = & ./gradlew assembleDebug --quiet
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ 编译成功 - 多语言功能代码无语法错误" -ForegroundColor Green
    } else {
        Write-Host "  ❌ 编译失败 - 请检查代码错误" -ForegroundColor Red
        Write-Host "  编译输出: $buildResult" -ForegroundColor Red
    }
} catch {
    Write-Host "  ❌ 编译测试执行失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 总结报告
Write-Host "`n📊 多语言功能测试总结:" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

Write-Host "`n🎯 测试项目:" -ForegroundColor Yellow
Write-Host "  • 语言资源文件完整性" -ForegroundColor White
Write-Host "  • 关键字符串翻译" -ForegroundColor White  
Write-Host "  • 语言管理器代码" -ForegroundColor White
Write-Host "  • 语言设置界面" -ForegroundColor White
Write-Host "  • 导航配置" -ForegroundColor White
Write-Host "  • 编译测试" -ForegroundColor White

Write-Host "`n🚀 下一步建议:" -ForegroundColor Yellow
Write-Host "  1. 在设备上测试语言切换功能" -ForegroundColor White
Write-Host "  2. 验证所有界面文字正确显示" -ForegroundColor White
Write-Host "  3. 测试语言偏好保存功能" -ForegroundColor White
Write-Host "  4. 检查不同语言下的界面布局" -ForegroundColor White

Write-Host "`n💡 使用提示:" -ForegroundColor Yellow
Write-Host "  • 在APP中: 设置 → 语言 → 选择语言" -ForegroundColor White
Write-Host "  • 支持语言: 英文、繁體中文、简体中文、日本語、한국어" -ForegroundColor White
Write-Host "  • 语言切换后建议重启APP以确保完全生效" -ForegroundColor White

Write-Host "`n✅ 多语言功能测试完成！" -ForegroundColor Green
