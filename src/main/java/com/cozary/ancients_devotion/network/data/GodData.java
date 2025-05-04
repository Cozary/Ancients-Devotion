package com.cozary.ancients_devotion.network.data;

import com.cozary.ancients_devotion.AncientsDevotion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GodData(String godName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GodData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "god_data"));

    public static final StreamCodec<ByteBuf, GodData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GodData::godName,
            GodData::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}





