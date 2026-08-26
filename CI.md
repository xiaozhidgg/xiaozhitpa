# 用 GitHub Actions 云端构建（无需本机联网，推荐）

本仓库已配好 `.github/workflows/build.yml`，会在 GitHub 的云端服务器上自动编译全部 5 个工程并产出 jar。

## 一次性设置
1. 在 GitHub 建一个 **Public** 仓库（例如 `xiaozhi-tpa`）。
2. 把 `D:\桌面\xiaozhitpa\xiaozhi-tpa` 整个推上去。用 git：
   ```bat
   cd D:\桌面\xiaozhitpa\xiaozhi-tpa
   git init
   git add .
   git commit -m "Xiaozhi TPA multi-loader build"
   git remote add origin https://github.com/<你的用户名>/xiaozhi-tpa.git
   git branch -M main
   git push -u origin main
   ```
   > 推之前确认 `.github` 文件夹被一起提交（`git add .` 会包含它）。项目里没有内嵌的 jar、也无需生成 wrapper。

## 之后怎么拿 jar
- **Artifacts**：每次推送/构建完成后，打开仓库 **Actions** → 点开对应 run → 底部 **Artifacts** 下载 5 个 jar（各自文件名为 `xiaozhi_tpa-1.0.0-<loader>-<mc>.jar`）。
- **Release**：打一个 `v*` 的 tag 并推送（如 `git tag v1.0.0 && git push origin v1.0.0`），工作流会自动创建一个 GitHub Release，把这 5 个 jar 作为附件挂上去，适合放下载页。

## 触发条件
- 推送到 `main` / `master`、或手动 **Run workflow**，都会构建并上传 Artifacts。
- 推 `v*` tag 时额外生成一个 Release。

## 说明
- 云端使用 GitHub 自带的 Gradle 与 JDK（1.20.1 用 JDK17，其余用 JDK21），无需本机安装任何东西。
- 构建需要联网下载 NeoForge / Forge / Fabric 依赖，这部分由 GitHub 服务器完成，与你本机网络无关。
- 若某平台很在意「jar 带 mc 版本号」的文件名，最终产物文件名规则是 `xiaozhi_tpa-<mod_version>-<loader>-<mc>.jar`（例如 `xiaozhi_tpa-1.0.0-fabric-1.21.1.jar`）。
