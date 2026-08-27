package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class HomeCommand {

    public static final String DEFAULT_NAME = "home";

    private HomeCommand() {}

    private static final SuggestionProvider<ServerCommandSource> HOME_SUGGESTIONS = (context, builder) -> {
        ServerPlayerEntity self = context.getSource().getPlayer();
        if (self == null) {
            return builder.buildFuture();
        }
        Collection<String> names = PlayerData.get().getAllHomeNames(self.getUuid());
        return CommandSource.suggestMatching(names, builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home")
                .executes(ctx -> teleport(ctx.getSource(), DEFAULT_NAME))
                .then(argument("name", StringArgumentType.string())
                        .suggests(HOME_SUGGESTIONS)
                        .executes(ctx -> teleport(ctx.getSource(), ctx.getArgument("name", String.class)))));
    }

    private static int teleport(ServerCommandSource source, String name) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        HomePos home = PlayerData.get().getHomeAnywhere(self.getUuid(), name);
        if (home == null) {
            source.sendError(Text.literal(DEFAULT_NAME.equals(name) ? "§c你还没有设置默认家。请先使用 /sethome。" : "§c未找到家点 " + name + "。"));
            return 0;
        }

        RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, home.identifier());
        ServerWorld targetWorld = self.getServer().getWorld(dimKey);
        if (targetWorld == null) {
            source.sendError(Text.literal("§c家所在的维度不可用！"));
            return 0;
        }

        SaveBackPosition.save(self);
        self.teleport(targetWorld, home.x, home.y, home.z, home.yaw, home.pitch);
        source.sendFeedback(() -> Text.literal("§a已返回家【" + name + "】。"), false);
        return 1;
    }
}
