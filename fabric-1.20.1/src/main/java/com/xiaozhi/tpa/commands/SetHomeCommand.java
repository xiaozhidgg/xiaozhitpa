package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.command.argument.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class SetHomeCommand {

    private SetHomeCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("sethome")
                .then(argument("name", StringArgumentType.string())
                        .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int execute(ServerCommandSource source, String name) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        ServerWorld world = self.getServerWorld();
        String dim = world.getRegistryKey().getValue().toString();
        HomePos pos = new HomePos(self.getX(), self.getY(), self.getZ(),
                self.getYaw(), self.getPitch(), dim);
        PlayerData.get().getEntry(self.getUuid()).homes
                .computeIfAbsent(dim, k -> new HashMap<>()).put(name, pos);
        PlayerData.get().saveLater();
        source.sendFeedback(() -> Text.translatable("command.sethome.success", name), true);
        return 1;
    }
}
