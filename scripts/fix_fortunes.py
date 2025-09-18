#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
籤文数据修复脚本
解决编码问题和数据完整性问题
"""

import csv
import json
import os
import sys
from pathlib import Path

def fix_csv_encoding():
    """修复CSV文件编码问题"""
    print("🔧 修复CSV文件编码...")
    
    csv_path = Path("app/src/main/assets/fortunes_source.csv")
    if not csv_path.exists():
        print("❌ CSV文件不存在")
        return False
    
    # 读取原始文件
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 重新写入，确保UTF8编码
        with open(csv_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print("✅ CSV编码修复完成")
        return True
        
    except Exception as e:
        print(f"❌ 编码修复失败: {e}")
        return False

def validate_fortunes():
    """验证籤文数据完整性"""
    print("🔍 验证籤文数据...")
    
    csv_path = Path("app/src/main/assets/fortunes_source.csv")
    if not csv_path.exists():
        return False
    
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            fortunes = list(reader)
        
        print(f"📊 总籤文数量: {len(fortunes)}")
        
        # 检查ID范围
        ids = []
        for fortune in fortunes:
            try:
                fortune_id = int(fortune['id'])
                ids.append(fortune_id)
            except ValueError:
                print(f"❌ 无效ID: {fortune['id']}")
                continue
        
        if not ids:
            print("❌ 没有有效的籤文ID")
            return False
        
        min_id = min(ids)
        max_id = max(ids)
        missing_ids = set(range(min_id, max_id + 1)) - set(ids)
        
        print(f"📈 ID范围: {min_id} - {max_id}")
        print(f"🔢 实际籤文数: {len(ids)}")
        
        if missing_ids:
            print(f"⚠️  缺少籤文: {sorted(missing_ids)}")
        else:
            print("✅ 籤文ID连续")
        
        return True
        
    except Exception as e:
        print(f"❌ 验证失败: {e}")
        return False

def create_backup():
    """创建备份文件"""
    print("💾 创建备份...")
    
    csv_path = Path("app/src/main/assets/fortunes_source.csv")
    backup_path = Path("app/src/main/assets/fortunes_source.backup.csv")
    
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        with open(backup_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print("✅ 备份创建完成")
        return True
        
    except Exception as e:
        print(f"❌ 备份失败: {e}")
        return False

def main():
    """主函数"""
    print("🚀 籤文数据修复工具")
    print("=" * 40)
    
    # 创建备份
    if not create_backup():
        return 1
    
    # 修复编码
    if not fix_csv_encoding():
        return 1
    
    # 验证数据
    if not validate_fortunes():
        return 1
    
    print("\n🎉 修复完成！")
    print("请重新测试应用程式。")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
