package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TpaCommand {

    private TpaCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpa")
                .then(argument("target", EntityArgumentType.player())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target")))));
    }

    private static int execute(ServerCommandSource source, ServerPlayerEntity target) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            source.sendError(Text.literal("Must be run as a player."));
            return 0;
        }

        if (self.getUuid().equals(target.getUuid())) {
            source.sendError(Text.translatable("command.tpa.self"));
            return 0;
        }

        if (TpaManager.hasOutgoing(self.getUuid())) {
            source.sendError(Text.translatable("command.tpa.request_exists"));
            return 0;
        }

        if (PlayerData.get().getEntry(target.getUuid()).autoAccept) {
            SaveBackPosition.save(self);
            self.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(),
                    target.getYaw(), target.getPitch());
            self.sendMessage(Text.translatable("command.tpa.auto_accepted", target.getDisplayName()), false);
            target.sendMessage(Text.translatable("command.tpa.auto_accepted_by", self.getDisplayName()), false);
            return 1;
        }

        if (TpaManager.hasIncoming(target.getUuid())) {
            source.sendError(Text.translatable("command.tpa.target_has_request", target.getDisplayName()));
            return 0;
        }

        TpaManager.sendRequest(self.getUuid(), target.getUuid());
        source.sendFeedback(() -> Text.translatable("command.tpa.sent", target.getDisplayName()), false);
        target.sendMessage(Text.translatable("command.tpa.received", self.getDisplayName()), false);
        return 1;
    }
}
