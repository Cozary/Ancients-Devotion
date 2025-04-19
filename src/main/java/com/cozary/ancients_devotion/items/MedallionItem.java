package com.cozary.ancients_devotion.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MedallionItem extends Item {

    public MedallionItem() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(64));
    }
}
