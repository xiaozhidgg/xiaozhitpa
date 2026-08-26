package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.data.TpaRequest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TpdenyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tpdeny")
                .executes(ctx -> deny(ctx.getSource(), null))
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> deny(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));
    }

    /** Deny the pending request from {@code from}; when {@code from} is null, deny any pending request. */
    private static int deny(CommandSourceStack source, ServerPlayer from) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();

        TpaRequest req = TpaManager.getIncoming(self);
        if (req == null) {
            source.sendFailure(Component.translatable("command.tpdeny.no_request"));
            return 0;
        }
        if (from != null && !req.from().equals(from.getUUID())) {
            source.sendFailure(Component.translatable("command.tpdeny.not_from_player", from.getDisplayName()));
            return 0;
        }

        ServerPlayer requester = self.getServer().getPlayerList().getPlayer(req.from());
        if (requester != null) {
            requester.sendSystemMessage(Component.translatable("command.tpdeny.denied", self.getDisplayName()));
        }
        self.sendSystemMessage(Component.translatable("command.tpdeny.denied_success"));
        TpaManager.removeRequest(self);
        return 1;
    }
}
