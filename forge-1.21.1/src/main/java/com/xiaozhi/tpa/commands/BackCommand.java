package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import static net.minecraft.commands.Commands.literal;

public class BackCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("back").executes(ctx -> execute(ctx.getSource())));
    }

    private static int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        HomePos back = PlayerData.get(self.serverLevel()).getBackPosition(self.getUUID());
        if (back == null) {
            source.sendFailure(Component.literal("§c没有可返回的位置！"));
            return 0;
        }

        ServerLevel targetLevel = self.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, back.dimension));
        if (targetLevel == null) {
            source.sendFailure(Component.literal("§c家所在的维度不可用！"));
            return 0;
        }

        self.teleportTo(targetLevel, back.x, back.y, back.z, back.yaw, back.pitch);
        source.sendSuccess(() -> Component.literal("§a已返回上一个位置！"), false);
        return 1;
    }
}
