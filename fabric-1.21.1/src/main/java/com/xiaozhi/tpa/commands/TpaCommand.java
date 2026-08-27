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
            source.sendError(Text.literal("§c不能向自己发送传送请求！"));
            return 0;
        }

        if (TpaManager.hasOutgoing(self.getUuid())) {
            source.sendError(Text.literal("§c你已有一个待处理的传送请求。"));
            return 0;
        }

        if (PlayerData.get().getEntry(target.getUuid()).autoAccept) {
            SaveBackPosition.save(self);
            self.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(),
                    target.getYaw(), target.getPitch());
            self.sendMessage(Text.literal("§a已tpa至 " + target.getDisplayName().getString() + " 处。"), false);
            target.sendMessage(Text.literal("§a" + self.getDisplayName().getString() + " 已自动传送到你这里。"), false);
            return 1;
        }

        if (TpaManager.hasIncoming(target.getUuid())) {
            source.sendError(Text.literal("§c" + target.getDisplayName().getString() + " 已有待处理的传送请求。"));
            return 0;
        }

        TpaManager.sendRequest(self.getUuid(), target.getUuid());
        source.sendFeedback(() -> Text.literal("§a已向 " + target.getDisplayName().getString() + " 发送传送请求。"), false);
        target.sendMessage(Text.literal("§b" + self.getDisplayName().getString() + " 想传送到你这里，输入 /tpaccept 接受或 /tpdeny 拒绝。"), false);
        return 1;
    }
}
