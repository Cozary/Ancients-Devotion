package com.cozary.ancients_devotion;

import com.cozary.ancients_devotion.init.GodRegistry;
import com.cozary.ancients_devotion.init.ModAttachmentTypes;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;


@Mod(AncientsDevotion.MOD_ID)
public class AncientsDevotion {

    public static final String MOD_ID = "ancients_devotion";
    public static final Logger LOG = LogUtils.getLogger();


    public AncientsDevotion(IEventBus modEventBus, ModContainer modContainer) {
        //NeoForge.EVENT_BUS.register(this);

        ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        GodRegistry.registerGods();
    }
}
