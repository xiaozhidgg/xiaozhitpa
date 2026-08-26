package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class ListHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("listhome").executes(ctx -> execute(ctx.getSource())));
    }

    private static int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        ServerLevel level = self.serverLevel();
        var dim = level.dimension().location();
        Map<String, HomePos> homes = PlayerData.get(level).getHomes(self.getUUID(), dim);
        if (homes.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.listhome.none"), true);
            return 0;
        }
        self.sendSystemMessage(Component.translatable("command.listhome.header"));
        for (String name : homes.keySet()) {
            self.sendSystemMessage(Component.literal("  - " + name));
        }
        return 1;
    }
}
