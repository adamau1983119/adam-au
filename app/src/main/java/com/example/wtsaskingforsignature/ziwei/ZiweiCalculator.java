package com.example.wtsaskingforsignature.ziwei;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 紫薇斗数基础计算器
 * 提供命宫、身宫、主星等基础计算功能
 */
public class ZiweiCalculator {
    
    // 十二地支
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    
    // 十天干
    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    
    // 十二宫位名称
    private static final String[] GONGWEI = {"命宫", "兄弟", "夫妻", "子女", "财帛", "疾厄", "迁移", "交友", "官禄", "田宅", "福德", "父母"};
    
    // 主星名称
    private static final String[] MAIN_STARS = {"紫微", "天机", "太阳", "武曲", "天同", "廉贞", "天府", "太阴", "贪狼", "巨门", "天相", "天梁", "七杀", "破军"};
    
    /**
     * 紫薇斗数计算结果
     */
    public static class ZiweiResult {
        private int mingGong;           // 命宫位置 (0-11)
        private int shenGong;           // 身宫位置 (0-11)
        private Map<String, Integer> mainStars;  // 主星位置
        private Map<String, Integer> gongWei;    // 十二宫位
        private String mingGongDizhi;   // 命宫地支
        private String shenGongDizhi;   // 身宫地支
        
        public ZiweiResult() {
            this.mainStars = new HashMap<>();
            this.gongWei = new HashMap<>();
        }
        
        // Getters and Setters
        public int getMingGong() { return mingGong; }
        public void setMingGong(int mingGong) { this.mingGong = mingGong; }
        
        public int getShenGong() { return shenGong; }
        public void setShenGong(int shenGong) { this.shenGong = shenGong; }
        
        public Map<String, Integer> getMainStars() { return mainStars; }
        public void setMainStars(Map<String, Integer> mainStars) { this.mainStars = mainStars; }
        
        public Map<String, Integer> getGongWei() { return gongWei; }
        public void setGongWei(Map<String, Integer> gongWei) { this.gongWei = gongWei; }
        
        public String getMingGongDizhi() { return mingGongDizhi; }
        public void setMingGongDizhi(String mingGongDizhi) { this.mingGongDizhi = mingGongDizhi; }
        
        public String getShenGongDizhi() { return shenGongDizhi; }
        public void setShenGongDizhi(String shenGongDizhi) { this.shenGongDizhi = shenGongDizhi; }
        
        @Override
        public String toString() {
            return String.format("命宫:%s(%d), 身宫:%s(%d), 主星:%s", 
                mingGongDizhi, mingGong, shenGongDizhi, shenGong, mainStars);
        }
    }
    
    /**
     * 计算紫薇斗数
     * @param birthDate 出生日期时间
     * @param birthPlace 出生地点（用于时区调整）
     * @return 紫薇斗数结果
     */
    public ZiweiResult calculate(LocalDateTime birthDate, String birthPlace) {
        ZiweiResult result = new ZiweiResult();
        
        try {
            // 1. 计算命宫
            int mingGong = calculateMingGong(birthDate);
            result.setMingGong(mingGong);
            result.setMingGongDizhi(DIZHI[mingGong]);
            
            // 2. 计算身宫
            int shenGong = calculateShenGong(birthDate);
            result.setShenGong(shenGong);
            result.setShenGongDizhi(DIZHI[shenGong]);
            
            // 3. 计算主星位置
            Map<String, Integer> mainStars = calculateMainStars(birthDate, mingGong);
            result.setMainStars(mainStars);
            
            // 4. 计算十二宫位
            Map<String, Integer> gongWei = calculateGongWei(mingGong);
            result.setGongWei(gongWei);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 计算命宫位置
     * 命宫 = (月数 + 时辰) % 12
     */
    private int calculateMingGong(LocalDateTime birthDate) {
        int month = birthDate.getMonthValue();
        int hour = birthDate.getHour();
        
        // 时辰转换 (子时=0, 丑时=1, ..., 亥时=11)
        int shiChen = convertHourToShiChen(hour);
        
        // 命宫计算
        int mingGong = (month + shiChen) % 12;
        return mingGong;
    }
    
    /**
     * 计算身宫位置
     * 身宫 = (12 - 命宫) % 12
     */
    private int calculateShenGong(LocalDateTime birthDate) {
        int mingGong = calculateMingGong(birthDate);
        int shenGong = (12 - mingGong) % 12;
        return shenGong;
    }
    
    /**
     * 将小时转换为时辰
     */
    private int convertHourToShiChen(int hour) {
        if (hour >= 23 || hour < 1) return 0;      // 子时 23:00-01:00
        else if (hour >= 1 && hour < 3) return 1;   // 丑时 01:00-03:00
        else if (hour >= 3 && hour < 5) return 2;   // 寅时 03:00-05:00
        else if (hour >= 5 && hour < 7) return 3;   // 卯时 05:00-07:00
        else if (hour >= 7 && hour < 9) return 4;   // 辰时 07:00-09:00
        else if (hour >= 9 && hour < 11) return 5;  // 巳时 09:00-11:00
        else if (hour >= 11 && hour < 13) return 6; // 午时 11:00-13:00
        else if (hour >= 13 && hour < 15) return 7; // 未时 13:00-15:00
        else if (hour >= 15 && hour < 17) return 8; // 申时 15:00-17:00
        else if (hour >= 17 && hour < 19) return 9; // 酉时 17:00-19:00
        else if (hour >= 19 && hour < 21) return 10;// 戌时 19:00-21:00
        else return 11;                              // 亥时 21:00-23:00
    }
    
    /**
     * 计算主星位置
     * 简化版主星计算，基于出生日期和命宫
     */
    private Map<String, Integer> calculateMainStars(LocalDateTime birthDate, int mingGong) {
        Map<String, Integer> mainStars = new HashMap<>();
        
        // 简化版主星计算
        // 紫微星：根据出生日期计算
        int ziweiPosition = calculateZiweiPosition(birthDate);
        mainStars.put("紫微", ziweiPosition);
        
        // 其他主星：基于紫微星位置推算
        mainStars.put("天机", (ziweiPosition + 1) % 12);
        mainStars.put("太阳", (ziweiPosition + 2) % 12);
        mainStars.put("武曲", (ziweiPosition + 3) % 12);
        mainStars.put("天同", (ziweiPosition + 4) % 12);
        mainStars.put("廉贞", (ziweiPosition + 5) % 12);
        mainStars.put("天府", (ziweiPosition + 6) % 12);
        mainStars.put("太阴", (ziweiPosition + 7) % 12);
        mainStars.put("贪狼", (ziweiPosition + 8) % 12);
        mainStars.put("巨门", (ziweiPosition + 9) % 12);
        mainStars.put("天相", (ziweiPosition + 10) % 12);
        mainStars.put("天梁", (ziweiPosition + 11) % 12);
        mainStars.put("七杀", (ziweiPosition + 12) % 12);
        mainStars.put("破军", (ziweiPosition + 13) % 12);
        
        return mainStars;
    }
    
    /**
     * 计算紫微星位置
     * 简化算法：基于出生日期
     */
    private int calculateZiweiPosition(LocalDateTime birthDate) {
        int day = birthDate.getDayOfMonth();
        int month = birthDate.getMonthValue();
        
        // 简化算法：基于日期计算
        int position = (day + month * 2) % 12;
        return position;
    }
    
    /**
     * 计算十二宫位
     * 以命宫为起点，顺时针排列
     */
    private Map<String, Integer> calculateGongWei(int mingGong) {
        Map<String, Integer> gongWei = new HashMap<>();
        
        for (int i = 0; i < 12; i++) {
            int position = (mingGong + i) % 12;
            gongWei.put(GONGWEI[i], position);
        }
        
        return gongWei;
    }
    
    /**
     * 获取宫位地支名称
     */
    public String getGongWeiDizhi(int position) {
        return DIZHI[position];
    }
    
    /**
     * 获取宫位名称
     */
    public String getGongWeiName(int position) {
        return GONGWEI[position];
    }
    
    /**
     * 获取主星名称
     */
    public String getMainStarName(int position) {
        return MAIN_STARS[position % MAIN_STARS.length];
    }
}
