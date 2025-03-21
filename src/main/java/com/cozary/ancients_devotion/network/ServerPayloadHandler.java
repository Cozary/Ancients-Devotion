package com.cozary.ancients_devotion.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleGodDataOnMain(final GodData data, final IPayloadContext context) {
        //?
        PacketDistributor.sendToPlayer((ServerPlayer) context.player(), data);
    }
}




