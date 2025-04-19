package com.cozary.ancients_devotion.init;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.items.MedallionItem;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AncientsDevotion.MOD_ID);

    public static LinkedHashSet<Supplier<Item>> CREATIVE_TAB_ITEMS = Sets.newLinkedHashSet();

    public static final Supplier<Item> MEDALLION = registerWithTab("medallion", MedallionItem::new);

    public static Supplier<Item> registerWithTab(final String name, final Supplier<? extends Item> supplier) {
        Supplier<Item> item = ITEMS.register(name, supplier);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }


}
