package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.xiaozhi.tpa.data.PlayerData;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/** Toggles the sender's auto-accept flag. */
public final class TpautoCommand {

    private TpautoCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpauto").executes(ctx -> toggle(ctx.getSource())));
    }

    private static int toggle(ServerCommandSource source) {
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) {
            return 0;
        }
        PlayerData.Entry entry = PlayerData.get().getEntry(self.getUuid());
        boolean isAuto = entry.autoAccept;
        entry.autoAccept = !isAuto;
        PlayerData.get().saveLater();
        self.sendMessage(Text.literal(!isAuto ? "§a已开启自动接受tpa。" : "§c已关闭自动接受tpa。"), false);
        return 1;
    }
}
