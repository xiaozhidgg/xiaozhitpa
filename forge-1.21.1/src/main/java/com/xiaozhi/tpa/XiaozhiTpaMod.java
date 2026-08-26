package com.xiaozhi.tpa;

import net.minecraftforge.fml.common.Mod;

@Mod(XiaozhiTpaMod.MODID)
public class XiaozhiTpaMod {
    public static final String MODID = "xiaozhi_tpa";

    public XiaozhiTpaMod() {
        // Commands and game events are wired up through @Mod.EventBusSubscriber below.
        // Nothing further is needed here — this is a server-side command mod.
    }
}
