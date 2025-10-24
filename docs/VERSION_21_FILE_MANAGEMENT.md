# Version 21 籤文標籤化文件管理策略

## 📁 文件命名規範

### 主要文件
- `fortunes_source_v21.csv` - **Version 21 當前使用的籤文文件**（只讀，不直接編輯）
- `fortunes_source_v21_working.csv` - **工作文件**（用於標籤化編輯）

### 版本文件
- `fortunes_source_v21_v1.0_original.csv` - 原始無標籤版本
- `fortunes_source_v21_v1.1_tagged_1_10.csv` - 籤文1-10標籤化版本
- `fortunes_source_v21_v1.2_tagged_1_20.csv` - 籤文1-20標籤化版本
- `fortunes_source_v21_v1.3_tagged_1_50.csv` - 籤文1-50標籤化版本
- `fortunes_source_v21_v2.0_tagged_complete.csv` - 完整標籤化版本

### 備份文件
- `fortunes_source_v21_backup_YYYYMMDD_HHMMSS.csv` - 時間戳備份

## 🔄 工作流程

### 標籤化工作流程
1. **開始工作前**：備份當前版本
2. **編輯工作文件**：只編輯 `fortunes_source_v21_working.csv`
3. **完成階段性工作**：保存為版本文件
4. **測試通過後**：複製到主文件 `fortunes_source_v21.csv`

### 安全措施
1. **永遠不直接編輯** `fortunes_source_v21.csv`
2. **每次修改前**：創建時間戳備份
3. **階段性保存**：每完成10籤標籤化就保存版本
4. **測試驗證**：每次更新主文件前都要測試

## ⚠️ 禁止操作
- ❌ 直接覆蓋 `fortunes_source_v21.csv`
- ❌ 刪除版本文件
- ❌ 不備份就修改主文件
