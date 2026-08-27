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
            source.sendFailure(Component.literal("§c没有待处理的传送请求！"));
            return 0;
        }
        if (from != null && !req.from().equals(from.getUUID())) {
            source.sendFailure(Component.literal("§c你没有来自 " + from.getDisplayName().getString() + " 的传送请求。"));
            return 0;
        }

        ServerPlayer requester = self.getServer().getPlayerList().getPlayer(req.from());
        if (requester != null) {
            requester.sendSystemMessage(Component.literal("§c" + self.getDisplayName().getString() + " 拒绝了你的传送请求。"));
        }
        self.sendSystemMessage(Component.literal("§a已拒绝传送请求。"));
        TpaManager.removeRequest(self);
        return 1;
    }
}
