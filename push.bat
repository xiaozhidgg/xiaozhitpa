@echo off
rem ============================================================
rem  Xiaozhi TPA - one-click commit & push to GitHub
rem  Requires: a machine that can reach github.com and is logged in.
rem  Prereq: create the repo on GitHub, then fill remote.txt with its URL.
rem ============================================================
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo [step] Locating Git...
where git >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Git was not found on PATH. Install git or add it to PATH.
    pause
    exit /b 1
)

echo [step] Reading repo URL from remote.txt...
set "REPO="
if exist remote.txt set /p REPO=<remote.txt
if not defined REPO (
    echo [ERROR] remote.txt missing or empty. Put your repo URL in it, e.g. https://github.com/USER/REPO.git
    pause
    exit /b 1
)
echo        Repo: !REPO!

echo [step] Setting remote origin...
git remote get-url origin >nul 2>nul
if errorlevel 1 (
    git remote add origin "!REPO!"
    echo        Added origin.
)

echo [step] Normalizing branch to main...
git branch -M main

echo [step] Staging and committing local changes...
git add -A
git commit -m "update: Xiaozhi TPA build & docs" >nul 2>nul
if errorlevel 1 echo        (nothing new to commit)

echo [step] Pushing to GitHub...
git push -u origin main
if errorlevel 1 (
    echo [INFO] Push rejected - usually GitHub pre-created README/.gitignore/LICENSE on this fresh repo.
    echo       Force-pushing our content over those auto-generated files...
    git push --force -u origin main
    if errorlevel 1 (
        echo [ERROR] Push still failed. Check: login/token, repo URL, or network.
        pause
        exit /b 1
    )
)

echo.
echo [DONE] Push succeeded. Open the repo on GitHub - Actions tab to see the cloud build.
echo       Jars appear in Artifacts (and Releases if you push a v* tag).
pause
