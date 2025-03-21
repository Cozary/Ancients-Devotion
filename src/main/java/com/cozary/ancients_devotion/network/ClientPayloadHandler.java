package com.cozary.ancients_devotion.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.cozary.ancients_devotion.init.ModAttachmentTypes.CURRENT_GOD;

public class ClientPayloadHandler {

    public static void handleGodDataOnMain(final GodData data, final IPayloadContext context) {
        String godName = data.godName();

        context.player().setData(CURRENT_GOD, godName);
    }
}



