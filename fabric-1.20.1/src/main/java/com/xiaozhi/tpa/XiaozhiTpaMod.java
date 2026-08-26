package com.xiaozhi.tpa;

import com.xiaozhi.tpa.commands.CommandRegister;
import com.xiaozhi.tpa.data.PlayerData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class XiaozhiTpaMod implements ModInitializer {
    public static final String MODID = "xiaozhi_tpa";

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(PlayerData::load);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PlayerData.get().saveLater());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegister.registerAll(dispatcher));
    }
}
