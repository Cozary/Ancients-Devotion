package com.cozary.ancients_devotion.network;

import com.cozary.ancients_devotion.network.data.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.cozary.ancients_devotion.init.ModAttachmentTypes.*;

public class ClientPayloadHandler {

    public static void handleGodDataOnMain(final GodData data, final IPayloadContext context) {
        String godName = data.godName();

        context.player().setData(CURRENT_GOD, godName);
    }

    public static void handleSoltitiaDevotionData(SoltitiaDevotionData data, IPayloadContext context) {
        context.player().setData(SOLTITIA_DEVOTION.get(), data.value());
    }

    public static void handleSilvaeriaDevotionData(SilvaeriaDevotionData data, IPayloadContext context) {
        context.player().setData(SILVAERIA_DEVOTION.get(), data.value());
    }

    public static void handlePatigeoDevotionData(PatigeoDevotionData data, IPayloadContext context) {
        context.player().setData(PATIGEO_DEVOTION.get(), data.value());
    }

    public static void handleSilvaeriaCropsCountData(SilvaeriaCropsCountData data, IPayloadContext context) {
        context.player().setData(SILVAERIA_CROPS_COUNT.get(), data.value());
    }

}



