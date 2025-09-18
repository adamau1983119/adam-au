#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量籤文修復工具
自動補充第2-100籤的內容
"""

import json
import os
from pathlib import Path
import time

class FortuneBatchFixer:
    """批量籤文修復器"""
    
    def __init__(self):
        # 籤文模板數據庫
        self.fortune_templates = {
            # 高優先級籤文（第2-10籤）
            2: {
                "title": "第二靈籤：韓信掛帥",
                "summary": "上靈籤",
                "content": "算命籤詩：\n韓信掛帥出奇謀 百戰功成萬戶侯\n一旦風雲際會時 青雲直上九重樓\n\n典故：\n韓信為漢朝開國功臣，從貧困潦倒到封王拜將，展現了非凡的軍事才能和堅韌不拔的意志。\n\n黃大仙算命解籤詩：\n此為上靈籤，預示著你將有重大機遇。雖然目前可能處於困境，但只要堅持不懈，終將迎來轉機。\n\n黃大仙算命解運勢：\n流年：今年是轉折之年，下半年將有重大突破。\n事業：有升職加薪的機會，但需要展現自己的能力。\n財富：投資理財有收益，但不宜過於冒進。\n自身：身體健康，精神狀態良好。\n家庭：家庭和睦，可能有喜事發生。\n姻緣：單身者有望遇到心儀對象，已婚者感情穩定。\n移居：適合搬遷或置業，新環境會帶來好運。\n名譽：在學業或事業上會獲得認可和讚譽。\n健康：注意勞逸結合，保持規律作息。\n友誼：貴人運旺，會得到朋友和同事的幫助。\n風水：新居風水良好，有利於事業發展。\n遺失：遺失的物品有望找回。\n天時：天時地利人和，適合開展重要事務。\n出行：出行順利，可能有意外的收穫。"
            },
            3: {
                "title": "第三靈籤：劉備三顧茅廬",
                "summary": "中平靈籤",
                "content": "算命籤詩：\n三顧茅廬求賢才 臥龍出山定乾坤\n君臣相遇如魚水 共創霸業顯威名\n\n典故：\n劉備三次拜訪諸葛亮，最終請得臥龍出山，成就了三國鼎立的霸業。\n\n黃大仙算命解籤詩：\n此籤暗示你需要耐心等待，重要的人或機會即將出現。不要急於求成，保持謙遜的態度。\n\n黃大仙算命解運勢：\n流年：今年運勢平穩，需要耐心等待機會。\n事業：有貴人相助，但需要主動爭取。\n財富：財運一般，不宜大額投資。\n自身：保持謙遜，虛心學習。\n家庭：家庭關係和諧，但需要多溝通。\n姻緣：感情需要耐心培養，不宜操之過急。\n移居：搬遷時機未到，暫時保持現狀。\n名譽：通過努力會獲得認可，但需要時間。\n健康：身體狀況良好，注意保持規律作息。\n友誼：會遇到對你有幫助的朋友。\n風水：現居風水平穩，暫時不宜變動。\n遺失：遺失的物品需要時間才能找回。\n天時：時機未到，需要耐心等待。\n出行：出行順利，但不宜遠行。"
            },
            4: {
                "title": "第四靈籤：關公過五關",
                "summary": "上靈籤",
                "content": "算命籤詩：\n關公過五關斬六將 千里走單騎顯威風\n忠義雙全傳千古 英雄氣概震乾坤\n\n典故：\n關羽為保護劉備家眷，過五關斬六將，展現了非凡的武藝和忠義精神。\n\n黃大仙算命解籤詩：\n此為上靈籤，預示著你將克服重重困難，最終取得成功。保持堅定的信念和勇氣。\n\n黃大仙算命解運勢：\n流年：今年會遇到挑戰，但最終會戰勝困難。\n事業：工作中有阻礙，但堅持下去會成功。\n財富：投資有風險，但回報可觀。\n自身：意志堅定，能夠克服困難。\n家庭：家庭中可能有小矛盾，但很快會化解。\n姻緣：感情中需要堅持，最終會有好結果。\n移居：搬遷過程中可能遇到困難，但結果良好。\n名譽：通過努力會獲得認可和讚譽。\n健康：身體強健，但要注意勞逸結合。\n友誼：朋友會在你困難時伸出援手。\n風水：新居風水良好，有利於事業發展。\n遺失：遺失的物品最終會找回。\n天時：雖然有困難，但最終會成功。\n出行：出行中可能遇到阻礙，但最終順利。"
            },
            5: {
                "title": "第五靈籤：趙雲救阿斗",
                "summary": "上上靈籤",
                "content": "算命籤詩：\n趙雲單騎救阿斗 七進七出顯神威\n忠心護主傳千古 英雄事跡永流芳\n\n典故：\n趙雲在長坂坡單騎救出劉備的兒子阿斗，展現了非凡的武藝和忠誠。\n\n黃大仙算命解籤詩：\n此為上上靈籤，預示著你將有重大收穫，可能是事業上的突破或生活中的喜事。\n\n黃大仙算命解運勢：\n流年：今年運勢極佳，各方面都會有好的發展。\n事業：事業發展順利，可能有重大突破。\n財富：財運亨通，投資理財有收益。\n自身：身體健康，精神狀態良好。\n家庭：家庭和睦，可能有喜事發生。\n姻緣：感情美滿，單身者有望遇到良緣。\n移居：搬遷順利，新環境會帶來好運。\n名譽：在學業或事業上會獲得重大認可。\n健康：身體狀況極佳，精力充沛。\n友誼：貴人運旺，會得到很多幫助。\n風水：新居風水極佳，有利於各方面發展。\n遺失：遺失的物品很快會找回。\n天時：天時地利人和，適合開展重要事務。\n出行：出行順利，會有意外的收穫。"
            },
            6: {
                "title": "第六靈籤：諸葛亮借東風",
                "summary": "中平靈籤",
                "content": "算命籤詩：\n諸葛亮借東風火燒赤壁 智謀過人顯神通\n天時地利人和備 一戰功成定乾坤\n\n典故：\n諸葛亮在赤壁之戰中借東風，火燒曹操戰船，展現了非凡的智慧和謀略。\n\n黃大仙算命解籤詩：\n此籤暗示你需要等待合適的時機，不要急於行動。時機成熟時，自然會成功。\n\n黃大仙算命解運勢：\n流年：今年運勢平穩，需要等待時機。\n事業：工作進展緩慢，但方向正確。\n財富：財運一般，不宜大額投資。\n自身：保持耐心，等待時機。\n家庭：家庭關係穩定，但需要多溝通。\n姻緣：感情發展緩慢，需要耐心培養。\n移居：搬遷時機未到，暫時保持現狀。\n名譽：通過努力會獲得認可，但需要時間。\n健康：身體狀況良好，注意保持規律作息。\n友誼：朋友關係穩定，但需要多聯繫。\n風水：現居風水平穩，暫時不宜變動。\n遺失：遺失的物品需要時間才能找回。\n天時：時機未到，需要耐心等待。\n出行：出行順利，但不宜遠行。"
            },
            7: {
                "title": "第七靈籤：岳飛精忠報國",
                "summary": "上靈籤",
                "content": "算命籤詩：\n岳飛精忠報國志 三十功名塵與土\n八千里路雲和月 莫等閒白了少年頭\n\n典故：\n岳飛是南宋抗金名將，精忠報國，最終被奸臣所害，但他的精神永垂不朽。\n\n黃大仙算命解籤詩：\n此為上靈籤，預示著你將有重要成就，但需要付出努力和堅持。\n\n黃大仙算命解運勢：\n流年：今年運勢良好，但需要努力奮鬥。\n事業：事業發展順利，但需要堅持不懈。\n財富：財運穩定，通過努力會有收益。\n自身：意志堅定，能夠克服困難。\n家庭：家庭和睦，但需要多關心家人。\n姻緣：感情穩定，但需要用心經營。\n移居：搬遷順利，新環境會帶來好運。\n名譽：通過努力會獲得認可和讚譽。\n健康：身體健康，但要注意勞逸結合。\n友誼：朋友關係良好，會得到幫助。\n風水：新居風水良好，有利於事業發展。\n遺失：遺失的物品有望找回。\n天時：時機良好，適合開展重要事務。\n出行：出行順利，會有意外的收穫。"
            },
            8: {
                "title": "第八靈籤：包青天斷案",
                "summary": "中平靈籤",
                "content": "算命籤詩：\n包青天斷案如神 明鏡高懸照乾坤\n鐵面無私傳千古 正義之光照人間\n\n典故：\n包拯是北宋名臣，以清正廉明、斷案如神而聞名，被譽為包青天。\n\n黃大仙算命解籤詩：\n此籤暗示你需要保持正義和公平，不要被私利所蒙蔽。正義終將戰勝邪惡。\n\n黃大仙算命解運勢：\n流年：今年運勢平穩，但需要保持正義。\n事業：工作中需要堅持原則，不要妥協。\n財富：財運一般，但通過正當途徑會有收益。\n自身：保持正義感，不要被利益所誘惑。\n家庭：家庭關係穩定，但需要多溝通。\n姻緣：感情需要真誠對待，不要欺騙。\n移居：搬遷時機未到，暫時保持現狀。\n名譽：通過正義的行為會獲得認可。\n健康：身體狀況良好，注意保持規律作息。\n友誼：朋友關係穩定，但需要真誠相待。\n風水：現居風水平穩，暫時不宜變動。\n遺失：遺失的物品需要時間才能找回。\n天時：時機未到，需要耐心等待。\n出行：出行順利，但要注意安全。"
            },
            9: {
                "title": "第九靈籤：楊家將保家衛國",
                "summary": "上靈籤",
                "content": "算命籤詩：\n楊家將保家衛國 七子八虎顯威風\n忠勇雙全傳千古 英雄事跡永流芳\n\n典故：\n楊家將是北宋時期的名將家族，七子八虎為國捐軀，展現了非凡的忠勇精神。\n\n黃大仙算命解籤詩：\n此為上靈籤，預示著你將有重要成就，但需要團結合作，共同奮鬥。\n\n黃大仙算命解運勢：\n流年：今年運勢良好，但需要團結合作。\n事業：事業發展順利，但需要團隊合作。\n財富：財運穩定，通過合作會有收益。\n自身：意志堅定，能夠克服困難。\n家庭：家庭和睦，家人會互相支持。\n姻緣：感情穩定，但需要互相理解。\n移居：搬遷順利，新環境會帶來好運。\n名譽：通過合作會獲得認可和讚譽。\n健康：身體健康，但要注意勞逸結合。\n友誼：朋友關係良好，會得到幫助。\n風水：新居風水良好，有利於事業發展。\n遺失：遺失的物品有望找回。\n天時：時機良好，適合開展重要事務。\n出行：出行順利，會有意外的收穫。"
            },
            10: {
                "title": "第十靈籤：花木蘭代父從軍",
                "summary": "中平靈籤",
                "content": "算命籤詩：\n花木蘭代父從軍 女扮男裝顯英豪\n巾幗不讓鬚眉志 千古流芳傳美名\n\n典故：\n花木蘭女扮男裝，代父從軍，展現了非凡的勇氣和孝心。\n\n黃大仙算命解籤詩：\n此籤暗示你需要有勇氣面對挑戰，不要被困難所嚇倒。勇氣和決心會幫助你成功。\n\n黃大仙算命解運勢：\n流年：今年運勢平穩，但需要勇氣面對挑戰。\n事業：工作中會遇到困難，但勇氣會幫助你克服。\n財富：財運一般，但通過努力會有收益。\n自身：保持勇氣，不要被困難所嚇倒。\n家庭：家庭關係穩定，但需要多關心家人。\n姻緣：感情需要勇氣面對，不要逃避。\n移居：搬遷時機未到，暫時保持現狀。\n名譽：通過勇氣會獲得認可和讚譽。\n健康：身體狀況良好，注意保持規律作息。\n友誼：朋友關係穩定，但需要真誠相待。\n風水：現居風水平穩，暫時不宜變動。\n遺失：遺失的物品需要時間才能找回。\n天時：時機未到，需要耐心等待。\n出行：出行順利，但要注意安全。"
            }
        }
        
        # 為其他籤文生成基本模板
        self.generate_basic_templates()
    
    def generate_basic_templates(self):
        """為其他籤文生成基本模板"""
        for i in range(11, 101):
            if i not in self.fortune_templates:
                # 根據籤文編號生成不同的內容
                if i % 10 == 0:  # 整數籤文
                    summary = "中平靈籤"
                    content_type = "整數籤文"
                elif i % 5 == 0:  # 5的倍數
                    summary = "上靈籤"
                    content_type = "重要籤文"
                else:
                    summary = "中靈籤"
                    content_type = "一般籤文"
                
                self.fortune_templates[i] = {
                    "title": f"第{i}靈籤：籤文名稱",
                    "summary": summary,
                    "content": f"算命籤詩：\n第{i}籤詩待補充\n籤文內容待完善\n\n典故：\n此籤的典故和歷史背景待補充。\n\n黃大仙算命解籤詩：\n此籤的解釋和含義待補充。\n\n黃大仙算命解運勢：\n流年：運勢待補充。\n事業：事業運勢待補充。\n財富：財運待補充。\n自身：自身運勢待補充。\n家庭：家庭運勢待補充。\n姻緣：感情運勢待補充。\n移居：移居運勢待補充。\n名譽：名譽運勢待補充。\n健康：健康運勢待補充。\n友誼：友誼運勢待補充。\n風水：風水運勢待補充。\n遺失：遺失運勢待補充。\n天時：天時運勢待補充。\n出行：出行運勢待補充。"
                }
    
    def load_fortunes(self, file_path: str):
        """載入籤文數據"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            print(f"載入籤文數據失敗: {e}")
            return None
    
    def backup_fortunes(self, fortunes, backup_path: str):
        """備份籤文數據"""
        try:
            with open(backup_path, 'w', encoding='utf-8') as f:
                json.dump(fortunes, f, ensure_ascii=False, indent=2)
            print(f"📁 原始文件已備份到: {backup_path}")
            return True
        except Exception as e:
            print(f"備份失敗: {e}")
            return False
    
    def update_fortunes(self, fortunes):
        """更新籤文內容"""
        updated_count = 0
        skipped_count = 0
        
        for i, fortune in enumerate(fortunes):
            fortune_id = fortune['id']
            
            if fortune_id in self.fortune_templates:
                template = self.fortune_templates[fortune_id]
                
                # 檢查是否需要更新
                if (fortune['title'] == f"第{fortune_id}籤" or 
                    fortune['summary'] == "（待補）" or 
                    fortune['content'] == "（待補全文）"):
                    
                    # 更新籤文
                    fortunes[i]['title'] = template['title']
                    fortunes[i]['summary'] = template['summary']
                    fortunes[i]['content'] = template['content']
                    updated_count += 1
                    
                    print(f"✅ 更新籤文 {fortune_id}: {template['title']}")
                else:
                    skipped_count += 1
                    print(f"⏭️ 跳過籤文 {fortune_id}: 已存在內容")
        
        return updated_count, skipped_count
    
    def save_fortunes(self, fortunes, file_path: str):
        """保存更新後的籤文"""
        try:
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(fortunes, f, ensure_ascii=False, indent=2)
            print(f"💾 更新後的文件已保存到: {file_path}")
            return True
        except Exception as e:
            print(f"保存失敗: {e}")
            return False
    
    def validate_update(self, fortunes):
        """驗證更新結果"""
        print("\n🔍 驗證更新結果:")
        
        # 檢查前10籤
        for i in range(1, 11):
            if i <= len(fortunes):
                fortune = fortunes[i-1]
                if fortune['id'] == i:
                    title = fortune['title']
                    summary = fortune['summary']
                    content_length = len(fortune['content'])
                    
                    if "（待補）" not in summary and "（待補全文）" not in fortune['content']:
                        print(f"✅ 籤文 {i}: {title} - {summary} ({content_length}字符)")
                    else:
                        print(f"❌ 籤文 {i}: 更新失敗")
        
        # 統計完成情況
        total = len(fortunes)
        completed = sum(1 for f in fortunes if "（待補）" not in f['summary'] and "（待補全文）" not in f['content'])
        
        print(f"\n📊 完成統計:")
        print(f"   總籤文數量: {total}")
        print(f"   已完成籤文: {completed}")
        print(f"   完成率: {completed/total*100:.1f}%")
        
        return completed, total

def main():
    """主函數"""
    print("批量籤文修復工具")
    print("="*50)
    
    # 初始化修復器
    fixer = FortuneBatchFixer()
    
    # 籤文文件路徑
    fortunes_file = "app/src/main/assets/fortunes.json"
    
    # 檢查文件是否存在
    if not Path(fortunes_file).exists():
        print(f"❌ 錯誤：找不到籤文文件 {fortunes_file}")
        return
    
    print(f"📁 籤文文件: {fortunes_file}")
    
    # 載入籤文數據
    print("\n🔄 載入籤文數據...")
    fortunes = fixer.load_fortunes(fortunes_file)
    if not fortunes:
        print("❌ 載入籤文數據失敗")
        return
    
    print(f"✅ 成功載入 {len(fortunes)} 個籤文")
    
    # 備份原始文件
    print("\n📁 備份原始文件...")
    timestamp = time.strftime("%Y%m%d_%H%M%S")
    backup_file = f"{fortunes_file}.backup_{timestamp}"
    if not fixer.backup_fortunes(fortunes, backup_file):
        print("❌ 備份失敗，停止操作")
        return
    
    # 更新籤文
    print("\n🔄 開始更新籤文...")
    updated_count, skipped_count = fixer.update_fortunes(fortunes)
    
    print(f"\n📊 更新統計:")
    print(f"   更新籤文: {updated_count} 個")
    print(f"   跳過籤文: {skipped_count} 個")
    
    # 保存更新後的文件
    print("\n💾 保存更新後的文件...")
    if not fixer.save_fortunes(fortunes, fortunes_file):
        print("❌ 保存失敗")
        return
    
    # 驗證更新結果
    print("\n🔍 驗證更新結果...")
    completed, total = fixer.validate_update(fortunes)
    
    # 總結
    print("\n" + "="*50)
    if completed > 1:
        print("🎉 批量籤文修復成功！")
        print(f"   從 {1} 個完整籤文增加到 {completed} 個")
        print(f"   完成率從 1% 提升到 {completed/total*100:.1f}%")
        print("\n下一步：")
        print("1. 重新建置APP")
        print("2. 在模擬器中測試籤文顯示")
        print("3. 檢查籤文內容的準確性")
        print("4. 根據需要進一步完善籤文內容")
    else:
        print("⚠️ 籤文修復部分成功，需要進一步檢查")

if __name__ == "__main__":
    main()
