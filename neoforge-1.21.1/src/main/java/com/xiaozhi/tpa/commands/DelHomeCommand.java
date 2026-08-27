package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DelHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("delhome")
                .then(argument("name", StringArgumentType.string())
                        .executes(ctx -> execute(ctx.getSource(), ctx.getArgument("name", String.class)))));
    }

    private static int execute(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        ServerLevel level = self.serverLevel();
        PlayerData data = PlayerData.get(level);
        if (!data.removeHomeAnywhere(self.getUUID(), name)) {
            source.sendFailure(Component.literal("§c家点 " + name + " 不存在！"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("§a家点 " + name + " 已删除。"), false);
        return 1;
    }
}
