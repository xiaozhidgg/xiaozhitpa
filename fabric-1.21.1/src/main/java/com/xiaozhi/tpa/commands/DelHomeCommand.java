package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class DelHomeCommand {

    private DelHomeCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("delhome")
                .then(argument("name", StringArgumentType.string())
                        .executes(ctx -> execute(ctx.getSource(), ctx.getArgument("name", String.class)))));
    }

    private static int execute(ServerCommandSource source, String name) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        if (!PlayerData.get().removeHomeAnywhere(self.getUuid(), name)) {
            source.sendError(Text.literal("§c家点 " + name + " 不存在！"));
            return 0;
        }
        source.sendFeedback(() -> Text.literal("§a家点 " + name + " 已删除。"), false);
        return 1;
    }
}
