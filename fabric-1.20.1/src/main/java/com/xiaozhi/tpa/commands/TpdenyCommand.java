package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.data.TpaRequest;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TpdenyCommand {

    private TpdenyCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpdeny")
                .executes(ctx -> deny(ctx.getSource(), null))
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> deny(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))));
    }

    private static int deny(ServerCommandSource source, ServerPlayerEntity from) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }

        TpaRequest req = TpaManager.getIncoming(self.getUuid());
        if (req == null) {
            source.sendError(Text.translatable("command.tpdeny.no_request"));
            return 0;
        }
        if (from != null && !req.from().equals(from.getUuid())) {
            source.sendError(Text.translatable("command.tpdeny.not_from_player", from.getDisplayName()));
            return 0;
        }

        ServerPlayerEntity requester = self.getServer().getPlayerManager().getPlayer(req.from());
        if (requester != null) {
            requester.sendMessage(Text.translatable("command.tpdeny.denied", self.getDisplayName()), false);
        }
        self.sendMessage(Text.translatable("command.tpdeny.denied_success"), false);
        TpaManager.removeRequest(self.getUuid());
        return 1;
    }
}
