package com.cozary.ancients_devotion.commands;

import com.cozary.ancients_devotion.util.DevotionHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setgod")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("god", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            String god = StringArgumentType.getString(context, "god");

                                            DevotionHandler.setCurrentGod(player, god);

                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    "Set god of " + player.getName().getString() + " to " + god), true);
                                            return 1;
                                        })
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("setdevotion")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("devotion", StringArgumentType.word())
                                        .then(Commands.argument("quantity", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String devotion = StringArgumentType.getString(context, "devotion");
                                                    int quantity = IntegerArgumentType.getInteger(context, "quantity");

                                                    DevotionHandler.setDevotion(player, DevotionHandler.getGod(devotion), quantity);

                                                    context.getSource().sendSuccess(() -> Component.literal(
                                                            "Set devotion of " + player.getName().getString() +
                                                                    " to " + quantity + " for " + devotion), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}

