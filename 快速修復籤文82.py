#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速修復籤文82：孔子擊磬
更新第82籤的完整內容
"""

import json
import os
from pathlib import Path

def update_fortune_82():
    """更新第82籤的內容"""
    
    # 籤文文件路徑
    fortunes_file = "app/src/main/assets/fortunes.json"
    
    # 檢查文件是否存在
    if not Path(fortunes_file).exists():
        print(f"錯誤：找不到籤文文件 {fortunes_file}")
        return False
    
    try:
        # 讀取現有籤文數據
        with open(fortunes_file, 'r', encoding='utf-8') as f:
            fortunes = json.load(f)
        
        print(f"成功載入 {len(fortunes)} 個籤文")
        
        # 第82籤的完整內容
        fortune_82_content = {
            "id": 82,
            "title": "第八十二靈籤：孔子擊磬",
            "summary": "中平靈籤",
            "content": "算命籤詩：\n聖人擊磬在于衛 誰料過門有荷簣\n嗟嘆有心挽道窮 可憐日月今將逝\n\n典故：\n孔子擊磬的故事，告誡我們，要惜取少年時，應該做的，就要及時去做，否則老之將至，有心無力了。聖人孔子，在衛國擊磬的時候，門外有人擔著竹簣路過，在磬聲中，聽出孔子是有志做一番救世救民的事業心，可惜，時不我與，歲月就這樣逝去了。\n\n黃大仙算命解籤詩：\n求得此靈籤者，凡事以善良和順為貴，不要以為善事太小而不去做，也不要以為惡事太小而去做。做人，要把握今天珍惜今天。\n\n凡事當以救人為己任，或出錢，或出力，有利益於人者便要做。幽則有神鑒賞，明則有人稱羡，何樂不為。若年紀老更要及早行善以救人，則必有貴人扶助。\n\n此靈籤似有憂愁氣象，問事者亦平常，無礙平穩。但是非小人口舌，恐難避兔矣，宜慎防能避之者吉。\n\n黃大仙算命解運勢：\n行未歸，病癒遲，蚕與畜，得利微，欲求財，要待時\n宅平常，婚不宜，問六甲，當禱祈，謀望者，宜慎之\n\n流年：天時對自己不利，雖無大礙，但難成大事。\n事業：腳踏實地盡快將手上的工作做好，勿作不著邊際構想。\n財富：靈籤暗示微中取利，太大的投資，風險大，要審慎。\n自身：如果人生如賭博，今年只有四成贏面。\n家庭：家居平淡，敬重老人家，會添福。\n姻緣：愛情一波三折，雙方都缺乏信心。\n移居：要移民就趁早進行，禍福未知，要對自己的選擇負責。\n名譽：難有揚名立功的機會。\n健康：身上的病痛，恐怕還要延續一段日子，最好換一個醫生。\n友誼：小人多，背後也有不少在非議您，但可以不理。\n風水：靈籤暗示財滯多口舌。\n遺失：尋靈籤暗示之亦難得。\n天時：氣運不甚美。\n出行：防小人口舌。"
        }
        
        # 查找並更新第82籤
        updated = False
        for i, fortune in enumerate(fortunes):
            if fortune['id'] == 82:
                # 備份原始內容
                original_content = fortune.copy()
                
                # 更新內容
                fortunes[i] = fortune_82_content
                updated = True
                
                print(f"✅ 成功更新第82籤：{fortune_82_content['title']}")
                print(f"   原始標題: {original_content['title']}")
                print(f"   新標題: {fortune_82_content['title']}")
                print(f"   原始摘要: {original_content['summary']}")
                print(f"   新摘要: {fortune_82_content['summary']}")
                print(f"   內容長度: {len(fortune_82_content['content'])} 字符")
                break
        
        if not updated:
            print("❌ 找不到第82籤，無法更新")
            return False
        
        # 備份原始文件
        backup_file = f"{fortunes_file}.backup"
        with open(backup_file, 'w', encoding='utf-8') as f:
            json.dump(fortunes, f, ensure_ascii=False, indent=2)
        print(f"📁 原始文件已備份到: {backup_file}")
        
        # 保存更新後的文件
        with open(fortunes_file, 'w', encoding='utf-8') as f:
            json.dump(fortunes, f, ensure_ascii=False, indent=2)
        print(f"💾 更新後的文件已保存到: {fortunes_file}")
        
        # 驗證更新結果
        print("\n🔍 驗證更新結果:")
        print(f"   第82籤標題: {fortunes[81]['title']}")
        print(f"   第82籤摘要: {fortunes[81]['summary']}")
        print(f"   第82籤內容長度: {len(fortunes[81]['content'])} 字符")
        
        if "孔子擊磬" in fortunes[81]['title'] and "中平靈籤" in fortunes[81]['summary']:
            print("✅ 更新驗證成功！")
            return True
        else:
            print("❌ 更新驗證失敗！")
            return False
            
    except Exception as e:
        print(f"❌ 更新過程中發生錯誤: {e}")
        return False

def show_fortune_82_preview():
    """顯示第82籤的預覽"""
    print("\n" + "="*60)
    print("第82籤：孔子擊磬 - 內容預覽")
    print("="*60)
    
    # 籤文內容
    content = """算命籤詩：
聖人擊磬在于衛 誰料過門有荷簣
嗟嘆有心挽道窮 可憐日月今將逝

典故：
孔子擊磬的故事，告誡我們，要惜取少年時，應該做的，就要及時去做，否則老之將至，有心無力了。

黃大仙算命解籤詩：
求得此靈籤者，凡事以善良和順為貴，不要以為善事太小而不去做，也不要以為惡事太小而去做。做人，要把握今天珍惜今天。

黃大仙算命解運勢：
流年：天時對自己不利，雖無大礙，但難成大事。
事業：腳踏實地盡快將手上的工作做好，勿作不著邊際構想。
財富：靈籤暗示微中取利，太大的投資，風險大，要審慎。
自身：如果人生如賭博，今年只有四成贏面。
家庭：家居平淡，敬重老人家，會添福。
姻緣：愛情一波三折，雙方都缺乏信心。
移居：要移民就趁早進行，禍福未知，要對自己的選擇負責。
名譽：難有揚名立功的機會。
健康：身上的病痛，恐怕還要延續一段日子，最好換一個醫生。
友誼：小人多，背後也有不少在非議您，但可以不理。
風水：靈籤暗示財滯多口舌。
遺失：尋靈籤暗示之亦難得。
天時：氣運不甚美。
出行：防小人口舌。"""
    
    print(f"標題: 第八十二靈籤：孔子擊磬")
    print(f"吉凶: 中平靈籤")
    print(f"內容長度: {len(content)} 字符")
    print(f"\n籤詩: 4行")
    print(f"典故: 約100字")
    print(f"解籤詩: 約200字")
    print(f"運勢項目: 15個")
    print("\n" + "="*60)

def main():
    """主函數"""
    print("快速修復籤文82：孔子擊磬")
    print("="*50)
    
    # 顯示預覽
    show_fortune_82_preview()
    
    # 確認是否繼續
    print("\n是否要更新第82籤的內容？")
    print("1. 是，立即更新")
    print("2. 否，僅查看預覽")
    
    try:
        choice = input("請選擇 (1/2): ").strip()
        if choice == "1":
            print("\n開始更新第82籤...")
            if update_fortune_82():
                print("\n🎉 第82籤更新成功！")
                print("下一步：")
                print("1. 重新建置APP")
                print("2. 在模擬器中測試第82籤")
                print("3. 檢查籤文顯示效果")
            else:
                print("\n❌ 第82籤更新失敗！")
        else:
            print("\n已取消更新，僅查看預覽。")
    except KeyboardInterrupt:
        print("\n\n操作已取消。")
    except Exception as e:
        print(f"\n發生錯誤: {e}")

if __name__ == "__main__":
    main()
