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
            source.sendError(Text.literal("§c没有待处理的传送请求！"));
            return 0;
        }
        if (from != null && !req.from().equals(from.getUuid())) {
            source.sendError(Text.literal("§c你没有来自 " + from.getDisplayName().getString() + " 的传送请求。"));
            return 0;
        }

        ServerPlayerEntity requester = self.getServer().getPlayerManager().getPlayer(req.from());
        if (requester != null) {
            requester.sendMessage(Text.literal("§c" + self.getDisplayName().getString() + " 拒绝了你的传送请求。"), false);
        }
        self.sendMessage(Text.literal("§a已拒绝传送请求。"), false);
        TpaManager.removeRequest(self.getUuid());
        return 1;
    }
}
