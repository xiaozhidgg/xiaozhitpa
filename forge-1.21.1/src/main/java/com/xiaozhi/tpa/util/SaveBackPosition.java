package com.xiaozhi.tpa.util;

import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** Stores the player's current position for later use by /back. */
public final class SaveBackPosition {
    private SaveBackPosition() {}

    public static void save(ServerPlayer player) {
        var level = player.serverLevel();
        HomePos pos = new HomePos(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot(), level.dimension().location());
        PlayerData.get(level).setBackPosition(player.getUUID(), pos);
    }
}
