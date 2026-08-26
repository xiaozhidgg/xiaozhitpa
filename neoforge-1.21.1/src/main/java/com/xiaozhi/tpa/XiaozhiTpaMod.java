package com.xiaozhi.tpa;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(XiaozhiTpaMod.MODID)
public class XiaozhiTpaMod {
    public static final String MODID = "xiaozhi_tpa";

    public XiaozhiTpaMod(IEventBus modEventBus, ModContainer modContainer) {
        // Commands and game events are wired up through @EventBusSubscriber below.
        // Nothing further is needed here — this is a server-side command mod.
    }
}
