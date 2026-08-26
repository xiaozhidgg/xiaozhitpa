package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandRegister {
    private CommandRegister() {}

    public static void registerAll(CommandDispatcher<ServerCommandSource> dispatcher) {
        TpaCommand.register(dispatcher);
        TpacceptCommand.register(dispatcher);
        TpdenyCommand.register(dispatcher);
        TpautoCommand.register(dispatcher);
        SetHomeCommand.register(dispatcher);
        HomeCommand.register(dispatcher);
        DelHomeCommand.register(dispatcher);
        ListHomeCommand.register(dispatcher);
        BackCommand.register(dispatcher);
    }
}
