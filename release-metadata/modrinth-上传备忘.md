# Modrinth 上传备忘

## 项目信息
- **Title**：Xiaozhi TPA
- **Slug / Project ID**：xiaozhi-tpa（自定义或由平台生成）
- **Description**：`一个服务端专用的 TPA / 回家 / 返回模组，支持 /home /tpa /back，按维度多座命名家，可选自动接受。`
- **Categories**：`utility`、`server-side`、`teleportation`
- **Project type**：Mod
- **Side / Client & Server**：**Server**（仅服务端）
- **License**：MIT

## 版本（每个文件单独发布）
| 文件 | 版本号 | 加载器 | 游戏版本 |
|---|---|---|---|
| xiaozhi_tpa-1.0.0-forge-1.20.1.jar | 1.0.0-forge | Forge | 1.20.1 |
| xiaozhi_tpa-1.0.0-forge-1.21.1.jar | 1.0.0-forge | Forge | 1.21.1 |
| xiaozhi_tpa-1.0.0-neoforge-1.21.1.jar | 1.0.0-neoforge | NeoForge | 1.21.1 |
| xiaozhi_tpa-1.0.0-fabric-1.20.1.jar | 1.0.0-fabric | Fabric | 1.20.1 |
| xiaozhi_tpa-1.0.0-fabric-1.21.1.jar | 1.0.0-fabric | Fabric | 1.21.1 |

## 可选 modrinth.mcmod 片段（pyproject 风格，便于自动化）
```toml
[modrinth]
title = "Xiaozhi TPA"
version = "1.0.0-neoforge"
game_versions = ["1.21.1"]
loaders = ["neoforge"]
client_side = "unsupported"
server_side = "required"
```
> 不同目标文件分别改动 `version` / `game_versions` / `loaders`。
