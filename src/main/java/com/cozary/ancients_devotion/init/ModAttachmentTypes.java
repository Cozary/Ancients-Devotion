package com.cozary.ancients_devotion.init;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

public class ModAttachmentTypes {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    private static final Supplier<AttachmentType<Float>> SOLTITIA_DEVOTION = ATTACHMENT_TYPES.register(
            "soltitia_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );

    private static final Supplier<AttachmentType<Float>> SILVAERIA_DEVOTION = ATTACHMENT_TYPES.register(
            "silvaeria_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );

    private static final Supplier<AttachmentType<Float>> PATIGEO_DEVOTION = ATTACHMENT_TYPES.register(
            "patigeo_devotion", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );

}
