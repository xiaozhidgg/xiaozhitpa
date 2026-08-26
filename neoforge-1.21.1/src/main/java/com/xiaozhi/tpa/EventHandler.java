package com.xiaozhi.tpa;

import com.xiaozhi.tpa.commands.CommandRegister;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** Registers the mod's commands and the game-bus event handlers. */
@EventBusSubscriber(modid = XiaozhiTpaMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class EventHandler {
    private EventHandler() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandRegister.registerAll(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Remember where the player died so /back returns them there.
            SaveBackPosition.save(player);
        }
    }
}
