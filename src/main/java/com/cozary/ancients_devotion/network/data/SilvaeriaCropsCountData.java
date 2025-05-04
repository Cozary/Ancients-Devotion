package com.cozary.ancients_devotion.network.data;

import com.cozary.ancients_devotion.AncientsDevotion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SilvaeriaCropsCountData(int value) implements CustomPacketPayload {
    public static final Type<SilvaeriaCropsCountData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("ancientsdevotion", "silvaeria_crops_count"));

    public static final StreamCodec<ByteBuf, SilvaeriaCropsCountData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, SilvaeriaCropsCountData::value, SilvaeriaCropsCountData::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

