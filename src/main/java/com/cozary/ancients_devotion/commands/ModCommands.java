package com.cozary.ancients_devotion.commands;

import com.cozary.ancients_devotion.gods.core.God;
import com.cozary.ancients_devotion.init.GodRegistry;
import com.cozary.ancients_devotion.network.GodData;
import com.cozary.ancients_devotion.util.DevotionHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public class ModCommands {
    public static final SuggestionProvider<CommandSourceStack> GOD_SUGGESTIONS = (context, builder) -> {
        for (Map.Entry<String, God> god : GodRegistry.GODS.entrySet()) {
            if (god.getValue().getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(god.getValue().getName());
            }
        }
        return builder.buildFuture();
    };

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setgod")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("god", StringArgumentType.word())
                                        .suggests(GOD_SUGGESTIONS)
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            String god = StringArgumentType.getString(context, "god");

                                            DevotionHandler.setCurrentGod(player, god);
                                            PacketDistributor.sendToPlayer(player, new GodData(god));
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
                                        .suggests(GOD_SUGGESTIONS)
                                        .then(Commands.argument("quantity", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String godDevotion = StringArgumentType.getString(context, "devotion");
                                                    int quantity = IntegerArgumentType.getInteger(context, "quantity");

                                                    DevotionHandler.setDevotion(player, DevotionHandler.getGod(godDevotion), quantity);

                                                    context.getSource().sendSuccess(() -> Component.literal(
                                                            "Set devotion of " + player.getName().getString() +
                                                                    " to " + quantity + " for " + godDevotion), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("checkdevotion")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    CommandSourceStack source = context.getSource();

                                    source.sendSuccess(() -> Component.literal(
                                            "Devotion of " + player.getName().getString() + ":"), false);

                                    for (Map.Entry<String, God> god : GodRegistry.GODS.entrySet()) {
                                        float devotion = DevotionHandler.getDevotion(player, god.getValue());
                                        source.sendSuccess(() -> Component.literal(
                                                " - " + god.getValue().getName() + ": " + devotion), false);
                                    }

                                    return 1;
                                })
                        )
        );

    }
}


