package com.cozary.ancients_devotion.events;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.God;
import com.cozary.ancients_devotion.network.GodData;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
    public static void onAttackPlayer(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onAttackPlayer(player, event.getSource().getEntity(), event);
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

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onPlayerBreakBlock(player, event);
        }

    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String god = DevotionHandler.getCurrentGod(player);
            PacketDistributor.sendToPlayer(player, new GodData(god));
        }
    }

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onPlayerEatItem(player, event);
        }
    }

    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.LeftClickBlock event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        String godName = getCurrentGod(player);
        God god = getGod(godName);

        if (god != null) {
            god.getBehavior().onPlayerUseItem(player, event);
        }
    }


}
