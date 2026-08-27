package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.data.TpaRequest;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TpacceptCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tpaccept")
                .executes(ctx -> accept(ctx.getSource(), null))
                .then(literal("all").executes(ctx -> accept(ctx.getSource(), null)))
                .then(argument("target", EntityArgument.player())
                        .executes(ctx -> accept(ctx.getSource(), EntityArgument.getPlayer(ctx, "target")))));
    }

    /** Accept the pending request from {@code target}; when {@code target} is null, accept any pending request. */
    private static int accept(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();

        TpaRequest req = TpaManager.getIncoming(self);
        if (req == null) {
            source.sendFailure(Component.literal("§c没有待处理的传送请求！"));
            return 0;
        }
        if (target != null && !req.from().equals(target.getUUID())) {
            source.sendFailure(Component.literal("§c你没有来自 " + target.getDisplayName().getString() + " 的传送请求。"));
            return 0;
        }

        ServerPlayer sender = self.getServer().getPlayerList().getPlayer(req.from());
        if (sender == null) {
            source.sendFailure(Component.literal("§c请求者已离线。"));
            TpaManager.removeRequest(self);
            return 0;
        }

        SaveBackPosition.save(sender);
        sender.teleportTo(self.serverLevel(), self.getX(), self.getY(), self.getZ(),
                self.getYRot(), self.getXRot());
        sender.sendSystemMessage(Component.literal("§a已tpa至 " + self.getDisplayName().getString() + " 处。"));
        self.sendSystemMessage(Component.literal("§a已同意 " + sender.getDisplayName().getString() + " 的传送。"));
        TpaManager.removeRequest(self);
        return 1;
    }
}
