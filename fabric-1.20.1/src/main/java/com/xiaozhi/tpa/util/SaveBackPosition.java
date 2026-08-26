package com.xiaozhi.tpa.util;

import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/** Stores the player's current position for later use by /back. */
public final class SaveBackPosition {
    private SaveBackPosition() {}

    public static void save(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        HomePos pos = new HomePos(player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch(), world.getRegistryKey().getValue().toString());
        PlayerData.get().getEntry(player.getUuid()).back = pos;
        PlayerData.get().saveLater();
    }
}
