package com.cozary.ancients_devotion.init;

import com.cozary.ancients_devotion.AncientsDevotion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AncientsDevotion.MOD_ID);

    public static final Supplier<CreativeModeTab> ANCIENTS_DEVOTION_TAB = CREATIVE_MODE_TAB.register(AncientsDevotion.MOD_ID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ancients_devotion"))
            .icon(() -> new ItemStack(ModItems.MEDALLION.get()))
            .displayItems((parameters, output) -> ModItems.CREATIVE_TAB_ITEMS.forEach((item) -> output.accept(item.get())))
            .build());

}




