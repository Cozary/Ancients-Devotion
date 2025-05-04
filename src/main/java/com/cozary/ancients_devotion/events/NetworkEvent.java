package com.cozary.ancients_devotion.events;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.network.ClientPayloadHandler;
import com.cozary.ancients_devotion.network.data.*;
import com.cozary.ancients_devotion.network.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = AncientsDevotion.MOD_ID)
public class NetworkEvent {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                GodData.TYPE,
                GodData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleGodDataOnMain,
                        ServerPayloadHandler::handleGodDataOnMain
                )
        );

        registrar.playBidirectional(
                PatigeoDevotionData.TYPE,
                PatigeoDevotionData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handlePatigeoDevotionData,
                        ServerPayloadHandler::handlePatigeoDevotionData
                )
        );

        registrar.playBidirectional(
                SilvaeriaDevotionData.TYPE,
                SilvaeriaDevotionData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleSilvaeriaDevotionData,
                        ServerPayloadHandler::handleSilvaeriaDevotionData
                )
        );

        registrar.playBidirectional(
                SoltitiaDevotionData.TYPE,
                SoltitiaDevotionData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleSoltitiaDevotionData,
                        ServerPayloadHandler::handleSoltitiaDevotionData
                )
        );

        registrar.playBidirectional(
                SilvaeriaCropsCountData.TYPE,
                SilvaeriaCropsCountData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleSilvaeriaCropsCountData,
                        ServerPayloadHandler::handleSilvaeriaCropsCountData
                )
        );

    }

}
