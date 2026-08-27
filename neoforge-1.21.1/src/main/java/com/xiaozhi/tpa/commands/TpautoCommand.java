package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

/**
 * Toggles the sender's auto-accept flag. When enabled, incoming /tpa requests
 * are accepted automatically.
 */
public class TpautoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tpauto").executes(ctx -> toggle(ctx.getSource())));
    }

    private static int toggle(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        PlayerData data = PlayerData.get(self.serverLevel());
        boolean isAuto = data.isAutoAcceptTpa(self.getUUID());
        data.setAutoAcceptTpa(self.getUUID(), !isAuto);
        self.sendSystemMessage(Component.literal(!isAuto ? "§a已开启自动接受tpa。" : "§c已关闭自动接受tpa。"));
        return 1;
    }
}
