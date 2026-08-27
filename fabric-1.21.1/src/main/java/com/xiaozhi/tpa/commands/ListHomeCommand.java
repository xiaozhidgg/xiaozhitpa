package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;

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
        String dim = self.getServerWorld().getRegistryKey().getValue().toString();
        Collection<String> names = PlayerData.get().getEntry(self.getUuid()).homes
                .getOrDefault(dim, Collections.emptyMap()).keySet();
        if (names.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§c你没有在这个维度设置家。"), false);
            return 0;
        }
        self.sendMessage(Text.literal("§a你在当前维度的家："), false);
        for (String name : names) {
            self.sendMessage(Text.literal("  - " + name), false);
        }
        return 1;
    }
}
