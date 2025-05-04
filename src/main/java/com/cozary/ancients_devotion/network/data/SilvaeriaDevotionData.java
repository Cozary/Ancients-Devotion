package com.cozary.ancients_devotion.network.data;

import com.cozary.ancients_devotion.AncientsDevotion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SilvaeriaDevotionData (float value) implements CustomPacketPayload {
    public static final Type<SilvaeriaDevotionData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "silvaeria_devotion"));

    public static final StreamCodec<ByteBuf, SilvaeriaDevotionData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.FLOAT, SilvaeriaDevotionData::value, SilvaeriaDevotionData::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}