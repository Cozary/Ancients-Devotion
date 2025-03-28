package com.cozary.ancients_devotion.gods.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public interface GodBehaviour {
    void onTick(Player player);

    void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event);

    void onAttackPlayer(Player player, Entity target, LivingIncomingDamageEvent event);

    void onPlayerDeath(Player player, LivingDeathEvent event);

    void onPlayerBreakBlock(Player player, BlockEvent.BreakEvent event);

    void onPlayerEatItem(Player player, LivingEntityUseItemEvent.Finish event);
}
