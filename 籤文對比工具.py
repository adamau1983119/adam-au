#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
籤文對比工具
專門對比特定籤文與權威來源的差異
"""

import json
import re
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass
from pathlib import Path
import time

@dataclass
class FortuneComparison:
    """籤文對比結果"""
    id: int
    title: str
    local_content: str
    reference_content: str
    differences: Dict[str, List[str]]
    similarity_score: float

class FortuneComparer:
    """籤文對比器"""
    
    def __init__(self):
        # 權威籤文數據庫（示例）
        self.reference_fortunes = {
            82: {
                "title": "第八十二靈籤：孔子擊磬",
                "verse": "聖人擊磬在于衛 誰料過門有荷簣\n嗟嘆有心挽道窮 可憐日月今將逝",
                "explanation": "孔子擊磬的故事，告誡我們，要惜取少年時，應該做的，就要及時去做，否則老之將至，有心無力了。聖人孔子，在衛國擊磬的時候，門外有人擔著竹簣路過，在磬聲中，聽出孔子是有志做一番救世救民的事業心，可惜，時不我與，歲月就這樣逝去了。",
                "fortune": {
                    "流年": "天時對自己不利，雖無大礙，但難成大事。",
                    "事業": "腳踏實地盡快將手上的工作做好，勿作不著邊際構想。",
                    "財富": "靈籤暗示微中取利，太大的投資，風險大，要審慎。",
                    "自身": "如果人生如賭博，今年只有四成贏面。",
                    "家庭": "家居平淡，敬重老人家，會添福。",
                    "姻緣": "愛情一波三折，雙方都缺乏信心。",
                    "移居": "要移民就趁早進行，禍福未知，要對自己的選擇負責。",
                    "名譽": "難有揚名立功的機會。",
                    "健康": "身上的病痛，恐怕還要延續一段日子，最好換一個醫生。",
                    "友誼": "小人多，背後也有不少在非議您，但可以不理。",
                    "風水": "靈籤暗示財滯多口舌。",
                    "遺失": "尋靈籤暗示之亦難得。",
                    "天時": "氣運不甚美。",
                    "出行": "防小人口舌。"
                }
            }
        }
    
    def load_local_fortunes(self, file_path: str) -> Dict[int, Dict]:
        """載入本地籤文數據"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return {item['id']: item for item in data}
        except Exception as e:
            print(f"載入本地籤文數據失敗: {e}")
            return {}
    
    def extract_verse_from_content(self, content: str) -> str:
        """從內容中提取籤詩"""
        lines = content.split('\n')
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
    
    def extract_explanation_from_content(self, content: str) -> str:
        """從內容中提取解釋部分"""
        lines = content.split('\n')
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
    
    def extract_fortune_items(self, content: str) -> Dict[str, str]:
        """從內容中提取運勢項目"""
        fortune_items = {}
        lines = content.split('\n')
        in_fortune = False
        
        for line in lines:
            if '黃大仙算命解運勢' in line or '黄大仙算命解运势' in line:
                in_fortune = True
                continue
            elif in_fortune and line.strip():
                # 嘗試解析運勢項目
                if '：' in line or ':' in line:
                    parts = re.split('[：:]', line, 1)
                    if len(parts) == 2:
                        key = parts[0].strip()
                        value = parts[1].strip()
                        if key and value:
                            fortune_items[key] = value
        
        return fortune_items
    
    def compare_fortune(self, fortune_id: int, local_fortunes: Dict[int, Dict]) -> Optional[FortuneComparison]:
        """對比特定籤文"""
        if fortune_id not in local_fortunes:
            print(f"本地籤文 {fortune_id} 不存在")
            return None
        
        if fortune_id not in self.reference_fortunes:
            print(f"權威籤文 {fortune_id} 不存在")
            return None
        
        local = local_fortunes[fortune_id]
        reference = self.reference_fortunes[fortune_id]
        
        # 提取本地籤文各部分
        local_verse = self.extract_verse_from_content(local['content'])
        local_explanation = self.extract_explanation_from_content(local['content'])
        local_fortune = self.extract_fortune_items(local['content'])
        
        # 對比結果
        differences = {
            "籤詩差異": [],
            "解釋差異": [],
            "運勢差異": [],
            "結構差異": []
        }
        
        # 對比籤詩
        if local_verse != reference['verse']:
            differences["籤詩差異"].append(f"本地: {local_verse}")
            differences["籤詩差異"].append(f"權威: {reference['verse']}")
        
        # 對比解釋
        if local_explanation and local_explanation != reference['explanation']:
            differences["解釋差異"].append("本地解釋與權威版本存在差異")
        
        # 對比運勢項目
        missing_items = []
        for key, value in reference['fortune'].items():
            if key not in local_fortune:
                missing_items.append(f"缺少: {key}")
        
        if missing_items:
            differences["運勢差異"].extend(missing_items)
        
        # 檢查結構完整性
        if not local_verse:
            differences["結構差異"].append("缺少籤詩部分")
        if not local_explanation:
            differences["結構差異"].append("缺少解釋部分")
        if not local_fortune:
            differences["結構差異"].append("缺少運勢解讀")
        
        # 計算相似度
        total_items = len(reference['fortune']) + 2  # 籤詩 + 解釋 + 運勢項目
        matched_items = 0
        
        if local_verse == reference['verse']:
            matched_items += 1
        if local_explanation and len(local_explanation) > 50:
            matched_items += 1
        for key in reference['fortune']:
            if key in local_fortune:
                matched_items += 1
        
        similarity_score = matched_items / total_items if total_items > 0 else 0
        
        return FortuneComparison(
            id=fortune_id,
            title=local['title'],
            local_content=local['content'],
            reference_content=str(reference),
            differences=differences,
            similarity_score=similarity_score
        )
    
    def generate_comparison_report(self, comparison: FortuneComparison) -> str:
        """生成對比報告"""
        report = []
        report.append("=" * 60)
        report.append(f"籤文對比報告: {comparison.title}")
        report.append("=" * 60)
        report.append(f"籤文ID: {comparison.id}")
        report.append(f"相似度: {comparison.similarity_score:.1%}")
        report.append("")
        
        # 詳細差異
        has_differences = False
        for category, items in comparison.differences.items():
            if items:
                has_differences = True
                report.append(f"【{category}】")
                for item in items:
                    report.append(f"  • {item}")
                report.append("")
        
        if not has_differences:
            report.append("✅ 籤文內容與權威版本完全一致！")
        else:
            report.append("⚠️ 發現以下差異，建議進行修正：")
            report.append("")
            
            # 修正建議
            if "籤詩差異" in comparison.differences and comparison.differences["籤詩差異"]:
                report.append("💡 籤詩修正建議:")
                report.append("  請核對籤詩文本，確保與權威版本一致")
                report.append("")
            
            if "運勢差異" in comparison.differences and comparison.differences["運勢差異"]:
                report.append("💡 運勢項目修正建議:")
                report.append("  請補充缺少的運勢項目，確保解讀完整")
                report.append("")
            
            if "結構差異" in comparison.differences and comparison.differences["結構差異"]:
                report.append("💡 結構完整性修正建議:")
                report.append("  請補充缺少的內容部分")
                report.append("")
        
        return "\n".join(report)
    
    def export_comparison_data(self, comparison: FortuneComparison, output_file: str):
        """導出對比數據"""
        data = {
            "id": comparison.id,
            "title": comparison.title,
            "similarity_score": comparison.similarity_score,
            "differences": comparison.differences,
            "local_content": comparison.local_content,
            "reference_content": comparison.reference_content,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S")
        }
        
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            print(f"對比數據已導出到: {output_file}")
        except Exception as e:
            print(f"導出對比數據失敗: {e}")

def main():
    """主函數"""
    print("籤文對比工具啟動中...")
    
    # 初始化對比器
    comparer = FortuneComparer()
    
    # 載入本地籤文數據
    local_fortunes = comparer.load_local_fortunes("app/src/main/assets/fortunes.json")
    if not local_fortunes:
        print("沒有載入到本地籤文數據")
        return
    
    print(f"成功載入 {len(local_fortunes)} 個本地籤文")
    
    # 對比第八十二靈籤
    fortune_id = 82
    print(f"\n開始對比籤文 {fortune_id}...")
    
    comparison = comparer.compare_fortune(fortune_id, local_fortunes)
    if comparison:
        # 生成報告
        report = comparer.generate_comparison_report(comparison)
        print(report)
        
        # 保存報告
        timestamp = time.strftime("%Y%m%d_%H%M%S")
        report_file = f"籤文{fortune_id}對比報告_{timestamp}.txt"
        try:
            with open(report_file, 'w', encoding='utf-8') as f:
                f.write(report)
            print(f"\n對比報告已保存到: {report_file}")
        except Exception as e:
            print(f"保存報告失敗: {e}")
        
        # 導出詳細數據
        data_file = f"籤文{fortune_id}對比數據_{timestamp}.json"
        comparer.export_comparison_data(comparison, data_file)
        
        print(f"\n對比完成！籤文 {fortune_id} 的相似度為: {comparison.similarity_score:.1%}")
    else:
        print(f"無法對比籤文 {fortune_id}")

if __name__ == "__main__":
    main()
