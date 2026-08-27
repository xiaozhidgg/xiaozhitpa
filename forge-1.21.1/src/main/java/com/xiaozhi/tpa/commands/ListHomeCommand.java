package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
        Map<ResourceLocation, Map<String, HomePos>> all = PlayerData.get(level).getAllHomes(self.getUUID());
        if (all.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§c你还没有设置任何家。"), false);
            return 0;
        }
        self.sendSystemMessage(Component.literal("§a你的家（跨维度）："));
        for (Map.Entry<ResourceLocation, Map<String, HomePos>> dim : all.entrySet()) {
            for (String name : dim.getValue().keySet()) {
                self.sendSystemMessage(Component.literal("§f  - " + name + " ( " + dim.getKey() + " )"));
            }
        }
        return 1;
    }
}
