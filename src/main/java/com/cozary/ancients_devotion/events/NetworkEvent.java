package com.cozary.ancients_devotion.events;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.network.ClientPayloadHandler;
import com.cozary.ancients_devotion.network.GodData;
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
    }

}
