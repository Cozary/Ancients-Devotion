package com.cozary.ancients_devotion.network.data;

import com.cozary.ancients_devotion.AncientsDevotion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PatigeoDevotionData (float value) implements CustomPacketPayload {
    public static final Type<PatigeoDevotionData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "patigeo_devotion"));

    public static final StreamCodec<ByteBuf, PatigeoDevotionData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.FLOAT, PatigeoDevotionData::value, PatigeoDevotionData::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}