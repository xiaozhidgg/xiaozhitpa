package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.registry.Registry;
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
        String dim = self.getServerWorld().getRegistryKey().getValue().toString();
        Collection<String> names = PlayerData.get().getEntry(self.getUuid()).homes
                .getOrDefault(dim, Collections.emptyMap()).keySet();
        return CommandSource.suggestMatching(names, builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home")
                .executes(ctx -> teleport(ctx.getSource(), DEFAULT_NAME))
                .then(argument("name", StringArgumentType.string())
                        .suggests(HOME_SUGGESTIONS)
                        .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int teleport(ServerCommandSource source, String name) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        String dim = self.getServerWorld().getRegistryKey().getValue().toString();
        HomePos home = PlayerData.get().getEntry(self.getUuid()).homes
                .getOrDefault(dim, Collections.emptyMap()).get(name);
        if (home == null) {
            source.sendError(Text.translatable(
                    DEFAULT_NAME.equals(name) ? "command.home.no_default" : "command.home.not_found", name));
            return 0;
        }

        RegistryKey<World> dimKey = RegistryKey.of(Registry.WORLD_KEY, home.identifier());
        ServerWorld targetWorld = self.getServer().getWorld(dimKey);
        if (targetWorld == null) {
            source.sendError(Text.translatable("command.home.invalid_dimension"));
            return 0;
        }

        SaveBackPosition.save(self);
        self.teleport(targetWorld, home.x, home.y, home.z, home.yaw, home.pitch);
        source.sendFeedback(() -> Text.translatable("command.home.teleported", name), true);
        return 1;
    }
}
