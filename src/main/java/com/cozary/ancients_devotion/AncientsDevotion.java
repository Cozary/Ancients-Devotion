package com.cozary.ancients_devotion;

import com.cozary.ancients_devotion.client.menu.GodScreen;
import com.cozary.ancients_devotion.commands.ModCommands;
import com.cozary.ancients_devotion.init.GodRegistry;
import com.cozary.ancients_devotion.init.ModAttachmentTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;


@Mod(AncientsDevotion.MOD_ID)
public class AncientsDevotion {

    public static final String MOD_ID = "ancients_devotion";
    public static final Logger LOG = LogUtils.getLogger();


    public AncientsDevotion(IEventBus modEventBus, ModContainer modContainer) {
        //NeoForge.EVENT_BUS.register(this);

        ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);


        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher());

        event.getDispatcher().register(
                Commands.literal("god")
                        .requires(source -> source.hasPermission(0))
                        .executes(context -> {
                            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new GodScreen()));
                            return 1;
                        })
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        GodRegistry.registerGods();
    }
}
