# releases/ — 最终上传用 jar

把各工程 `build/libs/*.jar` 复制到对应文件夹，文件命名统一为：

- `forge-1.20.1/`     `xiaozhi_tpa-1.0.0-forge-1.20.1.jar`
- `forge-1.21.1/`     `xiaozhi_tpa-1.0.0-forge-1.21.1.jar`
- `neoforge-1.21.1/`  `xiaozhi_tpa-1.0.0-neoforge-1.21.1.jar`
- `fabric-1.20.1/`    `xiaozhi_tpa-1.0.0-fabric-1.20.1.jar`
- `fabric-1.21.1/`    `xiaozhi_tpa-1.0.0-fabric-1.21.1.jar`

## 生成与收集（在可联网的机器上）
在每个工程目录执行 `gradlew.bat build`（Forge/NeoForge/Fabric 均可；Forge 1.20.1 用 JDK17，其余用 JDK21）。
产物在对应工程的 `build/libs/`，复制过来并改名为上面的规范文件名。

> 本仓库生成环境无外网，无法在此直接产出 jar；请在你自己的机器构建后把 jar 放进这里。
