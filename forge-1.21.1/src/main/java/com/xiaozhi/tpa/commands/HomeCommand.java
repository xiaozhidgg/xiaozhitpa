package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringArgumentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HomeCommand {

    public static final String DEFAULT_NAME = "home";

    private static final SuggestionProvider<CommandSourceStack> HOME_SUGGESTIONS = (context, builder) -> {
        ServerPlayer self = context.getSource().getPlayerOrException();
        ServerLevel level = self.serverLevel();
        ResourceLocation dim = level.dimension().location();
        Collection<String> names = PlayerData.get(level).getHomeNames(self.getUUID(), dim);
        return SharedSuggestionProvider.suggest(names, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("home")
                .executes(ctx -> teleport(ctx.getSource(), DEFAULT_NAME))
                .then(argument("name", StringArgumentType.string())
                        .suggests(HOME_SUGGESTIONS)
                        .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int teleport(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        ServerLevel level = self.serverLevel();
        ResourceLocation dim = level.dimension().location();

        HomePos home = PlayerData.get(level).getHome(self.getUUID(), dim, name);
        if (home == null) {
            source.sendFailure(Component.translatable(
                    DEFAULT_NAME.equals(name) ? "command.home.no_default" : "command.home.not_found", name));
            return 0;
        }

        ServerLevel targetLevel = self.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, home.dimension));
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("command.home.invalid_dimension"));
            return 0;
        }

        SaveBackPosition.save(self);
        self.teleportTo(targetLevel, home.x, home.y, home.z, home.yaw, home.pitch);
        source.sendSuccess(() -> Component.translatable("command.home.teleported", name), true);
        return 1;
    }
}
