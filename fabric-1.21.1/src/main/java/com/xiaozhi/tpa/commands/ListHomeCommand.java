package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public final class ListHomeCommand {

    private ListHomeCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("listhome").executes(ctx -> execute(ctx.getSource())));
    }

    private static int execute(ServerCommandSource source) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        Map<String, Map<String, HomePos>> all = PlayerData.get().getAllHomes(self.getUuid());
        if (all.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§c你还没有设置任何家。"), false);
            return 0;
        }
        self.sendMessage(Text.literal("§a你的家（跨维度）："), false);
        for (Map.Entry<String, Map<String, HomePos>> dim : all.entrySet()) {
            for (String name : dim.getValue().keySet()) {
                self.sendMessage(Text.literal("§f  - " + name + " ( " + dim.getKey() + " )"), false);
            }
        }
        return 1;
    }
}
