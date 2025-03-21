package com.cozary.ancients_devotion.events;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.God;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.cozary.ancients_devotion.util.DevotionHandler.getCurrentGod;
import static com.cozary.ancients_devotion.util.DevotionHandler.getGod;

@EventBusSubscriber(modid = AncientsDevotion.MOD_ID)
public class EventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onTick(player);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onAttack(player, event.getEntity(), event);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onPlayerDeath(player, event);
        }

    }

}
