package com.cozary.ancients_devotion.gods.core;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public interface GodBehaviour {
    void onTick(Player player);

    void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event);

    void onPlayerDeath(Player player, LivingDeathEvent event);
}
