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
        PlayerData data = PlayerData.get((ServerLevel) self.getLevel());
        boolean isAuto = data.isAutoAcceptTpa(self.getUUID());
        data.setAutoAcceptTpa(self.getUUID(), !isAuto);
        self.sendSystemMessage(Component.translatable(!isAuto ? "command.tpauto.enabled" : "command.tpauto.disabled"));
        return 1;
    }
}
