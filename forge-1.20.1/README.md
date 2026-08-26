# Xiaozhi TPA — Forge 1.20.1

服务端专用 TPA 模组（`modid = xiaozhi_tpa`，版本 `1.0.0-forge`）。仅需装入 Forge 1.20.1 **服务端** 的 `mods/`，客户端无需本模组。

## 命令
| 命令 | 说明 |
|---|---|
| `/tpa <玩家>` | 发送传送请求；对方开了自动接受则立即传送 |
| `/tpaccept [玩家\|all]` | 接受请求 |
| `/tpdeny [玩家]` | 拒绝请求 |
| `/tpauto` | 开关自动接受 |
| `/sethome <名字>` | 存家（按当前维度） |
| `/home [名字]` | 回家（默认 `home`，带补全） |
| `/delhome <名字>` | 删家 |
| `/listhome` | 列当前维度家 |
| `/back` | 回上一位置（死亡 / 传送前记录） |

TPA 请求 60 秒超时；数据存世界存档（按维度分多座命名家）。语言：中文+英文。

## 构建
需 **JDK 17**，`gradle build`（或 `gradlew.bat build`），产物 `build/libs/xiaozhi_tpa-1.0.0-forge.jar`。

如需生成 Gradle Wrapper：`gradle wrapper`。
