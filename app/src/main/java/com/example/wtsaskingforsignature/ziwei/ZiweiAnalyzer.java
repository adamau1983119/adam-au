package com.example.wtsaskingforsignature.ziwei;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 紫薇斗数分析器
 * 结合籤文和紫薇斗数进行分析
 */
public class ZiweiAnalyzer {
    
    private ZiweiCalculator calculator;
    
    public ZiweiAnalyzer() {
        this.calculator = new ZiweiCalculator();
    }
    
    /**
     * 分析结果
     */
    public static class AnalysisResult {
        private String fortuneAnalysis;      // 籤文分析
        private String ziweiAnalysis;        // 紫薇斗数分析
        private String combinationAnalysis;  // 结合分析
        private String personalizedAdvice;   // 个性化建议
        private ZiweiCalculator.ZiweiResult ziweiResult; // 紫薇斗数结果
        
        public AnalysisResult() {}
        
        // Getters and Setters
        public String getFortuneAnalysis() { return fortuneAnalysis; }
        public void setFortuneAnalysis(String fortuneAnalysis) { this.fortuneAnalysis = fortuneAnalysis; }
        
        public String getZiweiAnalysis() { return ziweiAnalysis; }
        public void setZiweiAnalysis(String ziweiAnalysis) { this.ziweiAnalysis = ziweiAnalysis; }
        
        public String getCombinationAnalysis() { return combinationAnalysis; }
        public void setCombinationAnalysis(String combinationAnalysis) { this.combinationAnalysis = combinationAnalysis; }
        
        public String getPersonalizedAdvice() { return personalizedAdvice; }
        public void setPersonalizedAdvice(String personalizedAdvice) { this.personalizedAdvice = personalizedAdvice; }
        
        public ZiweiCalculator.ZiweiResult getZiweiResult() { return ziweiResult; }
        public void setZiweiResult(ZiweiCalculator.ZiweiResult ziweiResult) { this.ziweiResult = ziweiResult; }
        
        @Override
        public String toString() {
            return String.format("籤文分析: %s\n紫薇分析: %s\n结合分析: %s\n个性化建议: %s", 
                fortuneAnalysis, ziweiAnalysis, combinationAnalysis, personalizedAdvice);
        }
    }
    
    /**
     * 个人资料
     */
    public static class PersonalInfo {
        private LocalDateTime birthDate;
        private String birthPlace;
        private String gender;
        private String question;
        
        public PersonalInfo() {}
        
        public PersonalInfo(LocalDateTime birthDate, String birthPlace, String gender, String question) {
            this.birthDate = birthDate;
            this.birthPlace = birthPlace;
            this.gender = gender;
            this.question = question;
        }
        
        // Getters and Setters
        public LocalDateTime getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }
        
        public String getBirthPlace() { return birthPlace; }
        public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }
    
    /**
     * 籤文信息
     */
    public static class FortuneInfo {
        private int id;
        private String title;
        private String summary;
        private String content;
        
        public FortuneInfo() {}
        
        public FortuneInfo(int id, String title, String summary, String content) {
            this.id = id;
            this.title = title;
            this.summary = summary;
            this.content = content;
        }
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    /**
     * 综合分析
     * @param fortune 籤文信息
     * @param personalInfo 个人资料
     * @return 分析结果
     */
    public AnalysisResult analyze(FortuneInfo fortune, PersonalInfo personalInfo) {
        AnalysisResult result = new AnalysisResult();
        
        try {
            // 1. 计算紫薇斗数
            ZiweiCalculator.ZiweiResult ziweiResult = calculator.calculate(
                personalInfo.getBirthDate(), 
                personalInfo.getBirthPlace()
            );
            result.setZiweiResult(ziweiResult);
            
            // 2. 籤文分析
            String fortuneAnalysis = analyzeFortune(fortune);
            result.setFortuneAnalysis(fortuneAnalysis);
            
            // 3. 紫薇斗数分析
            String ziweiAnalysis = analyzeZiwei(ziweiResult, personalInfo);
            result.setZiweiAnalysis(ziweiAnalysis);
            
            // 4. 结合分析
            String combinationAnalysis = combineAnalysis(fortune, ziweiResult, personalInfo);
            result.setCombinationAnalysis(combinationAnalysis);
            
            // 5. 个性化建议
            String personalizedAdvice = generatePersonalizedAdvice(fortune, ziweiResult, personalInfo);
            result.setPersonalizedAdvice(personalizedAdvice);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 出错时提供基础分析
            result.setFortuneAnalysis("籤文分析：第" + fortune.getId() + "籤，请参考籤文内容。");
            result.setZiweiAnalysis("紫薇斗数分析暂时无法提供，请稍后再试。");
            result.setCombinationAnalysis("结合分析暂时无法提供。");
            result.setPersonalizedAdvice("建议您保持积极心态，相信自己的判断。");
        }
        
        return result;
    }
    
    /**
     * 籤文分析
     */
    private String analyzeFortune(FortuneInfo fortune) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("【籤文分析】\n");
        analysis.append("籤文编号：第").append(fortune.getId()).append("籤\n");
        analysis.append("籤文标题：").append(fortune.getTitle()).append("\n");
        analysis.append("籤文等级：").append(fortune.getSummary()).append("\n");
        analysis.append("籤文内容：").append(fortune.getContent()).append("\n");
        
        // 根据籤文ID分析运势
        String fortuneLevel = getFortuneLevel(fortune.getId());
        analysis.append("运势分析：").append(fortuneLevel).append("\n");
        
        return analysis.toString();
    }
    
    /**
     * 紫薇斗数分析
     */
    private String analyzeZiwei(ZiweiCalculator.ZiweiResult ziweiResult, PersonalInfo personalInfo) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("【紫薇斗数分析】\n");
        analysis.append("命宫位置：").append(ziweiResult.getMingGongDizhi()).append("宫\n");
        analysis.append("身宫位置：").append(ziweiResult.getShenGongDizhi()).append("宫\n");
        
        // 主星分析
        analysis.append("主星分布：\n");
        Map<String, Integer> mainStars = ziweiResult.getMainStars();
        for (Map.Entry<String, Integer> entry : mainStars.entrySet()) {
            String starName = entry.getKey();
            int position = entry.getValue();
            String gongWeiName = calculator.getGongWeiName(position);
            analysis.append("  ").append(starName).append("星在").append(gongWeiName).append("\n");
        }
        
        // 宫位分析
        analysis.append("十二宫位：\n");
        Map<String, Integer> gongWei = ziweiResult.getGongWei();
        for (Map.Entry<String, Integer> entry : gongWei.entrySet()) {
            String gongName = entry.getKey();
            int position = entry.getValue();
            String dizhi = calculator.getGongWeiDizhi(position);
            analysis.append("  ").append(gongName).append("：").append(dizhi).append("宫\n");
        }
        
        return analysis.toString();
    }
    
    /**
     * 结合分析
     */
    private String combineAnalysis(FortuneInfo fortune, ZiweiCalculator.ZiweiResult ziweiResult, PersonalInfo personalInfo) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("【籤文与紫薇斗数结合分析】\n");
        
        // 分析命宫与籤文的关系
        String mingGongDizhi = ziweiResult.getMingGongDizhi();
        String mingGongGongWei = calculator.getGongWeiName(ziweiResult.getMingGong());
        
        analysis.append("命宫分析：您的命宫在").append(mingGongDizhi).append("宫，对应").append(mingGongGongWei).append("。\n");
        
        // 分析主星与籤文的关系
        Map<String, Integer> mainStars = ziweiResult.getMainStars();
        String ziweiPosition = calculator.getGongWeiName(mainStars.get("紫微"));
        analysis.append("紫微星位置：紫微星在").append(ziweiPosition).append("，这是您的主导星。\n");
        
        // 结合籤文等级进行分析
        String fortuneLevel = getFortuneLevel(fortune.getId());
        analysis.append("籤文结合：第").append(fortune.getId()).append("籤为").append(fortuneLevel);
        analysis.append("，结合您的紫薇斗数，").append(generateCombinationInsight(fortune.getId(), ziweiResult));
        
        return analysis.toString();
    }
    
    /**
     * 生成个性化建议
     */
    private String generatePersonalizedAdvice(FortuneInfo fortune, ZiweiCalculator.ZiweiResult ziweiResult, PersonalInfo personalInfo) {
        StringBuilder advice = new StringBuilder();
        advice.append("【个性化建议】\n");
        
        // 基于籤文等级的建议
        String fortuneLevel = getFortuneLevel(fortune.getId());
        advice.append("籤文建议：").append(getFortuneAdvice(fortuneLevel)).append("\n");
        
        // 基于紫薇斗数的建议
        String mingGongGongWei = calculator.getGongWeiName(ziweiResult.getMingGong());
        advice.append("命宫建议：您的命宫在").append(mingGongGongWei).append("，").append(getMingGongAdvice(ziweiResult.getMingGong())).append("\n");
        
        // 基于主星的建议
        Map<String, Integer> mainStars = ziweiResult.getMainStars();
        String ziweiPosition = calculator.getGongWeiName(mainStars.get("紫微"));
        advice.append("紫微建议：紫微星在").append(ziweiPosition).append("，").append(getZiweiAdvice(mainStars.get("紫微"))).append("\n");
        
        // 通用建议
        advice.append("通用建议：保持积极心态，相信自己的判断，顺其自然。\n");
        
        return advice.toString();
    }
    
    /**
     * 获取籤文等级
     */
    private String getFortuneLevel(int fortuneId) {
        if (fortuneId >= 1 && fortuneId <= 25) return "上上籤";
        else if (fortuneId >= 26 && fortuneId <= 50) return "上籤";
        else if (fortuneId >= 51 && fortuneId <= 75) return "中籤";
        else return "下籤";
    }
    
    /**
     * 获取籤文建议
     */
    private String getFortuneAdvice(String level) {
        switch (level) {
            case "上上籤": return "运势极佳，适合大展宏图，建议把握机会，积极进取。";
            case "上籤": return "运势良好，稳步发展，建议保持努力，会有不错的发展。";
            case "中籤": return "运势平稳，需要耐心，建议稳扎稳打，为未来做准备。";
            case "下籤": return "运势需改善，需要调整，建议调整策略，寻求帮助。";
            default: return "保持积极心态，相信美好未来。";
        }
    }
    
    /**
     * 获取命宫建议
     */
    private String getMingGongAdvice(int mingGong) {
        String[] advices = {
            "适合从事与水相关的行业，如航运、渔业等。",
            "适合从事与土相关的行业，如房地产、农业等。",
            "适合从事与木相关的行业，如教育、出版等。",
            "适合从事与木相关的行业，如艺术、设计等。",
            "适合从事与土相关的行业，如建筑、工程等。",
            "适合从事与火相关的行业，如科技、能源等。",
            "适合从事与火相关的行业，如餐饮、娱乐等。",
            "适合从事与土相关的行业，如医疗、保健等。",
            "适合从事与金相关的行业，如金融、IT等。",
            "适合从事与金相关的行业，如珠宝、机械等。",
            "适合从事与土相关的行业，如政府、法律等。",
            "适合从事与水相关的行业，如旅游、贸易等。"
        };
        return advices[mingGong];
    }
    
    /**
     * 获取紫微星建议
     */
    private String getZiweiAdvice(int ziweiPosition) {
        String[] advices = {
            "紫微星在此宫，主贵气，适合从事管理、领导工作。",
            "紫微星在此宫，主智慧，适合从事研究、分析工作。",
            "紫微星在此宫，主光明，适合从事传播、教育 work。",
            "紫微星在此宫，主权威，适合从事执法、军事工作。",
            "紫微星在此宫，主和谐，适合从事外交、协调工作。",
            "紫微星在此宫，主正义，适合从事法律、监察工作。",
            "紫微星在此宫，主财富，适合从事金融、投资工作。",
            "紫微星在此宫，主智慧，适合从事学术、研究 work。",
            "紫微星在此宫，主进取，适合从事创业、开拓工作。",
            "紫微星在此宫，主沟通，适合从事销售、公关工作。",
            "紫微星在此宫，主辅佐，适合从事助理、秘书工作。",
            "紫微星在此宫，主保护，适合从事安保、医疗工作。"
        };
        return advices[ziweiPosition % 12];
    }
    
    /**
     * 生成结合洞察
     */
    private String generateCombinationInsight(int fortuneId, ZiweiCalculator.ZiweiResult ziweiResult) {
        String fortuneLevel = getFortuneLevel(fortuneId);
        String mingGongGongWei = calculator.getGongWeiName(ziweiResult.getMingGong());
        
        if (fortuneLevel.equals("上上籤")) {
            return "这是一个非常好的时机，建议您在" + mingGongGongWei + "方面大胆行动。";
        } else if (fortuneLevel.equals("上籤")) {
            return "运势不错，在" + mingGongGongWei + "方面会有好的发展。";
        } else if (fortuneLevel.equals("中籤")) {
            return "运势平稳，在" + mingGongGongWei + "方面需要耐心等待。";
        } else {
            return "运势需要改善，在" + mingGongGongWei + "方面需要谨慎行事。";
        }
    }
}
