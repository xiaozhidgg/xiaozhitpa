package com.xiaozhi.tpa.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiaozhi.tpa.data.PlayerData;
import com.xiaozhi.tpa.util.HomePos;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SetHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("sethome")
                .then(argument("name", StringArgumentType.string())
                        .executes(ctx -> execute(ctx.getSource(), ctx.getArgument("name", String.class)))));
    }

    private static int execute(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer self = source.getPlayerOrException();
        ServerLevel level = self.serverLevel();
        HomePos pos = new HomePos(self.getX(), self.getY(), self.getZ(),
                self.getYRot(), self.getXRot(), level.dimension().location());
        PlayerData.get(level).setHome(self.getUUID(), level.dimension().location(), name, pos);
        source.sendSuccess(() -> Component.literal("§a已设置家【" + name + "】。"), false);
        return 1;
    }
}
