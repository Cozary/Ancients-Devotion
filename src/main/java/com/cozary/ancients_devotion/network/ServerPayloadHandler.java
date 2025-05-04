package com.cozary.ancients_devotion.network;

import com.cozary.ancients_devotion.network.data.*;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.cozary.ancients_devotion.init.ModAttachmentTypes.*;
import static com.cozary.ancients_devotion.init.ModAttachmentTypes.SILVAERIA_CROPS_COUNT;

public class ServerPayloadHandler {

    public static void handleGodDataOnMain(final GodData data, final IPayloadContext context) {
        //?
        PacketDistributor.sendToServer(data);
    }

    public static void handleSoltitiaDevotionData(SoltitiaDevotionData data, IPayloadContext context) {
        PacketDistributor.sendToServer(data);
    }

    public static void handleSilvaeriaDevotionData(SilvaeriaDevotionData data, IPayloadContext context) {
        PacketDistributor.sendToServer(data);
    }

    public static void handlePatigeoDevotionData(PatigeoDevotionData data, IPayloadContext context) {
        PacketDistributor.sendToServer(data);
    }

    public static void handleSilvaeriaCropsCountData(SilvaeriaCropsCountData data, IPayloadContext context) {
        PacketDistributor.sendToServer(data);
    }
}




