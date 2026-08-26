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
    echo [信息] 首次推送被拒（远端可能已存在提交）。尝试 rebase 合并后再推...
    git pull --rebase origin main
    if errorlevel 1 (
        echo [错误] rebase 失败。请手动处理冲突，或确认远端仓库已清空。
        pause
        exit /b 1
    )
    git push -u origin main
    if errorlevel 1 (
        echo [错误] 仍推送失败。请检查：登录/令牌、仓库地址、网络、远端是否有无关提交。
        pause
        exit /b 1
    )
)

echo.
echo [完成] 推送成功。去 GitHub 仓库 - Actions 页查看构建，Artifacts 或 Releases 里下载 5 个 jar。
pause
