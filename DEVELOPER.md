# Xiaozhi TPA — 开发者文档

面向后续维护/扩展者的说明。项目是一套「**多加载器 × 多 MC 版本**」的管理方式：同一个 `modid = xiaozhi_tpa`、同一套命令与行为，但**每个组合是独立工程**，而不是一套源码自动生成。

---

## 1. 目录结构

```
xiaozhi-tpa/
  forge-1.20.1/        # Forge 1.20.1 工程（旧 SavedData API，JDK17）
  forge-1.21.1/        # Forge 1.21.1 工程（SavedData.Factory，JDK21）
  neoforge-1.21.1/     # NeoForge 1.21.1 工程（@EventBusSubscriber，JDK21）
  fabric-1.20.1/       # Fabric 1.20.1 工程（Yarn 映射，fabric.mod.json，JDK17）
  fabric-1.21.1/       # Fabric 1.21.1 工程（Loom 1.8，JDK21）
  releases/            # 按 加载器-版本 分文件夹的最终构建产物
  release-metadata/    # 各发布平台上传模板
  README.md            # 总览（面向玩家/发布）
  DEVELOPER.md         # 本文档
```

每个工程内部结构一致：

```
<工程>/
  build.gradle          # 平台/版本相关构建脚本
  gradle.properties     # 版本号、modid、加载器/映射版本等
  settings.gradle
  gradlew.bat           # Gradle Wrapper 脚本（wrapper jar 需自行 bootstrap）
  gradle/wrapper/gradle-wrapper.properties
  src/main/java/com/xiaozhi/tpa/
      XiaozhiTpaMod.java            # 入口
      EventHandler.java             # 事件订阅（Forge/NeoForge；Fabric 用回调，无此类）
      commands/                     # 9 个命令 + CommandRegister
      data/PlayerData.java          # 持久化数据
      data/TpaManager.java          # 传送请求（内存态）
      data/TpaRequest.java          # 请求记录
      util/HomePos.java             # 位置 + 维度
      util/SaveBackPosition.java    # /back 位置记录
      util/Reference.java           # MODID / 常量
  src/main/resources/
      META-INF/{mods.toml | neoforge.mods.toml | fabric.mod.json}
      assets/xiaozhi_tpa/lang/{en_us.json, zh_cn.json}
      pack.mcmeta
```

---

## 2. 设计约定（三套加载器都要遵守）

- **逻辑与平台解耦**：命令逻辑、TPA 请求管理、家点数据模型在各平台基本一致，只有「平台接入」部分不同（事件订阅、持久化 API、传送 API、构建脚本）。
- **命令行为一致**（所有版本相同）：
  - `/tpa`、`/tpaccept [玩家|all]`、`/tpdeny [玩家]`、`/tpauto`
  - `/sethome <名字>`、`/home [名字]`、`/delhome <名字>`、`/listhome`
  - `/back`
- **TPA 请求 60 秒超时**（`Reference.TPA_TIMEOUT`），在 `TpaManager` 里做惰性过期清理（无全局 tick，避免跨版本 TickEvent API 差异）。
- **每玩家多座命名家，按维度区分**：homes 的数据结构是 `维度 -> (名字 -> HomePos)`。
- **`/back` 位置记录时机**：死亡时、以及被 `/tpa` 传送前、`/home` 传送前。
- **持久化两套**：Forge/NeoForge 存世界存档（`SavedData`）；Fabric 存 `世界目录/xiaozhi_tpa_player_data.json`（Gson）。行为等价，但文件位置不同。
- **服务端专用**：客户端无任何注册项，`displayTest = "IGNORE_SERVER_VERSION"`（Forge/NeoForge）、Fabric `environment: "server"`。

---

## 3. 三套加载器的关键差异（移植对照表）

| 关注点 | Forge 1.20.1 | Forge/NeoForge 1.21.x | Fabric |
|---|---|---|---|
| 入口注解 | `@Mod` | `@Mod` / `@Mod` | `ModInitializer#onInitialize` |
| 事件总线 | `MinecraftForge.EVENT_BUS` | 同左 / `NeoForge.EVENT_BUS` | 无；用回调 API |
| 事件订阅 | `@Mod.EventBusSubscriber(bus=Bus.FORGE)` | 同左 / `@EventBusSubscriber(bus=Bus.GAME)` | `CommandRegistrationCallback`、`ServerLifecycleEvents` |
| 命令注册事件 | `net.minecraftforge.event.RegisterCommandsEvent` | 同左（Forge）/ `net.neoforged.neoforge.event.RegisterCommandsEvent` | `CommandRegistrationCallback.EVENT` |
| 死亡事件 | `net.minecraftforge.event.entity.living.LivingDeathEvent` | 同左（Forge）/ `net.neoforged.neoforge.event.entity.living.LivingDeathEvent` | 无需（也可注册） |
| 持久化 API | `DimensionDataStorage.computeIfAbsent(loader, supplier, name)` | `SavedData.Factory` + `computeIfAbsent(factory, name)` | Gson + 世界目录 JSON |
| 取玩家世界 | `(ServerLevel) player.getLevel()` | `(ServerLevel) player.getLevel()` | `player.getServerWorld()` |
| 跨维传送 | `teleportTo(ServerLevel,x,y,z,yaw,pitch)` | `teleportTo(...)` | `teleport(ServerWorld,x,y,z,yaw,pitch)` |
| 维度标识 | `level.dimension().location()`（ResourceLocation） | 同左 | `world.getRegistryKey().getValue()`（Identifier） |
| 取维度世界 | `server.getLevel(ResourceKey.create(Registries.DIMENSION, loc))` | 同左 | `server.getWorld(RegistryKey.of(Registry.WORLD_KEY, id))` |
| 消息组件 | `Component.translatable` | `Component.translatable` | `Text.translatable` |
| 命令源 | `ServerCommandSource` | `ServerCommandSource` | `ServerCommandSource`（Yarn） |
| 源取玩家 | `source.getPlayerOrException()` | `source.getPlayerOrException()` | `source.getPlayer()`（可为 null） |
| 发送反馈 | `source.sendSuccess(…)` / `sendFailure(…)` | 同左 | `source.sendFeedback(…)` / `sendError(…)` |
| 构建插件 | `net.minecraftforge.gradle` `[6.0,6.2)` | 同左（Forge）/ `net.neoforged.moddev` | `fabric-loom` |
| Java | 17 | 21 | 20.1→17，21.1→21 |

> ⚠️ **映射差异**：Forge/NeoForge 用 Mojang 官方映射（`getLevel()`、`teleportTo(...)`、`SavedData.Factory`）；Fabric 默认用 **Yarn** 映射（`getServerWorld()`、`teleport(...)`、`Text`、`Identifier`）。同一逻辑在两边方法名/类名完全不同——**这是为什么每个组合都要单独移植**。

---

## 4. 如何新增一个命令（以 Forge/NeoForge 为例）

1. 在 `commands/` 新建 `FooCommand.java`，实现静态 `register(CommandDispatcher<CommandSourceStack>)`：

```java
public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(literal("foo")
        .then(argument("target", EntityArgument.player())
            .executes(ctx -> execute(ctx.getSource(), EntityArgument.getPlayer(ctx, "target")))));
}

private static int execute(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
    ServerPlayer self = source.getPlayerOrException();
    // ...逻辑
    source.sendSuccess(() -> Component.translatable("command.foo.done"), true);
    return 1;
}
```

2. 在 `CommandRegister.registerAll(...)` 里加一行 `FooCommand.register(dispatcher);`。

3. 在 `assets/xiaozhi_tpa/lang/en_us.json` 和 `zh_cn.json` 里加翻译键（`command.foo.*`）。

4. Fabric 侧对应地写一个 `FooCommand`（用 Yarn 名字），并在 `CommandRegister`（Fabric）里注册——**两边独立维护**。

---

## 5. 如何新增一个 MC 版本 / 加载器

原则：**复制最近的一个同类工程，只改「平台接入」部分。**

- 新增 **Forge 小版本**（如 1.21.1 → 1.21.4）：复制 `forge-1.21.1/`，改 `gradle.properties` 的 `minecraft_version`、`forge_version`，重点核对 `SavedData`/事件 API 是否变了（1.21.x 内部差异小）。
- 新增 **NeoForge 版本**：复制 `neoforge-1.21.1/`，改 `neo_version`，改动点同上。
- 新增 **Fabric 版本**：复制 `fabric-1.21.1/`，改 `minecraft_version`、`yarn_mappings`、`loader_version`、`fabric_version`，并在 `fabric.mod.json` 更新 `minecraft`/`java`/`fabricloader` 约束。
- 新增 **加载器**（如 Quilt）：参考 Fabric 工程重写加载器接入；Quilt 用 `quilt_loader.json` 与 Quilt API，**不能**直接复制 Fabric。

> 每个版本都要**分别构建验证**，因为即使版本号差一个 patch，也可能有 API 更名。

---

## 6. 数据模型与持久化

### HomePos（位置 + 维度）
字段：`x, y, z, yaw, pitch, dimension`。Forge/NeoForge 里 dimension 是 `ResourceLocation`；Fabric 里是 String（identifier 字符串），便于 Gson 序列化。

### PlayerData
- Forge/NeoForge：`extends SavedData`，存到**主世界** `ServerLevel.getDataStorage()`，键 `xiaozhi_tpa_player_data`。结构：
  - `homes: UUID -> (维度 -> (名字 -> HomePos))`
  - `backs: UUID -> HomePos`
  - `autoAccept: UUID -> boolean`
  - 改动后调用 `setDirty()` 触发自动保存。
- Fabric：`PlayerData` 是单例 + Gson 序列化到 `世界目录/xiaozhi_tpa_player_data.json`。`SERVER_STARTED` 载入，`SERVER_STOPPED` 与每次改动写回。

### TpaManager
纯内存态（**不持久化**），记录「目标 -> 请求(来源, 目标, 时间戳)」。请求过期（60s）在读取时惰性清除。一个人同时最多一个发出、一个收到的请求。

---

## 7. 构建与运行

每个工程（在**可联网**的机器上）：

```bat
cd <目录>
gradle wrapper          rem 首次，生成 wrapper（或本机已有 Gradle 可跳过）
gradlew.bat build       rem 产物在 build/libs/
```

> 本仓库生成环境**没有外网**，无法在此编译；所有 `jar` 需你本机构建。Fabric 版需先装 **Fabric API**（属于环境依赖，非本 mod 依赖）。

> 若你**本机无法联网**或不想折腾 JDK/Gradle，推荐用仓库自带的 **GitHub Actions**（见根目录 [`CI.md`](CI.md)）：推送到 GitHub 即云端编译全部 5 个工程并产出 jar，作为 Release/Artifact 下载。

### Java 版本要求
| 工程 | JDK |
|---|---|
| forge-1.20.1 | 17 |
| forge-1.21.1 / neoforge-1.21.1 / fabric-1.21.1 | 21 |
| fabric-1.20.1 | 17 |

---

## 8. 版本与命名规范

- `modid`：一律 `xiaozhi_tpa`。
- 显示名：`Xiaozhi TPA`。
- 版本号**按加载器加后缀**：
  - Forge：`1.0.0-forge`
  - NeoForge：`1.0.0-neoforge`
  - Fabric：`1.0.0-fabric`
- 文件名规范：`xiaozhi_tpa-<版本>-<加载器>-<mc版本>.jar`，如 `xiaozhi_tpa-1.0.0-fabric-1.21.1.jar`。

---

## 9. 常见坑

- 把 Mojang 名字（Forge/NeoForge）当 Yarn 名字（Fabric）用，会直接编译失败——**按第 3 节对照表来**。
- `PlayerData.get(...)` 在 Forge 1.20.1 与 1.21.x 的 `computeIfAbsent` **签名不同**：1.20.1 是 `(loader, constructor, name)`，1.21 是 `(Factory, name)`。改版本时最容易踩。
- 跨维传送：必须传**目标维度**的 World/ServerLevel，而不是当前世界。`/home`、`/back`、自动接受 `/tpa` 都要按维度取目标世界。
- Forge 需要在 jar task 里 `finalizedBy 'reobfJar'`，否则服务器端会因映射问题崩溃。
- `gradle-wrapper.jar` 是二进制，仓库里无法保留；请用 `gradle wrapper` 或本机 Gradle。

---

## 10. 许可

采用 **MIT License**（见根目录 [`LICENSE`](LICENSE)），各工程 `mods.toml`/`fabric.mod.json` 的 `license` 字段均已设置为 `MIT`。如需更换为其他协议（如 LGPL-3.0），请同步修改 `LICENSE` 文件与 5 个工程的 `license` 字段。

---

## 11. 发布 / 上传与协作（CI 工作流）

> 目标：仓库推送到 GitHub 后，**GitHub Actions 云端自动编译出 5 个 jar**，你没网也能拿到产物。

### 分工
- **我（在本地沙箱）**：只负责**改代码 + 提交本地 git 仓库**（`git add -A && git commit`）。⚠️ 注意：本沙箱 shell **无外网**（连 GitHub 都 `000`），所以我**不能执行 `git push`**。
- **你（跑一条命令）**：双击运行根目录 [`push.bat`](push.bat)，它会读取 `remote.txt` 里的仓库地址并 `git push`。这一步需要在**你联网 + 已认证**的机器上。

### 仓库地址 / 认证（关键信息，发布前需你来填）
- **仓库地址**：写入根目录 `remote.txt`（已 gitignore，不入库）。本项目为 `https://github.com/xiaozhidgg/xiaozhitpa.git`。
- **认证方式（二选一）**：
  - HTTPS + Personal Access Token（推荐）：GitHub 会弹窗让你登录/输 Token；或先在 Git 里配置 credential manager。
  - SSH：配好 SSH 密钥，`remote` 用 `git@github.com:<你的用户名>/xiaozhi-tpa.git`。
- **GitHub 侧**：先手动建一个 **Public** 仓库（例如 `xiaozhi-tpa`），不需要推任何文件，空仓库即可。

### 每次更新流程
1. 我改完代码 → 本地 `git add -A && git commit`（在沙箱内完成，无需网络）。
2. 你运行 `push.bat` → 推到 GitHub。
3. GitHub Actions 自动构建 5 个工程，跑完在 **Actions** 页 Artifacts 下载 5 个 jar；若推 `v*` tag，会额外出一个 **Release** 挂上这些 jar。
4. 拿到 jar 后按 `release-metadata/` 模板去各平台发布。

### 我需要你提供的
- GitHub 仓库地址（填进 `remote.txt`）。
- 确认你在要运行 `push.bat` 的机器上**能联网到 GitHub** 且**已认证**（令牌/SSH）。
- （可选）希望 `push.bat` 推 `v*` tag 自动打 Release：我按 `CI.md` 的说明执行 `git tag v1.0.0 && git push origin v1.0.0`。

### 触发与产物
- 推 `main`/`master` 或手动 Run workflow → 构建并上传 Artifacts。
- 推 `v*` tag → 额外创建 GitHub Release，5 个 jar 作为附件。
- 文件名规则：`xiaozhi_tpa-<mod_version>-<loader>-<mc>.jar`（如 `xiaozhi_tpa-1.0.0-neoforge-1.21.1.jar`）。

