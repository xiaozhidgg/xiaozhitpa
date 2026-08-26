@echo off
rem ============================================================
rem  Xiaozhi TPA - 一键提交并推送到 GitHub
rem  用法：本机已联网 + 已在 git 里配置好账号(HTTPS 令牌 或 SSH)
rem  前提：GitHub 上已创建仓库(Public)，并把地址填到 remote.txt
rem ============================================================
setlocal enabledelayedexpansion
cd /d "%~dp0"

set "REPO="
if exist remote.txt set /p REPO=<remote.txt
if not defined REPO (
    echo [错误] 未找到 remote.txt 或地址为空。请创建 remote.txt 并填入仓库地址。
    pause
    exit /b 1
)

rem 首次设置 remote（已存在则跳过）
git remote get-url origin >nul 2>nul
if errorlevel 1 (
    git remote add origin "%REPO%"
    echo [信息] 已添加 remote: %REPO%
)

rem 分支统一为 main
git branch -M main

rem 暂存并提交本地全部改动
git add -A
git commit -m "update: Xiaozhi TPA build & docs" >nul 2>nul || echo [信息] 没有新的改动需要提交。

echo [信息] 正在推送到 GitHub（需本机联网 + 已认证）...
git push -u origin main
if errorlevel 1 (
    echo [错误] 推送失败。请检查：已登录 GitHub(令牌/SSH)、仓库地址、网络。
    pause
    exit /b 1
)

echo.
echo [完成] 推送成功。去 GitHub 仓库 - Actions 页查看构建，Artifacts 或 Releases 里下载 5 个 jar。
pause
