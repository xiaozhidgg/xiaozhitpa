# Xiaozhi TPA — 多版本发布合集

一个**服务端专用**的 TPA / Home / Back 模组（`modid = xiaozhi_tpa`，显示名 **Xiaozhi TPA**），
提供 `/tpa` `/tpaccept` `/tpdeny` `/tpauto` `/sethome` `/home` `/delhome` `/listhome` `/back` 等命令。
由 Forge 1.20.1 的 `xsytpa` 移植而来，支持新旧版本服务器。

> **服务端专用**：客户端无需安装，直接在 NeoForge/Forge/Fabric 的**服务端** `mods/` 目录放入对应 jar 即可。

> 🛠️ **开发者**：工程结构、三套加载器移植要点、如何新增命令/版本，见 [`DEVELOPER.md`](DEVELOPER.md)。

> ☁️ **本机断网也能出 jar**：仓库已配好 GitHub Actions（`.github/workflows/build.yml`），推送到 GitHub 即云端构建 5 个 jar，见 [`CI.md`](CI.md)。

## 各版本/加载器产物

| 加载器 | MC 版本 | 目录 | 版本号 | 状态 |
|---|---|---|---|---|
| Forge | 1.20.1 | `forge-1.20.1/` | `1.0.0-forge` | — |
| Forge | 1.21.1 | `forge-1.21.1/` | `1.0.0-forge` | — |
| NeoForge | 1.21.1 | `neoforge-1.21.1/` | `1.0.0-neoforge` | ✅ 已完成源码 |
| Fabric | 1.20.1 | `fabric-1.20.1/` | `1.0.0-fabric` | — |
| Fabric | 1.21.1 | `fabric-1.21.1/` | `1.0.0-fabric` | — |

> ⚠️ NeoForge 从 **1.20.2** 才诞生，因此 **1.20.1 没有 NeoForge**，只能使用 Forge 或 Fabric。

## 命令（所有版本一致，所有玩家可用）

| 命令 | 说明 |
|---|---|
| `/tpa <玩家>` | 向玩家发送传送请求；目标开启了自动接受时立即传送。 |
| `/tpaccept [玩家|all]` | 接受请求；不带参数接受待处理请求。 |
| `/tpdeny [玩家]` | 拒绝请求。 |
| `/tpauto` | 开关「自动接受」请求。 |
| `/sethome <名字>` | 把当前位置存为命名家点（按当前维度）。 |
| `/home [名字]` | 传送到家点（不带参数用默认 `home`），带自动补全。 |
| `/delhome <名字>` | 删除家点。 |
| `/listhome` | 列出当前维度的家点。 |
| `/back` | 返回上一位置（死亡时与前一次 TPA/回家前记录）。 |

行为：TPA 请求 **60 秒超时**；每玩家**多座命名家、按维度区分**；`/back` 位置在**死亡**与**被 TPA/回家传送前**记录；数据**存档持久化**（Forge/NeoForge 存世界存档，Fabric 存世界目录 `xiaozhi_tpa_player_data.json`）。

> 💬 **提示语言**：本模组为**服务端专用**，客户端无需安装，因此命令提示是**内置中文**直接发给玩家（如「已返回家【名称】」「已tpa至 【玩家】处」），不会随客户端语言切换。OP 不会看到其他玩家的指令反馈（服务器后台日志仍正常记录）。

## 构建说明（重要）

本仓库生成环境**无外网**，因此所有 `jar` 都需**在你自己的、可联网的机器**上用各自目录里的 Gradle 构建。

- Forge / NeoForge：`gradlew.bat build`（或 `gradle build`），产物在对应目录 `build/libs/`。
- Fabric：`gradlew.bat build`，产物在对应目录 `build/libs/`。
- 各目录的 `README.md` 有当前版本的详细安装/命令说明。

## 发布元数据

所有平台的发布模板（CurseForge / Modrinth / 苦力怕论坛 / MC百科·9minecraft / GitHub Releases）
在 [`release-metadata/`](release-metadata) 内，上传时直接抄用。

## 目录结构

```
xiaozhi-tpa/
  forge-1.20.1/        # Forge 1.20.1 工程
  forge-1.21.1/        # Forge 1.21.1 工程
  neoforge-1.21.1/     # NeoForge 1.21.1 工程（已完成）
  fabric-1.20.1/       # Fabric 1.20.1 工程
  fabric-1.21.1/       # Fabric 1.21.1 工程
  releases/            # 按 加载器-版本 分文件夹的最终 jar
  release-metadata/    # 各平台发布模板
```
