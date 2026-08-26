# 苦力怕论坛（MCBBS）发布帖模板

> 用 `【】` 标出的地方替换成你的内容。把对应下载链接/附件替换为实际文件。

---

【标题】：[原创] Xiaozhi TPA —— 服务端 TPA / 回家 / 返回 模组（Forge / NeoForge / Fabric，1.20.1 & 1.21.1）

【作者】：xiaozhi
【版本】：1.0.0-forge / 1.0.0-neoforge / 1.0.0-fabric
【适用】：MC 1.20.1、1.21.1；Forge、NeoForge、Fabric；服务端

## 简介
一个服务端专用的 `TPA / home / back` 命令模组，支持按维度多座命名家、TPA 60 秒超时、自动接受开关，数据存档持久化，中文+英文界面。

## 命令
| 命令 | 说明 |
|---|---|
| `/tpa <玩家>` | 发送传送请求；对方开启自动接受则立即传送 |
| `/tpaccept [玩家\|all]` | 接受请求 |
| `/tpdeny [玩家]` | 拒绝请求 |
| `/tpauto` | 开关自动接受 |
| `/sethome <名字>` | 存家（按维度） |
| `/home [名字]` | 回家（默认 `home`，带补全） |
| `/delhome <名字>` | 删家 |
| `/listhome` | 列当前维度家 |
| `/back` | 回上一位置（死亡 / 传送前记录） |

## 安装
把对应加载器/版本的 jar 放入服务器 `mods/` 目录（客户端无需安装）。

## 下载
- Forge 1.20.1：`xiaozhi_tpa-1.0.0-forge-1.20.1.jar`
- Forge 1.21.1：`xiaozhi_tpa-1.0.0-forge-1.21.1.jar`
- NeoForge 1.21.1：`xiaozhi_tpa-1.0.0-neoforge-1.21.1.jar`
- Fabric 1.20.1：`xiaozhi_tpa-1.0.0-fabric-1.20.1.jar`
- Fabric 1.21.1：`xiaozhi_tpa-1.0.0-fabric-1.21.1.jar`

> 注意：Forge 1.20.1 需 JDK17，其余需 JDK21。
