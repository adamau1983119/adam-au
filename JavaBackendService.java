// Java 後端服務示例
// 這是一個完整的 Java Spring Boot 服務，可以部署到服務器上

package com.wts.fortune;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class FortuneServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FortuneServiceApplication.class, args);
    }

    @Service
    public static class FortuneService {
        
        // 籤文資料庫（實際應用中應該使用真實資料庫）
        private final Map<Integer, Fortune> fortuneDatabase = new HashMap<>();
        
        public FortuneService() {
            initializeFortunes();
        }
        
        private void initializeFortunes() {
            // 初始化籤文資料
            fortuneDatabase.put(1, new Fortune(1, "黃大仙第1籤", "上上籤", "事業運勢極佳，適合大膽進取"));
            fortuneDatabase.put(2, new Fortune(2, "黃大仙第2籤", "上籤", "財運良好，收入穩定增長"));
            // ... 更多籤文
        }
        
        /**
         * 抽籤 API
         */
        @GetMapping("/draw")
        public ResponseEntity<DrawResponse> draw() {
            Random random = new Random();
            int fortuneId = random.nextInt(100) + 1;
            Fortune fortune = fortuneDatabase.getOrDefault(fortuneId, 
                new Fortune(fortuneId, "第" + fortuneId + "籤", "中籤", "籤文內容"));
            
            return ResponseEntity.ok(new DrawResponse(fortune));
        }
        
        /**
         * 獲取指定籤文
         */
        @GetMapping("/fortunes/{id}")
        public ResponseEntity<Fortune> getFortune(@PathVariable int id) {
            Fortune fortune = fortuneDatabase.get(id);
            if (fortune != null) {
                return ResponseEntity.ok(fortune);
            }
            return ResponseEntity.notFound().build();
        }
        
        /**
         * 解籤對話 API
         */
        @PostMapping("/chat")
        public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
            try {
                // 獲取籤文內容
                Fortune fortune = fortuneDatabase.get(request.getFortuneId());
                if (fortune == null) {
                    return ResponseEntity.badRequest().build();
                }
                
                // 使用 AI 邏輯生成解籤
                String aiResponse = generateAIResponse(fortune, request.getQuestion());
                
                // 創建回應
                ChatMessage message = new ChatMessage("assistant", aiResponse);
                List<ChatMessage> messages = Arrays.asList(message);
                
                return ResponseEntity.ok(new ChatResponse(messages));
                
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }
        
        /**
         * 擲杯結果 API
         */
        @GetMapping("/cup/result")
        public ResponseEntity<CupResultResponse> cupResult() {
            Random random = new Random();
            List<CupAttempt> attempts = new ArrayList<>();
            
            // 模擬三次擲杯
            for (int i = 1; i <= 3; i++) {
                boolean isPositive = random.nextBoolean();
                attempts.add(new CupAttempt(i, isPositive));
            }
            
            // 判斷是否有效（至少兩個正面或兩個反面）
            long positiveCount = attempts.stream().filter(CupAttempt::isPositive).count();
            boolean isValid = positiveCount >= 2 || positiveCount <= 1;
            
            return ResponseEntity.ok(new CupResultResponse(attempts, isValid));
        }
        
        /**
         * AI 解籤邏輯
         */
        private String generateAIResponse(Fortune fortune, String question) {
            StringBuilder response = new StringBuilder();
            
            response.append("【AI 智能解籤】\n");
            response.append("籤文：").append(fortune.getTitle()).append("\n");
            response.append("等級：").append(fortune.getSummary()).append("\n\n");
            
            // 根據籤文等級和問題類型生成回應
            String advice = generateAdvice(fortune.getSummary(), question);
            response.append("【解籤分析】\n");
            response.append(fortune.getContent()).append("\n\n");
            response.append("【智能建議】\n");
            response.append(advice).append("\n\n");
            response.append("【溫馨提醒】\n");
            response.append("此解籤僅供參考，具體行動請結合實際情況。");
            
            return response.toString();
        }
        
        /**
         * 生成建議
         */
        private String generateAdvice(String level, String question) {
            if (question.contains("事業") || question.contains("工作")) {
                return generateCareerAdvice(level);
            } else if (question.contains("財運") || question.contains("金錢")) {
                return generateWealthAdvice(level);
            } else if (question.contains("感情") || question.contains("婚姻")) {
                return generateLoveAdvice(level);
            } else if (question.contains("健康") || question.contains("身體")) {
                return generateHealthAdvice(level);
            } else {
                return generateGeneralAdvice(level);
            }
        }
        
        private String generateCareerAdvice(String level) {
            switch (level) {
                case "上上籤":
                    return "事業運勢極佳，適合大膽進取。建議：1) 把握當下機會 2) 展現領導才能 3) 擴展業務範圍";
                case "上籤":
                    return "事業發展良好，穩步上升。建議：1) 保持專業水準 2) 加強團隊合作 3) 學習新技能";
                case "中籤":
                    return "事業運勢平穩，需要耐心。建議：1) 專注當前工作 2) 提升工作效率 3) 改善溝通技巧";
                default:
                    return "事業面臨挑戰，需要調整。建議：1) 檢視工作方法 2) 改善人際關係 3) 提升專業能力";
            }
        }
        
        private String generateWealthAdvice(String level) {
            switch (level) {
                case "上上籤":
                    return "財運極佳，投資機會多。建議：1) 把握投資時機 2) 分散投資組合 3) 學習理財知識";
                case "上籤":
                    return "財運良好，收入穩定增長。建議：1) 制定理財計劃 2) 適度投資理財 3) 控制不必要支出";
                case "中籤":
                    return "財運平穩，收支平衡。建議：1) 量入為出 2) 避免衝動消費 3) 學習節儉理財";
                default:
                    return "財運需改善，需要調整。建議：1) 檢視支出習慣 2) 制定預算計劃 3) 避免高風險投資";
            }
        }
        
        private String generateLoveAdvice(String level) {
            switch (level) {
                case "上上籤":
                    return "感情運勢極佳，桃花旺盛。建議：1) 主動表達愛意 2) 參加社交活動 3) 展現個人魅力";
                case "上籤":
                    return "感情發展良好，關係穩定。建議：1) 增進彼此了解 2) 創造浪漫時刻 3) 共同成長進步";
                case "中籤":
                    return "感情運勢平穩，需要耐心。建議：1) 保持真誠溝通 2) 理解對方感受 3) 尋找共同興趣";
                default:
                    return "感情面臨挑戰，需要努力。建議：1) 檢視關係問題 2) 改善溝通方式 3) 尋求專業建議";
            }
        }
        
        private String generateHealthAdvice(String level) {
            switch (level) {
                case "上上籤":
                    return "健康狀況極佳，精力充沛。建議：1) 保持運動習慣 2) 均衡飲食營養 3) 充足睡眠休息";
                case "上籤":
                    return "健康狀況良好，身體強健。建議：1) 規律運動鍛煉 2) 注意飲食衛生 3) 保持良好作息";
                case "中籤":
                    return "健康狀況平穩，需要關注。建議：1) 適度運動鍛煉 2) 調整飲食習慣 3) 改善睡眠質量";
                default:
                    return "健康需要改善，需要調養。建議：1) 諮詢醫生建議 2) 調整生活方式 3) 避免不良習慣";
            }
        }
        
        private String generateGeneralAdvice(String level) {
            switch (level) {
                case "上上籤":
                    return "整體運勢極佳，諸事順遂。建議：1) 把握當下機會 2) 積極進取行動 3) 幫助他人成長";
                case "上籤":
                    return "整體運勢良好，穩步發展。建議：1) 保持積極心態 2) 專注重要事務 3) 維護人際關係";
                case "中籤":
                    return "整體運勢平穩，需要努力。建議：1) 保持耐心毅力 2) 專注當前目標 3) 改善不足之處";
                default:
                    return "整體運勢需改善，需要調整。建議：1) 檢視生活方向 2) 調整心態觀念 3) 尋求專業指導";
            }
        }
    }
}

// 資料模型類
class Fortune {
    private int id;
    private String title;
    private String summary;
    private String content;
    
    public Fortune(int id, String title, String summary, String content) {
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

class DrawResponse {
    private Fortune fortune;
    
    public DrawResponse(Fortune fortune) {
        this.fortune = fortune;
    }
    
    public Fortune getFortune() { return fortune; }
    public void setFortune(Fortune fortune) { this.fortune = fortune; }
}

class ChatRequest {
    @JsonProperty("fortuneId")
    private int fortuneId;
    
    @JsonProperty("question")
    private String question;
    
    public int getFortuneId() { return fortuneId; }
    public void setFortuneId(int fortuneId) { this.fortuneId = fortuneId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}

class ChatMessage {
    private String role;
    private String content;
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

class ChatResponse {
    private List<ChatMessage> messages;
    
    public ChatResponse(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}

class CupAttempt {
    private int attempt;
    private boolean isPositive;
    
    public CupAttempt(int attempt, boolean isPositive) {
        this.attempt = attempt;
        this.isPositive = isPositive;
    }
    
    public int getAttempt() { return attempt; }
    public void setAttempt(int attempt) { this.attempt = attempt; }
    public boolean isPositive() { return isPositive; }
    public void setIsPositive(boolean isPositive) { this.isPositive = isPositive; }
}

class CupResultResponse {
    private List<CupAttempt> attempts;
    private boolean isValid;
    
    public CupResultResponse(List<CupAttempt> attempts, boolean isValid) {
        this.attempts = attempts;
        this.isValid = isValid;
    }
    
    public List<CupAttempt> getAttempts() { return attempts; }
    public void setAttempts(List<CupAttempt> attempts) { this.attempts = attempts; }
    public boolean isValid() { return isValid; }
    public void setValid(boolean isValid) { this.isValid = isValid; }
}
