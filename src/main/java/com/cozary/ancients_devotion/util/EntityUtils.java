package com.cozary.ancients_devotion.util;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;


public class EntityUtils {

    public static void applyAttributeModifier(AttributeInstance instance, AttributeModifier modifier) {
        if (!instance.hasModifier(modifier.id()))
            instance.addTransientModifier(modifier);
    }

    public static void removeAttributeModifier(AttributeInstance instance, AttributeModifier modifier) {
        if (instance.hasModifier(modifier.id()))
            instance.removeModifier(modifier.id());
    }
}
