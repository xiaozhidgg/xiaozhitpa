package com.xiaozhi.tpa;

import com.xiaozhi.tpa.commands.CommandRegister;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Registers the mod's commands and the game-bus event handlers. */
@Mod.EventBusSubscriber(modid = XiaozhiTpaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
