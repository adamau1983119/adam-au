#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
籤文標籤添加腳本
為Version 21的籤文內容添加NLP分析標籤
"""

import csv
import re
import os

# 標籤對應表
TAG_MAPPING = {
    # A系列 - 基礎信息
    'A1': '求籤吉凶',
    'A2': '黃大仙算命解籤詩', 
    'A3': '流年',
    
    # B-P系列 - 具體分類
    'B': '事業',
    'C': '財富', 
    'D': '自身',
    'E': '家庭',
    'F': '姻緣',
    'G': '移居',
    'H': '名譽',
    'I': '健康',
    'J': '友誼',
    'K': '風水',
    'L': '遺失',
    'M': '自身',  # 與D類似，補充
    'N': '天時',
    'O': '交易',
    'P': '出行'
}

# 關鍵詞匹配規則
KEYWORD_RULES = {
    'A1': [r'求籤吉凶', r'上上籤', r'上籤', r'中籤', r'下籤', r'下下籤', r'吉凶'],
    'A2': [r'黃大仙算命解籤詩', r'算命籤詩', r'可喜可賀', r'左右逢源'],
    'A3': [r'流年', r'今年運勢', r'去年失去', r'今年得到'],
    'B': [r'事業', r'功名', r'貴人', r'升遷', r'謀事', r'工作'],
    'C': [r'財富', r'求財', r'正財', r'橫財', r'財運'],
    'D': [r'自身', r'四季平安', r'順遂'],
    'E': [r'家庭', r'家宅', r'添丁', r'和氣'],
    'F': [r'姻緣', r'愛情', r'婚姻', r'適婚'],
    'G': [r'移居', r'搬遷', r'移民', r'置業'],
    'H': [r'名譽', r'嘉獎', r'學業', r'社會服務'],
    'I': [r'健康', r'病即愈', r'小病'],
    'J': [r'友誼', r'貴人', r'廣結善緣'],
    'K': [r'風水', r'丁財兩旺', r'風水發貴'],
    'L': [r'遺失', r'失物', r'尋回'],
    'M': [r'自身'],  # 與D重複，用於補充
    'N': [r'天時', r'豐稔'],
    'O': [r'交易', r'買賣'],
    'P': [r'出行', r'行人', r'往來']
}

def add_tags_to_content(content):
    """為籤文內容添加標籤"""
    lines = content.split('\n')
    tagged_lines = []
    
    for line in lines:
        line = line.strip()
        if not line:
            tagged_lines.append(line)
            continue
            
        # 檢查是否已經有標籤
        if re.match(r'^\[[A-P]\d*\]', line):
            tagged_lines.append(line)
            continue
            
        # 根據關鍵詞匹配添加標籤
        tag_added = False
        for tag, keywords in KEYWORD_RULES.items():
            for keyword in keywords:
                if re.search(keyword, line):
                    tagged_line = f'[{tag}]{line}'
                    tagged_lines.append(tagged_line)
                    tag_added = True
                    break
            if tag_added:
                break
                
        if not tag_added:
            tagged_lines.append(line)
    
    return '\n'.join(tagged_lines)

def process_fortunes_csv(input_file, output_file):
    """處理籤文CSV文件"""
    print(f"開始處理籤文文件: {input_file}")
    
    with open(input_file, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        rows = list(reader)
    
    print(f"讀取到 {len(rows)} 條籤文記錄")
    
    # 處理每條記錄
    for i, row in enumerate(rows):
        if i < 5:  # 只處理前5籤作為範例
            print(f"處理第 {row['id']} 籤...")
            
            # 為content添加標籤
            original_content = row['content']
            tagged_content = add_tags_to_content(original_content)
            row['content'] = tagged_content
            
            # 為summary添加標籤
            original_summary = row['summary']
            tagged_summary = add_tags_to_content(original_summary)
            row['summary'] = tagged_summary
    
    # 寫入新文件
    with open(output_file, 'w', encoding='utf-8', newline='') as outfile:
        fieldnames = ['id', 'title', 'summary', 'content']
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)
    
    print(f"處理完成，輸出文件: {output_file}")

def main():
    input_file = "app/src/main/assets/fortunes_source_v21.csv"
    output_file = "app/src/main/assets/fortunes_source_v21_tagged.csv"
    
    if not os.path.exists(input_file):
        print(f"錯誤：找不到輸入文件 {input_file}")
        return
    
    process_fortunes_csv(input_file, output_file)
    print("籤文標籤添加完成！")

if __name__ == "__main__":
    main()
