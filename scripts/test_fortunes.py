#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
籤文数据完整性测试脚本
验证 #1-#100 籤文内容是否完整
"""

import csv
import json
import os
import sys
from pathlib import Path

def test_csv_fortunes():
    """测试CSV格式籤文数据"""
    print("🔍 测试CSV籤文数据...")
    
    csv_path = Path("app/src/main/assets/fortunes_source.csv")
    if not csv_path.exists():
        print("❌ CSV文件不存在:", csv_path)
        return False
    
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            fortunes = list(reader)
        
        print(f"📊 总籤文数量: {len(fortunes)}")
        
        # 检查必需字段
        required_fields = ['id', 'title', 'summary', 'content']
        missing_fields = []
        for field in required_fields:
            if field not in fortunes[0].keys():
                missing_fields.append(field)
        
        if missing_fields:
            print(f"❌ 缺少必需字段: {missing_fields}")
            return False
        
        # 检查籤文ID完整性
        expected_ids = set(range(1, 101))
        actual_ids = set()
        missing_ids = []
        empty_content = []
        
        for fortune in fortunes:
            try:
                fortune_id = int(fortune['id'])
                actual_ids.add(fortune_id)
                
                # 检查内容是否为空
                if not fortune['title'].strip() or not fortune['summary'].strip() or not fortune['content'].strip():
                    empty_content.append(fortune_id)
                    
            except ValueError:
                print(f"❌ 无效的籤文ID: {fortune['id']}")
                return False
        
        missing_ids = expected_ids - actual_ids
        
        if missing_ids:
            print(f"❌ 缺少籤文: {sorted(missing_ids)}")
            return False
        
        if empty_content:
            print(f"⚠️  籤文内容为空: {sorted(empty_content)}")
        
        print("✅ CSV籤文数据验证通过")
        print(f"   - 籤文数量: {len(fortunes)}")
        print(f"   - ID范围: 1-100")
        print(f"   - 字段完整: {required_fields}")
        
        return True
        
    except Exception as e:
        print(f"❌ CSV测试失败: {e}")
        return False

def test_json_fortunes():
    """测试JSON格式籤文数据"""
    print("\n🔍 测试JSON籤文数据...")
    
    json_path = Path("app/src/main/assets/fortunes.json")
    if not json_path.exists():
        print("❌ JSON文件不存在:", json_path)
        return False
    
    try:
        with open(json_path, 'r', encoding='utf-8') as f:
            fortunes = json.load(f)
        
        print(f"📊 总籤文数量: {len(fortunes)}")
        
        # 检查数据结构
        if not isinstance(fortunes, list):
            print("❌ JSON数据不是数组格式")
            return False
        
        if len(fortunes) == 0:
            print("❌ JSON数据为空")
            return False
        
        # 检查第一个籤文的结构
        first_fortune = fortunes[0]
        required_fields = ['id', 'title', 'summary', 'content']
        missing_fields = []
        
        for field in required_fields:
            if field not in first_fortune:
                missing_fields.append(field)
        
        if missing_fields:
            print(f"❌ 缺少必需字段: {missing_fields}")
            return False
        
        # 检查籤文ID完整性
        expected_ids = set(range(1, 101))
        actual_ids = set()
        missing_ids = []
        
        for fortune in fortunes:
            try:
                fortune_id = int(fortune['id'])
                actual_ids.add(fortune_id)
            except (ValueError, KeyError):
                print(f"❌ 无效的籤文ID: {fortune.get('id', 'N/A')}")
                return False
        
        missing_ids = expected_ids - actual_ids
        
        if missing_ids:
            print(f"❌ 缺少籤文: {sorted(missing_ids)}")
            return False
        
        print("✅ JSON籤文数据验证通过")
        print(f"   - 籤文数量: {len(fortunes)}")
        print(f"   - ID范围: 1-100")
        print(f"   - 字段完整: {required_fields}")
        
        return True
        
    except Exception as e:
        print(f"❌ JSON测试失败: {e}")
        return False

def test_assets_integrity():
    """测试资源文件完整性"""
    print("\n🔍 测试资源文件完整性...")
    
    assets_dir = Path("app/src/main/assets")
    if not assets_dir.exists():
        print("❌ assets目录不存在")
        return False
    
    required_files = [
        "fortunes_source.csv",
        "fortunes.json"
    ]
    
    missing_files = []
    for file_name in required_files:
        file_path = assets_dir / file_name
        if not file_path.exists():
            missing_files.append(file_name)
        else:
            file_size = file_path.stat().st_size
            print(f"   ✅ {file_name}: {file_size:,} bytes")
    
    if missing_files:
        print(f"❌ 缺少文件: {missing_files}")
        return False
    
    print("✅ 资源文件完整性验证通过")
    return True

def test_sample_content():
    """测试样本籤文内容"""
    print("\n🔍 测试样本籤文内容...")
    
    csv_path = Path("app/src/main/assets/fortunes_source.csv")
    if not csv_path.exists():
        return False
    
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            fortunes = list(reader)
        
        # 测试几个关键籤文
        test_ids = [1, 50, 100]
        
        for test_id in test_ids:
            fortune = next((f for f in fortunes if int(f['id']) == test_id), None)
            if fortune:
                print(f"   ✅ 第{test_id}籤: {fortune['title'][:30]}...")
                
                # 检查内容长度
                title_len = len(fortune['title'])
                summary_len = len(fortune['summary'])
                content_len = len(fortune['content'])
                
                if title_len < 5 or summary_len < 5 or content_len < 10:
                    print(f"      ⚠️  内容过短: 标题({title_len}), 摘要({summary_len}), 内容({content_len})")
            else:
                print(f"   ❌ 第{test_id}籤不存在")
                return False
        
        print("✅ 样本籤文内容验证通过")
        return True
        
    except Exception as e:
        print(f"❌ 样本内容测试失败: {e}")
        return False

def main():
    """主测试函数"""
    print("🚀 黃大仙靈簽 APP 籤文数据完整性测试")
    print("=" * 50)
    
    tests = [
        test_assets_integrity,
        test_csv_fortunes,
        test_json_fortunes,
        test_sample_content
    ]
    
    passed = 0
    total = len(tests)
    
    for test in tests:
        try:
            if test():
                passed += 1
        except Exception as e:
            print(f"❌ 测试异常: {e}")
    
    print("\n" + "=" * 50)
    print(f"📊 测试结果: {passed}/{total} 通过")
    
    if passed == total:
        print("🎉 所有测试通过！籤文数据完整，可以上架。")
        return 0
    else:
        print("⚠️  部分测试失败，请检查籤文数据。")
        return 1

if __name__ == "__main__":
    sys.exit(main())
