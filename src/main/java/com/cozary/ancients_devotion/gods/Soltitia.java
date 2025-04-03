package com.cozary.ancients_devotion.gods;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.AbstractGodBehavior;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Soltitia extends AbstractGodBehavior {
    private static final float PASSIVE_GAIN_RATE = 0.00005f; //Per tick
    private static final ResourceLocation LITTLE_SUN_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "little_sun_attack_debuff");
    private static final ResourceLocation SHARED_LIGHT_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "shared_light_speed_modifier");
    private static final ResourceLocation CURSED_SHADOW_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "cursed_shadow_speed_modifier");
    private static final ResourceLocation CURSED_SHADOW_DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "cursed_shadow_damage_modifier");
    private final Set<LivingEntity> affectedEntities = new HashSet<>();
    private final Set<Player> affectedPlayers = new HashSet<>();

    @Override
    public void onTick(Player player) {

        //Devotion
        increaseDevotion(player);

        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 5)) {
            applyLittleSun(player);
        }
        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 10)) {
            applyDivineHealing(player);
        }
        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 20)) {
            applySharedLight(player);
        }

        applyCursedShadow(player);
    }

    @Override
    public void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 30)) {
            applyBurningJudgment(player, target, event);
        }
        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 60)) {
            applyJudgmentOfLight(player, target, event);
        }

        applyVowOfJustice(player, target);
    }

    @Override
    public void onPlayerDeath(Player player, LivingDeathEvent event) {
        if (DevotionHandler.hasDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), 80)) {
            applySunProtection(player, event);
        }

        applyBetrayedLight(player);
    }

    //Check everything have this
    public static boolean isInSunLight(Player player) {
        return player.level().canSeeSky(player.blockPosition()) && player.level().isDay();
    }

    private void increaseDevotion(Player player) {
        if (isInSunLight(player)) {
            DevotionHandler.increaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), PASSIVE_GAIN_RATE);
        }
    }

    private void applyLittleSun(Player player) {
        float radius = Math.min(15.0f, Math.max(5.0f, DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.2f) + 5.0f); //Lv50 max value
        float damageReduction = Math.min(25.0f, Math.max(1.0f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) - 5) * 0.25f + 1)); //Lv100 max value

        if (!isInSunLight(player))
            return;

        List<LivingEntity> currentEntities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                entity -> entity instanceof Mob
        );

        for (LivingEntity entity : currentEntities) {
            if (!affectedEntities.contains(entity)) {
                AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackDamage != null && attackDamage.getModifier(LITTLE_SUN_MODIFIER_ID) == null) {
                    AttributeModifier modifier = new AttributeModifier(
                            LITTLE_SUN_MODIFIER_ID,
                            damageReduction,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    );
                    attackDamage.addTransientModifier(modifier);
                    affectedEntities.add(entity);
                }
            }
        }

        affectedEntities.removeIf(entity -> {
            if (!currentEntities.contains(entity)) {
                AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackDamage != null) {
                    attackDamage.removeModifier(LITTLE_SUN_MODIFIER_ID);
                }
                return true;
            }
            return false;
        });
    }

    private void applySunProtection(Player player, LivingDeathEvent event) {

        if (!isInSunLight(player))
            return;

        //Tbd cd

        event.setCanceled(true);

        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 125, 2));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 350, 4));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2));
        player.level().broadcastEntityEvent(player, (byte) 35);
    }

    private void applyDivineHealing(Player player) {
        float healRegen = Math.min(0.5f, Math.max(0.05f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.005f))); //Lv100 max value

        if (player.getHealth() < player.getMaxHealth() || isInSunLight(player)) {
            player.heal((float) (double) healRegen);
        }
    }

    private void applyBurningJudgment(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        RandomSource random = player.level().random;
        float setFireProbability = Math.min(1.0f, Math.max(0.1f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 1.14f) - 14.3f)); //Lv100 max value
        int fireTicks = (int) Math.min(100, Math.max(20, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 1.25f) - 25.0f)); //Lv100 max value
        float undeadDamagePercentage = Math.min(50.0f, Math.max(10.0f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.057f) - 7.15f)); //Lv100 max value
        float instaKillProbability = Math.min(0.01f, Math.max(0.001f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.00012f) - 0.003f)); //Lv100 max value

        if (!isInSunLight(player))
            return;

        if (random.nextFloat() < setFireProbability) {
            target.setRemainingFireTicks(fireTicks);
        }

        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            event.setAmount(event.getAmount() * undeadDamagePercentage);
        }

        if (random.nextFloat() < instaKillProbability) {
            event.setAmount(target.getMaxHealth());
        }
    }

    //Add health-regeneration
    private void applySharedLight(Player player) {
        if (!isInSunLight(player))
            return;

        float radius = Math.min(30.0f, Math.max(10.0f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.25f) + 5.0f)); //Lv100 max value
        float speedBonusPercentage = Math.min(25.0f, Math.max(1.0f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.3f) - 5.0f)); //Lv100 max value
        float healRegen = Math.min(0.5f, Math.max(0.05f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.005f))); //Lv100 max value

        List<Player> currentPlayers = player.level().getEntitiesOfClass(
                Player.class,
                player.getBoundingBox().inflate(radius),
                otherPlayer -> otherPlayer != player //Need test in MP ._. sure 0%
        );

        for (Player currentPlayer : currentPlayers) {
            if (!affectedPlayers.contains(currentPlayer)) {
                AttributeInstance movementSpeed = currentPlayer.getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeed != null && movementSpeed.getModifier(SHARED_LIGHT_MODIFIER_ID) == null) {
                    AttributeModifier modifier = new AttributeModifier(
                            SHARED_LIGHT_MODIFIER_ID,
                            speedBonusPercentage,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    );
                    movementSpeed.addTransientModifier(modifier);
                    affectedPlayers.add(currentPlayer);

                    if (currentPlayer.getHealth() < currentPlayer.getMaxHealth()) {
                        currentPlayer.heal((float) (double) healRegen);
                    }
                }
            }
        }

        affectedPlayers.removeIf(currentPlayer -> {
            if (!currentPlayers.contains(currentPlayer)) {
                AttributeInstance movementSpeed = currentPlayer.getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeed != null) {
                    movementSpeed.removeModifier(SHARED_LIGHT_MODIFIER_ID);
                }
                return true;
            }
            return false;
        });
    }

    private void applyJudgmentOfLight(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        if (!isInSunLight(player))
            return;

        float glowingDuration = Math.min(100.0f, Math.max(20.0f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 2.0f) - 100)); //Lv100 max value
        float armorReduction = Math.min(0.75f, Math.max(0.99f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * -0.006f) + 1.35f)); //Lv100 max value


        Scoreboard scoreboard = player.getScoreboard();

        PlayerTeam playerTeam = scoreboard.getPlayerTeam("judgmentofLightTargets");
        if (playerTeam == null) {
            playerTeam = scoreboard.addPlayerTeam("judgmentofLightTargets");
            playerTeam.setColor(ChatFormatting.LIGHT_PURPLE);
        }

        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, (int) glowingDuration, 30));
        scoreboard.addPlayerToTeam(target.getStringUUID(), playerTeam);

        if (target.hasEffect(MobEffects.GLOWING)) {
            event.getContainer().addModifier(DamageContainer.Reduction.ARMOR, (container, baseReduction) -> baseReduction * armorReduction); //Armor Reduction.
        }
    }

    private void applyCursedShadow(Player player) {
        if (!isInSunLight(player)) {
            DevotionHandler.decreaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), PASSIVE_GAIN_RATE / 2); //Create unique variable?
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null && movementSpeed.getModifier(CURSED_SHADOW_MODIFIER_ID) == null) {
                AttributeModifier speedModifier = new AttributeModifier(
                        CURSED_SHADOW_MODIFIER_ID,
                        -20.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );
                movementSpeed.addTransientModifier(speedModifier);
            }

            AttributeInstance damageTaken = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageTaken != null && damageTaken.getModifier(CURSED_SHADOW_DAMAGE_MODIFIER_ID) == null) {
                AttributeModifier damageModifier = new AttributeModifier(
                        CURSED_SHADOW_DAMAGE_MODIFIER_ID,
                        -50.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );
                damageTaken.addTransientModifier(damageModifier);
            }

        } else {
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(CURSED_SHADOW_MODIFIER_ID);
            }

            AttributeInstance damageTaken = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageTaken != null) {
                damageTaken.removeModifier(CURSED_SHADOW_DAMAGE_MODIFIER_ID);
            }
        }
    }

    private void applyVowOfJustice(Player player, LivingEntity target) {
        if (target.getType().getCategory() == MobCategory.CREATURE) {
            DevotionHandler.decreaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), PASSIVE_GAIN_RATE * 10); //Create unique variable?
        }
    }

    private void applyBetrayedLight(Player player) {
        DevotionHandler.decreaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), PASSIVE_GAIN_RATE * 100); //Create unique variable?
    }
}

