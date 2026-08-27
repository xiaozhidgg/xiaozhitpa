package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.TpaManager;
import com.xiaozhi.tpa.data.TpaRequest;
import com.xiaozhi.tpa.util.SaveBackPosition;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TpacceptCommand {

    private TpacceptCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpaccept")
                .executes(ctx -> accept(ctx.getSource(), null))
                .then(literal("all").executes(ctx -> accept(ctx.getSource(), null)))
                .then(argument("target", EntityArgumentType.player())
                        .executes(ctx -> accept(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target")))));
    }

    private static int accept(ServerCommandSource source, ServerPlayerEntity target) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }

        TpaRequest req = TpaManager.getIncoming(self.getUuid());
        if (req == null) {
            source.sendError(Text.literal("§c没有待处理的传送请求！"));
            return 0;
        }
        if (target != null && !req.from().equals(target.getUuid())) {
            source.sendError(Text.literal("§c你没有来自 " + target.getDisplayName().getString() + " 的传送请求。"));
            return 0;
        }

        ServerPlayerEntity sender = self.getServer().getPlayerManager().getPlayer(req.from());
        if (sender == null) {
            source.sendError(Text.literal("§c请求者已离线。"));
            TpaManager.removeRequest(self.getUuid());
            return 0;
        }

        SaveBackPosition.save(sender);
        sender.teleport(self.getServerWorld(), self.getX(), self.getY(), self.getZ(),
                self.getYaw(), self.getPitch());
        sender.sendMessage(Text.literal("§a已tpa至 " + self.getDisplayName().getString() + " 处。"), false);
        self.sendMessage(Text.literal("§a已同意 " + sender.getDisplayName().getString() + " 的传送。"), false);
        TpaManager.removeRequest(self.getUuid());
        return 1;
    }
}
