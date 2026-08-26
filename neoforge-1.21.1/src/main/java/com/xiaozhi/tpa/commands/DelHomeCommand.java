package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DelHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("delhome")
                .then(argument("name", StringArgumentType.string())
                        .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int execute(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        ServerLevel level = (ServerLevel) self.getLevel();
        var dim = level.dimension().location();
        PlayerData data = PlayerData.get(level);
        if (data.getHome(self.getUUID(), dim, name) == null) {
            source.sendFailure(Component.translatable("command.delhome.not_found", name));
            return 0;
        }
        data.removeHome(self.getUUID(), dim, name);
        source.sendSuccess(() -> Component.translatable("command.delhome.success", name), true);
        return 1;
    }
}
