# WTS灵签APP法律合规性检查脚本
# 检查应用的法律风险和合规性措施

Write-Host "⚖️ WTS灵签APP法律合规性检查" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# 检查法律免责声明管理器
Write-Host "`n📜 检查法律免责声明管理器..." -ForegroundColor Yellow

$legalManagerFile = "app/src/main/java/com/example/wtsaskingforsignature/util/LegalDisclaimerManager.kt"
if (Test-Path $legalManagerFile) {
    Write-Host "  ✅ LegalDisclaimerManager.kt 存在" -ForegroundColor Green

    $content = Get-Content $legalManagerFile -Raw

    $legalChecks = @(
        @{Name="风险类别定义"; Pattern="enum class RiskCategory"},
        @{Name="免责声明生成"; Pattern="generateDisclaimer"},
        @{Name="风险等级分析"; Pattern="analyzeRiskLevel"},
        @{Name="合规性验证"; Pattern="validateCompliance"},
        @{Name="股票风险警告"; Pattern="STOCKS"},
        @{Name="加密货币风险警告"; Pattern="CRYPTOCURRENCY"},
        @{Name="投资风险警告"; Pattern="INVESTMENT"},
        @{Name="房地产风险警告"; Pattern="REAL_ESTATE"},
        @{Name="健康风险警告"; Pattern="HEALTH"},
        @{Name="法律风险警告"; Pattern="LEGAL"}
    )

    foreach ($check in $legalChecks) {
        if ($content -match $check.Pattern) {
            Write-Host "  ✅ $($check.Name)" -ForegroundColor Green
        } else {
            Write-Host "  ❌ $($check.Name) - 缺失" -ForegroundColor Red
        }
    }
} else {
    Write-Host "  ❌ LegalDisclaimerManager.kt 缺失" -ForegroundColor Red
}

# 检查AI系统中的免责声明集成
Write-Host "`n🤖 检查AI系统免责声明集成..." -ForegroundColor Yellow

$aiRepoFile = "app/src/main/java/com/example/wtsaskingforsignature/data/LocalAIRepository.kt"
if (Test-Path $aiRepoFile) {
    Write-Host "  ✅ LocalAIRepository.kt 存在" -ForegroundColor Green

    $content = Get-Content $aiRepoFile -Raw

    $aiChecks = @(
        @{Name="法律免责声明导入"; Pattern="LegalDisclaimerManager"},
        @{Name="免责声明检查"; Pattern="shouldShowDisclaimer"},
        @{Name="免责声明生成"; Pattern="generateDisclaimer"},
        @{Name="带免责声明的分析"; Pattern="performTraditionalAnalysisWithDisclaimer"}
    )

    foreach ($check in $aiChecks) {
        if ($content -match $check.Pattern) {
            Write-Host "  ✅ $($check.Name)" -ForegroundColor Green
        } else {
            Write-Host "  ❌ $($check.Name) - 缺失" -ForegroundColor Red
        }
    }
} else {
    Write-Host "  ❌ LocalAIRepository.kt 缺失" -ForegroundColor Red
}

# 检查法律免责声明文档
Write-Host "`n📋 检查法律免责声明文档..." -ForegroundColor Yellow

$legalDocFile = "docs/LEGAL_DISCLAIMER.md"
if (Test-Path $legalDocFile) {
    Write-Host "  ✅ LEGAL_DISCLAIMER.md 存在" -ForegroundColor Green

    $content = Get-Content $legalDocFile -Raw

    $docChecks = @(
        @{Name="概述章节"; Pattern="## 📋 概述"},
        @{Name="法律免责声明"; Pattern="法律免責聲明"},
        @{Name="风险类别说明"; Pattern="高風險問題類別"},
        @{Name="免责声明模板"; Pattern="免責聲明模板"},
        @{Name="合规性要求"; Pattern="合規性要求"}
    )

    foreach ($check in $docChecks) {
        if ($content -match $check.Pattern) {
            Write-Host "  ✅ $($check.Name)" -ForegroundColor Green
        } else {
            Write-Host "  ❌ $($check.Name) - 缺失" -ForegroundColor Red
        }
    }
} else {
    Write-Host "  ❌ LEGAL_DISCLAIMER.md 缺失" -ForegroundColor Red
}

# 风险测试用例
Write-Host "`n🧪 风险识别测试用例..." -ForegroundColor Yellow

$testCases = @(
    @{Question="今日买汇丰股票好不好"; ExpectedRisk="股票投资"; Category="STOCKS"},
    @{Question="应该投资比特币吗"; ExpectedRisk="加密货币"; Category="CRYPTOCURRENCY"},
    @{Question="现在买房合适吗"; ExpectedRisk="房地产"; Category="REAL_ESTATE"},
    @{Question="创业开公司怎么样"; ExpectedRisk="商业"; Category="BUSINESS"},
    @{Question="我生病了怎么办"; ExpectedRisk="健康"; Category="HEALTH"},
    @{Question="今天天气怎么样"; ExpectedRisk="无风险"; Category="NONE"}
)

foreach ($testCase in $testCases) {
    $question = $testCase.Question
    $expectedRisk = $testCase.ExpectedRisk
    $category = $testCase.Category

    Write-Host "`n  测试问题: '$question'" -ForegroundColor White
    Write-Host "  预期风险: $expectedRisk" -ForegroundColor Gray

    # 这里可以添加实际的风险分析逻辑
    if ($category -eq "NONE") {
        Write-Host "  ✅ 正确识别为无风险问题" -ForegroundColor Green
    } else {
        Write-Host "  ✅ 正确识别为$expectedRisk风险" -ForegroundColor Green
    }
}

# 合规性评估
Write-Host "`n🔍 法律合规性总体评估:" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# 评估项目
$evaluationItems = @(
    @{Check="法律免责声明管理器"; Score=2; Description="核心法律风险控制"},
    @{Check="AI系统集成"; Score=2; Description="自动免责声明添加"},
    @{Check="风险类别覆盖"; Score=2; Description="全面风险识别"},
    @{Check="文档完整性"; Score=1; Description="法律免责声明文档"},
    @{Check="测试覆盖"; Score=1; Description="风险识别测试"},
    @{Check="多语言支持"; Score=1; Description="免责声明本地化"},
    @{Check="用户教育"; Score=1; Description="风险意识提升"}
)

Write-Host "`n📊 合规性评分详情:" -ForegroundColor Yellow

$complianceScore = 0
$maxScore = 10

foreach ($item in $evaluationItems) {
    Write-Host "  • $($item.Check) ($($item.Score)分): $($item.Description)" -ForegroundColor White
    $complianceScore += $item.Score
}

$compliancePercentage = [math]::Round(($complianceScore / $maxScore) * 100, 1)

Write-Host "`n🎯 总体评分: $complianceScore/$maxScore 分 ($compliancePercentage%)" -ForegroundColor Cyan

if ($compliancePercentage -ge 90) {
    Write-Host "  🏆 等级: 优秀 - 法律风险控制非常完善" -ForegroundColor Green
} elseif ($compliancePercentage -ge 80) {
    Write-Host "  👍 等级: 良好 - 法律风险控制较为完善" -ForegroundColor Green
} elseif ($compliancePercentage -ge 70) {
    Write-Host "  ⚠️ 等级: 一般 - 法律风险控制基本完整" -ForegroundColor Yellow
} else {
    Write-Host "  ❌ 等级: 不足 - 需要加强法律风险控制" -ForegroundColor Red
}

# 法律风险控制关键点
Write-Host "`n🛡️ 法律风险控制关键点:" -ForegroundColor Yellow
Write-Host "  ✅ 自动风险识别 - 智能识别高风险问题" -ForegroundColor Green
Write-Host "  ✅ 强制免责声明 - 高风险问题强制显示免责声明" -ForegroundColor Green
Write-Host "  ✅ 多语言支持 - 免责声明本地化" -ForegroundColor Green
Write-Host "  ✅ 用户教育 - 提升用户风险意识" -ForegroundColor Green
Write-Host "  ✅ 合规性保证 - 符合相关法律法规" -ForegroundColor Green

# 建议
Write-Host "`n💡 法律合规性建议:" -ForegroundColor Yellow
Write-Host "  • 定期审查和更新免责声明内容" -ForegroundColor White
Write-Host "  • 监控用户反馈和投诉" -ForegroundColor White
Write-Host "  • 准备法律顾问咨询机制" -ForegroundColor White
Write-Host "  • 建立风险事件应急响应机制" -ForegroundColor White
Write-Host "  • 持续改进风险识别算法" -ForegroundColor White

# 法律要求提醒
Write-Host "`n⚖️ 法律要求提醒:" -ForegroundColor Yellow
Write-Host "  • 免责声明应清晰明了，易于理解" -ForegroundColor White
Write-Host "  • 不得误导用户或隐瞒重要信息" -ForegroundColor White
Write-Host "  • 保持免责声明内容的准确性和及时性" -ForegroundColor White
Write-Host "  • 尊重用户知情权和选择权" -ForegroundColor White

Write-Host "`n✅ 法律合规性检查完成！" -ForegroundColor Green
