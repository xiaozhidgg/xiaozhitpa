# GitHub Releases 模板

## 标题
`v1.0.0` —— Xiaozhi TPA 多版本合集

## 正文
### Xiaozhi TPA v1.0.0
服务端专用 TPA / 回家 / 返回模组，支持 Forge、NeoForge、Fabric 与 1.20.1 / 1.21.1。

**功能**
- `/tpa` `/tpaccept` `/tpdeny` `/tpauto`：传送请求与自动接受
- `/sethome` `/home` `/delhome` `/listhome`：多座命名家（按维度）
- `/back`：返回死亡或传送前位置
- TPA 60 秒超时、数据存档持久化、中文+英文

**构建产物**
| 文件 | 加载器 | MC |
|---|---|---|
| xiaozhi_tpa-1.0.0-forge-1.20.1.jar | Forge | 1.20.1 |
| xiaozhi_tpa-1.0.0-forge-1.21.1.jar | Forge | 1.21.1 |
| xiaozhi_tpa-1.0.0-neoforge-1.21.1.jar | NeoForge | 1.21.1 |
| xiaozhi_tpa-1.0.0-fabric-1.20.1.jar | Fabric | 1.20.1 |
| xiaozhi_tpa-1.0.0-fabric-1.21.1.jar | Fabric | 1.21.1 |

**安装**：把对应 jar 放入服务器 `mods/`（客户端无需安装）。
**说明**：本仓库生成环境无外网，jar 需在本机 `gradlew build` 生成；源码见各目录。

## 附件
上传 `releases/` 下的 5 个 jar 作为附件。
