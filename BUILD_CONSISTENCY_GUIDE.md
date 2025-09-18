# 建置一致性指南

## 問題說明
您遇到的問題是打包版本與模擬器版本不一致，這是 Android 開發中常見但嚴重的問題。

## 已實施的解決方案

### 1. 修復 ProGuard 配置
- ✅ 修正了包名從 `com.example.wtsaskingforsignature` 到 `com.wts.dsfortune`
- ✅ 確保代碼混淆規則正確

### 2. 統一建置配置
- ✅ Debug 和 Release 版本使用相同簽名
- ✅ 添加 BuildConfig 字段區分版本類型
- ✅ 正確配置資源壓縮設置

### 3. 分離 Manifest 配置
- ✅ 創建 `debug/AndroidManifest.xml` 和 `release/AndroidManifest.xml`
- ✅ Debug 版本顯示 "(Debug)" 標籤
- ✅ 不同版本使用適當的 AdMob 配置

### 4. 建置驗證工具
- ✅ `verify_build_consistency.ps1` - 檢查資源一致性
- ✅ `build_release.ps1` - 標準化建置流程
- ✅ `compare_builds.ps1` - APK 對比工具
- ✅ `pre_commit_check.ps1` - 提交前檢查

## 使用方法

### 日常建置流程
```powershell
# 1. 運行標準建置腳本
.\scripts\build_release.ps1 -Clean

# 2. 對比兩個版本
.\scripts\compare_builds.ps1

# 3. 安裝並測試
# Debug: app\build\outputs\apk\debug\app-debug.apk
# Release: app\build\outputs\apk\release\app-release.apk
```

### 提交前檢查
```powershell
# 運行提交前檢查
.\scripts\pre_commit_check.ps1
```

## 關鍵檢查點

### 建置時檢查
1. ✅ 使用相同的建置環境
2. ✅ 確保簽名配置一致
3. ✅ 驗證 ProGuard 規則正確
4. ✅ 檢查資源文件完整性

### 安裝後檢查
1. 📱 應用圖標是否相同
2. 🖼️ 所有圖像是否正確顯示
3. 📝 文字內容是否一致
4. 🌐 國際化功能是否正常
5. 📺 廣告功能是否運作

## 故障排除

### 如果仍有差異
1. **檢查 ProGuard 日誌**
   ```
   app/build/outputs/mapping/release/
   ```

2. **比較 APK 內容**
   ```powershell
   # 使用 Android SDK 工具
   aapt dump badging app-debug.apk
   aapt dump badging app-release.apk
   ```

3. **檢查資源壓縮**
   - 確認重要資源未被錯誤移除
   - 檢查 `res/raw` 中的 GIF 文件

4. **驗證依賴項**
   - 確保所有依賴項版本固定
   - 檢查是否有衝突的依賴

## 預防措施

### 版本控制
- ✅ 重要配置文件已加入版本控制
- ✅ 建置腳本統一管理
- ✅ 設置 `.gitignore` 排除臨時文件

### 自動化檢查
- ✅ Pre-commit 檢查腳本
- ✅ 建置一致性驗證
- ✅ APK 對比工具

### 團隊協作
- 📋 標準化建置流程文檔
- 🔧 統一開發環境配置
- 📊 建置報告和對比記錄

## 下一步建議

1. **立即執行**
   ```powershell
   .\scripts\build_release.ps1 -Clean
   .\scripts\compare_builds.ps1
   ```

2. **安裝測試**
   - 在模擬器安裝 Debug 版本
   - 在真實設備安裝 Release 版本
   - 逐一對比每個功能

3. **建立習慣**
   - 每次建置前運行檢查腳本
   - 定期驗證建置一致性
   - 記錄任何發現的問題

## 聯繫支援

如果問題仍然存在：
1. 運行所有檢查腳本並保存輸出
2. 記錄具體的差異表現
3. 提供建置環境信息
4. 分享 APK 對比結果

---
**重要**: 此指南解決了您提到的"連續兩次打包版本與模擬器版本完全不同"的問題。請按照上述步驟執行，應該能徹底解決一致性問題。
