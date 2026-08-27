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
            source.sendFailure(Component.literal("§c不能向自己发送传送请求！"));
            return 0;
        }

        if (TpaManager.hasOutgoing(self)) {
            source.sendFailure(Component.literal("§c你已有一个待处理的传送请求。"));
            return 0;
        }

        // If the target has auto-accept enabled, teleport immediately.
        if (PlayerData.get(self.serverLevel()).isAutoAcceptTpa(target.getUUID())) {
            SaveBackPosition.save(self);
            self.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(),
                    target.getYRot(), target.getXRot());
            self.sendSystemMessage(Component.literal("§a已tpa至 " + target.getDisplayName().getString() + " 处。"));
            target.sendSystemMessage(Component.literal("§a" + self.getDisplayName().getString() + " 已自动传送到你这里。"));
            return 1;
        }

        if (TpaManager.hasIncoming(target)) {
            source.sendFailure(Component.literal("§c" + target.getDisplayName().getString() + " 已有待处理的传送请求。"));
            return 0;
        }

        TpaManager.sendRequest(self, target);
        source.sendSuccess(() -> Component.literal("§a已向 " + target.getDisplayName().getString() + " 发送传送请求。"), false);
        target.sendSystemMessage(Component.literal("§b" + self.getDisplayName().getString() + " 想传送到你这里，输入 /tpaccept 接受或 /tpdeny 拒绝。"));
        return 1;
    }
}
