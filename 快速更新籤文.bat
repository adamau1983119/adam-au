@echo off
chcp 65001 >nul
echo 籤文快速更新工具
echo ====================

echo.
echo 正在備份原始籤文文件...
copy "app\src\main\assets\fortunes.json" "app\src\main\assets\fortunes.json.backup" >nul
if %errorlevel% equ 0 (
    echo ✅ 備份成功
) else (
    echo ❌ 備份失敗
    pause
    exit /b 1
)

echo.
echo 正在更新籤文內容...
echo 已完成：第1籤、第2籤、第3籤、第82籤
echo 待更新：第4-81籤、第83-100籤

echo.
echo 請手動更新以下籤文：
echo 1. 第4籤：關公過五關
echo 2. 第5籤：趙雲救阿斗
echo 3. 第6籤：諸葛亮借東風
echo 4. 第7籤：岳飛精忠報國
echo 5. 第8籤：包青天斷案
echo 6. 第9籤：楊家將保家衛國
echo 7. 第10籤：花木蘭代父從軍
echo ...
echo 第82籤：孔子擊磬（已完成）

echo.
echo 更新完成後，請：
echo 1. 重新建置APP
echo 2. 在模擬器中測試籤文顯示
echo 3. 檢查籤文內容的準確性

echo.
pause
