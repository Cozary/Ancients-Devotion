package com.cozary.ancients_devotion.init;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

public class ModAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<String>> CURRENT_GOD = ATTACHMENT_TYPES.register(
            "current_god", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).copyOnDeath().build()
    );


    //Remember to send to client this ones
    public static final Supplier<AttachmentType<Float>> SOLTITIA_DEVOTION = ATTACHMENT_TYPES.register(
            "soltitia_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Float>> SILVAERIA_DEVOTION = ATTACHMENT_TYPES.register(
            "silvaeria_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Float>> PATIGEO_DEVOTION = ATTACHMENT_TYPES.register(
            "patigeo_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).copyOnDeath().build()
    );

    //
    public static final Supplier<AttachmentType<Integer>> SILVAERIA_CROPS_COUNT = ATTACHMENT_TYPES.register(
            "silvaeria_crops_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
    );

}
