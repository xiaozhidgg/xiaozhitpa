package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.literal;

public final class BackCommand {

    private BackCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("back").executes(ctx -> execute(ctx.getSource())));
    }

    private static int execute(ServerCommandSource source) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        HomePos back = PlayerData.get().getEntry(self.getUuid()).back;
        if (back == null) {
            source.sendError(Text.literal("§c没有可返回的位置！"));
            return 0;
        }

        RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, back.identifier());
        ServerWorld targetWorld = self.getServer().getWorld(dimKey);
        if (targetWorld == null) {
            source.sendError(Text.literal("§c家所在的维度不可用！"));
            return 0;
        }

        self.teleport(targetWorld, back.x, back.y, back.z, back.yaw, back.pitch);
        source.sendFeedback(() -> Text.literal("§a已返回上一个位置！"), false);
        return 1;
    }
}
