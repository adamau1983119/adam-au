#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
為籤文37-100添加標籤的腳本
"""

import re
import sys

def add_tags_to_fortunes_37_100(input_file, output_file):
    """為籤文37-100添加標籤"""
    print(f"開始為籤文37-100添加標籤...")
    
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    new_lines = []
    
    in_fortune_37_plus = False
    fortune_number = 0
    
    for i, line in enumerate(lines):
        # 檢查是否是新籤文的開始
        match = re.match(r'^"(\d+)",', line)
        if match:
            fortune_number = int(match.group(1))
            in_fortune_37_plus = (fortune_number >= 37)
            
            if in_fortune_37_plus:
                print(f"處理籤文 {fortune_number}")
                # 為籤文標題行添加 [A1] 和 [A2-1] 標籤
                tagged_line = line
                tagged_line = re.sub(r'"求籤吉凶：', '"[A1]求籤吉凶：', tagged_line)
                tagged_line = re.sub(r'"算命籤詩：', '"[A2-1]算命籤詩：', tagged_line)
                new_lines.append(tagged_line)
            else:
                new_lines.append(line)
        elif in_fortune_37_plus:
            # 為籤文37-100的內容添加標籤
            tagged_line = line
            
            # 添加各種標籤
            if re.match(r'^黃大仙算命解籤詩：', tagged_line):
                tagged_line = re.sub(r'^黃大仙算命解籤詩：', '[A2-2]黃大仙算命解籤詩：', tagged_line)
            elif re.match(r'^黃大仙算命解運勢：', tagged_line):
                tagged_line = re.sub(r'^黃大仙算命解運勢：', '[A2-2]黃大仙算命解運勢：', tagged_line)
            elif re.match(r'^流年：', tagged_line):
                tagged_line = re.sub(r'^流年：', '[A3]流年：', tagged_line)
            elif re.match(r'^事業：', tagged_line):
                tagged_line = re.sub(r'^事業：', '[B]事業：', tagged_line)
            elif re.match(r'^財富：', tagged_line):
                tagged_line = re.sub(r'^財富：', '[C]財富：', tagged_line)
            elif re.match(r'^自身：', tagged_line):
                tagged_line = re.sub(r'^自身：', '[D]自身：', tagged_line)
            elif re.match(r'^家庭：', tagged_line):
                tagged_line = re.sub(r'^家庭：', '[E]家庭：', tagged_line)
            elif re.match(r'^姻緣：', tagged_line):
                tagged_line = re.sub(r'^姻緣：', '[F]姻緣：', tagged_line)
            elif re.match(r'^移居：', tagged_line):
                tagged_line = re.sub(r'^移居：', '[G]移居：', tagged_line)
            elif re.match(r'^名譽：', tagged_line):
                tagged_line = re.sub(r'^名譽：', '[H]名譽：', tagged_line)
            elif re.match(r'^健康：', tagged_line):
                tagged_line = re.sub(r'^健康：', '[I]健康：', tagged_line)
            elif re.match(r'^友誼：', tagged_line):
                tagged_line = re.sub(r'^友誼：', '[J]友誼：', tagged_line)
            elif re.match(r'^風水：', tagged_line):
                tagged_line = re.sub(r'^風水：', '[K]風水：', tagged_line)
            elif re.match(r'^遺失：', tagged_line):
                tagged_line = re.sub(r'^遺失：', '[L]遺失：', tagged_line)
            elif re.match(r'^天時：', tagged_line):
                tagged_line = re.sub(r'^天時：', '[N]天時：', tagged_line)
            elif re.match(r'^交易：', tagged_line):
                tagged_line = re.sub(r'^交易：', '[O]交易：', tagged_line)
            elif re.match(r'^出行：', tagged_line):
                tagged_line = re.sub(r'^出行：', '[P]出行：', tagged_line)
            
            new_lines.append(tagged_line)
        else:
            new_lines.append(line)
    
    # 寫入新文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(new_lines))
    
    print(f"標籤添加完成！輸出文件：{output_file}")
    print(f"處理了 {len(new_lines)} 行")

if __name__ == "__main__":
    input_file = "app/src/main/assets/fortunes_source_v21.csv"
    output_file = "app/src/main/assets/fortunes_source_v21_tagged.csv"
    
    try:
        add_tags_to_fortunes_37_100(input_file, output_file)
    except Exception as e:
        print(f"錯誤：{e}")
        sys.exit(1)
