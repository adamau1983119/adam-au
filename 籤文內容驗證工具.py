#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
籤文內容驗證工具
自動對比籤文內容的正確性和完整性
"""

import json
import re
import requests
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass
from pathlib import Path
import time

@dataclass
class FortuneContent:
    """籤文內容數據結構"""
    id: int
    title: str
    summary: str
    content: str
    
    @property
    def verse(self) -> str:
        """提取籤詩部分"""
        lines = self.content.split('\n')
        verse_lines = []
        in_verse = False
        
        for line in lines:
            if '籤詩' in line or '签诗' in line:
                in_verse = True
                continue
            elif in_verse and (line.startswith('典故') or line.startswith('黃大仙') or line.startswith('黄大仙')):
                break
            elif in_verse and line.strip():
                verse_lines.append(line.strip())
        
        return '\n'.join(verse_lines)
    
    @property
    def explanation(self) -> str:
        """提取解釋部分"""
        lines = self.content.split('\n')
        explanation_lines = []
        in_explanation = False
        
        for line in lines:
            if '黃大仙算命解籤詩' in line or '黄大仙算命解签诗' in line:
                in_explanation = True
                continue
            elif in_explanation and (line.startswith('黃大仙算命解運勢') or line.startswith('黄大仙算命解运势')):
                break
            elif in_explanation and line.strip():
                explanation_lines.append(line.strip())
        
        return '\n'.join(explanation_lines)
    
    @property
    def fortune(self) -> str:
        """提取運勢解讀部分"""
        lines = self.content.split('\n')
        fortune_lines = []
        in_fortune = False
        
        for line in lines:
            if '黃大仙算命解運勢' in line or '黄大仙算命解运势' in line:
                in_fortune = True
                continue
            elif in_fortune and line.strip():
                fortune_lines.append(line.strip())
        
        return '\n'.join(fortune_lines)

class FortuneValidator:
    """籤文內容驗證器"""
    
    def __init__(self):
        self.reference_sources = {
            "黃大仙祠官方": "https://www.siksikyuen.org.hk/",
            "香港黃大仙祠": "https://www.wongtaisin.org.hk/",
            "台灣各大廟宇": "https://www.taiwan.net.tw/",
        }
        
        # 常見籤文關鍵詞
        self.common_keywords = [
            "籤詩", "签诗", "籤文", "签文", "解籤", "解签",
            "運勢", "运势", "吉凶", "吉凶", "流年", "流年",
            "事業", "事业", "財富", "财富", "健康", "健康",
            "姻緣", "姻缘", "家庭", "家庭", "移居", "移居"
        ]
    
    def load_fortunes(self, file_path: str) -> List[FortuneContent]:
        """載入籤文數據"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return [FortuneContent(**item) for item in data]
        except Exception as e:
            print(f"載入籤文數據失敗: {e}")
            return []
    
    def validate_fortune_structure(self, fortune: FortuneContent) -> Dict[str, bool]:
        """驗證籤文結構完整性"""
        validation = {
            "有標題": bool(fortune.title and fortune.title.strip()),
            "有摘要": bool(fortune.summary and fortune.summary.strip()),
            "有內容": bool(fortune.content and fortune.content.strip()),
            "有籤詩": bool(fortune.verse and fortune.verse.strip()),
            "有解釋": bool(fortune.explanation and fortune.explanation.strip()),
            "有運勢": bool(fortune.fortune and fortune.fortune.strip()),
        }
        
        # 檢查內容長度
        validation["內容長度適中"] = 100 < len(fortune.content) < 2000
        
        return validation
    
    def check_content_quality(self, fortune: FortuneContent) -> Dict[str, List[str]]:
        """檢查內容質量"""
        issues = {
            "警告": [],
            "錯誤": [],
            "建議": []
        }
        
        # 檢查是否有待補內容
        if "待補" in fortune.content or "（待補）" in fortune.content:
            issues["錯誤"].append("存在待補內容")
        
        # 檢查籤詩格式
        if fortune.verse:
            verse_lines = fortune.verse.split('\n')
            if len(verse_lines) < 2:
                issues["警告"].append("籤詩行數過少")
            if len(verse_lines) > 8:
                issues["警告"].append("籤詩行數過多")
        
        # 檢查解釋完整性
        if fortune.explanation:
            if len(fortune.explanation) < 50:
                issues["警告"].append("解釋內容過短")
        
        # 檢查運勢項目
        if fortune.fortune:
            required_items = ["流年", "事業", "財富", "健康", "姻緣"]
            missing_items = [item for item in required_items if item not in fortune.fortune]
            if missing_items:
                issues["建議"].append(f"缺少運勢項目: {', '.join(missing_items)}")
        
        return issues
    
    def generate_validation_report(self, fortunes: List[FortuneContent]) -> str:
        """生成驗證報告"""
        report = []
        report.append("=" * 60)
        report.append("籤文內容驗證報告")
        report.append("=" * 60)
        report.append(f"總籤文數量: {len(fortunes)}")
        report.append("")
        
        # 統計信息
        total_issues = 0
        structure_issues = 0
        quality_issues = 0
        
        for fortune in fortunes:
            report.append(f"籤文 {fortune.id}: {fortune.title}")
            report.append("-" * 40)
            
            # 結構驗證
            structure = self.validate_fortune_structure(fortune)
            structure_problems = [k for k, v in structure.items() if not v]
            if structure_problems:
                structure_issues += 1
                report.append(f"❌ 結構問題: {', '.join(structure_problems)}")
            
            # 質量檢查
            quality = self.check_content_quality(fortune)
            for level, issues in quality.items():
                if issues:
                    quality_issues += len(issues)
                    total_issues += len(issues)
                    for issue in issues:
                        icon = "❌" if level == "錯誤" else "⚠️" if level == "警告" else "💡"
                        report.append(f"{icon} {level}: {issue}")
            
            if not structure_problems and not any(quality.values()):
                report.append("✅ 籤文內容完整且符合標準")
            
            report.append("")
        
        # 總結
        report.append("=" * 60)
        report.append("驗證總結")
        report.append("=" * 60)
        report.append(f"總籤文數量: {len(fortunes)}")
        report.append(f"結構問題籤文: {structure_issues}")
        report.append(f"質量問題數量: {quality_issues}")
        report.append(f"總問題數量: {total_issues}")
        
        if total_issues == 0:
            report.append("🎉 所有籤文內容都符合標準！")
        elif total_issues < 10:
            report.append("👍 籤文內容整體良好，有少量問題需要改進")
        else:
            report.append("⚠️ 籤文內容存在較多問題，建議優先處理錯誤級別問題")
        
        return "\n".join(report)
    
    def export_validation_data(self, fortunes: List[FortuneContent], output_file: str):
        """導出驗證數據為JSON格式"""
        validation_data = []
        
        for fortune in fortunes:
            data = {
                "id": fortune.id,
                "title": fortune.title,
                "structure_validation": self.validate_fortune_structure(fortune),
                "quality_issues": self.check_content_quality(fortune),
                "verse": fortune.verse,
                "explanation": fortune.explanation,
                "fortune": fortune.fortune
            }
            validation_data.append(data)
        
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(validation_data, f, ensure_ascii=False, indent=2)
            print(f"驗證數據已導出到: {output_file}")
        except Exception as e:
            print(f"導出驗證數據失敗: {e}")

def main():
    """主函數"""
    print("籤文內容驗證工具啟動中...")
    
    # 初始化驗證器
    validator = FortuneValidator()
    
    # 載入籤文數據
    fortunes_file = "app/src/main/assets/fortunes.json"
    if not Path(fortunes_file).exists():
        print(f"找不到籤文文件: {fortunes_file}")
        return
    
    fortunes = validator.load_fortunes(fortunes_file)
    if not fortunes:
        print("沒有載入到籤文數據")
        return
    
    print(f"成功載入 {len(fortunes)} 個籤文")
    
    # 生成驗證報告
    report = validator.generate_validation_report(fortunes)
    print(report)
    
    # 保存報告到文件
    timestamp = time.strftime("%Y%m%d_%H%M%S")
    report_file = f"籤文驗證報告_{timestamp}.txt"
    try:
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write(report)
        print(f"\n驗證報告已保存到: {report_file}")
    except Exception as e:
        print(f"保存報告失敗: {e}")
    
    # 導出詳細驗證數據
    data_file = f"籤文驗證數據_{timestamp}.json"
    validator.export_validation_data(fortunes, data_file)
    
    print("\n驗證完成！請查看生成的報告和數據文件。")

if __name__ == "__main__":
    main()
