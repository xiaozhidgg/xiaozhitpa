package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TpaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tpa")
                .then(argument("target", EntityArgument.player())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.getPlayer(ctx, "target")))));
    }

    private static int execute(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();

        if (self.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.translatable("command.tpa.self"));
            return 0;
        }

        if (TpaManager.hasOutgoing(self)) {
            source.sendFailure(Component.translatable("command.tpa.request_exists"));
            return 0;
        }

        // If the target has auto-accept enabled, teleport immediately.
        if (PlayerData.get(self.serverLevel()).isAutoAcceptTpa(target.getUUID())) {
            SaveBackPosition.save(self);
            self.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(),
                    target.getYRot(), target.getXRot());
            self.sendSystemMessage(Component.translatable("command.tpa.auto_accepted", target.getDisplayName()));
            target.sendSystemMessage(Component.translatable("command.tpa.auto_accepted_by", self.getDisplayName()));
            return 1;
        }

        if (TpaManager.hasIncoming(target)) {
            source.sendFailure(Component.translatable("command.tpa.target_has_request", target.getDisplayName()));
            return 0;
        }

        TpaManager.sendRequest(self, target);
        source.sendSuccess(() -> Component.translatable("command.tpa.sent", target.getDisplayName()), false);
        target.sendSystemMessage(Component.translatable("command.tpa.received", self.getDisplayName()));
        return 1;
    }
}
