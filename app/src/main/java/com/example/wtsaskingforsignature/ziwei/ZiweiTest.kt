package com.example.wtsaskingforsignature.ziwei

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 紫薇斗数系统测试类
 * 用于验证紫薇斗数计算和分析功能
 */
class ZiweiTest {
    
    companion object {
        /**
         * 运行所有测试
         */
        @JvmStatic
        fun runAllTests() {
            println("🧪 开始紫薇斗数系统测试...")
            println("=".repeat(50))
            
            testZiweiCalculator()
            testZiweiAnalyzer()
            testZiweiAnalysisAdapter()
            
            println("=".repeat(50))
            println("✅ 所有测试完成！")
        }
        
        /**
         * 测试紫薇斗数计算器
         */
        private fun testZiweiCalculator() {
            println("\n📊 测试紫薇斗数计算器...")
            
            val calculator = ZiweiCalculator()
            
            // 测试用例1：1990年1月1日 12:00
            val birthDate1 = LocalDateTime.of(1990, 1, 1, 12, 0)
            val result1 = calculator.calculate(birthDate1, "北京")
            
            println("测试用例1 - 1990年1月1日 12:00:")
            println("  命宫：${result1.mingGongDizhi}宫 (${result1.mingGong})")
            println("  身宫：${result1.shenGongDizhi}宫 (${result1.shenGong})")
            println("  紫微星：${calculator.getGongWeiName(result1.mainStars["紫微"] ?: 0)}宫")
            
            // 测试用例2：1985年6月15日 15:30
            val birthDate2 = LocalDateTime.of(1985, 6, 15, 15, 30)
            val result2 = calculator.calculate(birthDate2, "上海")
            
            println("\n测试用例2 - 1985年6月15日 15:30:")
            println("  命宫：${result2.mingGongDizhi}宫 (${result2.mingGong})")
            println("  身宫：${result2.shenGongDizhi}宫 (${result2.shenGong})")
            println("  紫微星：${calculator.getGongWeiName(result2.mainStars["紫微"] ?: 0)}宫")
            
            // 测试用例3：1995年12月25日 23:45
            val birthDate3 = LocalDateTime.of(1995, 12, 25, 23, 45)
            val result3 = calculator.calculate(birthDate3, "广州")
            
            println("\n测试用例3 - 1995年12月25日 23:45:")
            println("  命宫：${result3.mingGongDizhi}宫 (${result3.mingGong})")
            println("  身宫：${result3.shenGongDizhi}宫 (${result3.shenGong})")
            println("  紫微星：${calculator.getGongWeiName(result3.mainStars["紫微"] ?: 0)}宫")
        }
        
        /**
         * 测试紫薇斗数分析器
         */
        private fun testZiweiAnalyzer() {
            println("\n🔍 测试紫薇斗数分析器...")
            
            val analyzer = ZiweiAnalyzer()
            
            // 测试用例：1990年1月1日出生，询问事业
            val birthDate = LocalDateTime.of(1990, 1, 1, 12, 0)
            val personalInfo = ZiweiAnalyzer.PersonalInfo(
                birthDate,
                "北京",
                "男",
                "我想创业开公司，前景如何？"
            )
            
            val fortuneInfo = ZiweiAnalyzer.FortuneInfo(
                25,
                "第25籤",
                "上上籤",
                "这是第25籤的籤文内容"
            )
            
            val result = analyzer.analyze(fortuneInfo, personalInfo)
            
            println("分析结果：")
            println("  籤文分析：${result.fortuneAnalysis.take(100)}...")
            println("  紫薇分析：${result.ziweiAnalysis.take(100)}...")
            println("  结合分析：${result.combinationAnalysis.take(100)}...")
            println("  个性化建议：${result.personalizedAdvice.take(100)}...")
        }
        
        /**
         * 测试紫薇斗数分析适配器
         */
        private fun testZiweiAnalysisAdapter() {
            println("\n🔧 测试紫薇斗数分析适配器...")
            
            val adapter = ZiweiAnalysisAdapter()
            
            // 测试基本信息获取
            val basicInfo = adapter.getBasicZiweiInfo("1990-01-01")
            println("基本信息：")
            println(basicInfo)
            
            // 测试快速分析
            println("\n快速分析测试：")
            val quickResult = runBlocking { 
                adapter.quickAnalyze(25, "我想创业，请用紫薇斗数分析", "1990-01-01")
            }
            println("快速分析结果：${quickResult.take(200)}...")
        }
        
        /**
         * 运行阻塞协程（用于测试）
         */
        private fun <T> runBlocking(block: suspend () -> T): T {
            return kotlinx.coroutines.runBlocking { block() }
        }
    }
}
