package com.cozary.ancients_devotion.gods.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public abstract class AbstractGodBehavior implements GodBehaviour {
    @Override
    public void onTick(Player player) {
    }

    @Override
    public void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
    }

    @Override
    public void onAttackPlayer(Player player, Entity target, LivingIncomingDamageEvent event) {
    }

    @Override
    public void onPlayerDeath(Player player, LivingDeathEvent event) {
    }

    @Override
    public void onPlayerBreakBlock(Player player, BlockEvent.BreakEvent event) {
    }
}
