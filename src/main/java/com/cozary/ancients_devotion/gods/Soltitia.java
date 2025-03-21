package com.cozary.ancients_devotion.gods;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.AbstractGodBehavior;
import com.cozary.ancients_devotion.gods.core.God;
import com.cozary.ancients_devotion.init.GodRegistry;
import com.cozary.ancients_devotion.network.GodData;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Soltitia extends AbstractGodBehavior {
    private static final float PASSIVE_GAIN_RATE = 0.00005f; //Per tick
    private static final ResourceLocation LITTLE_SUN_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "little_sun_attack_debuff");
    private static final ResourceLocation SHARED_LIGHT_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "shared_light_speed_modifier");
    private static final ResourceLocation CURSED_SHADOW_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "shared_light_speed_modifier");
    private static final ResourceLocation CURSED_SHADOW_DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "shared_light_damage_modifier");
    public static God SOLTITIA;
    private final Set<LivingEntity> affectedEntities = new HashSet<>();
    private final Set<Player> affectedPlayers = new HashSet<>();

    private static void testing(Player player) {

        DevotionHandler.setCurrentGod(player, "soltitia");
        SOLTITIA = GodRegistry.GODS.get(DevotionHandler.getCurrentGod(player));
        PacketDistributor.sendToPlayer((ServerPlayer) player, new GodData(SOLTITIA.getName()));

        //AncientsDevotion.LOG.info("Current Devotion: {}", DevotionHandler.getDevotion(player, SOLTITIA));
    }

    @Override
    public void onTick(Player player) {
        testing(player);

        isPlayerLookingAtSun(player);

        increaseDevotion(player);
        applyLittleSun(player);
        applyDivineHealing(player);
        applySharedLight(player);
        applyCursedShadow(player);
    }

    @Override
    public void onPlayerDeath(Player player, LivingDeathEvent event) {
        applySunProtection(player, event);
        applyBetrayedLight(player);
    }

    @Override
    public void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        applyBurningJudgment(player, target, event);
        applyJudgmentOfLight(player, target, event);
        applyVowOfJustice(player, target);
    }

    //Ritual
    public void isPlayerLookingAtSun(Player player) {
        Level level = player.level();

        if (!level.isDay()) {
            return;
        }

        float sunAngle = level.getSunAngle(player.tickCount) + (float) Math.PI / 2; //-pi/2 moon
        Vec3 view = player.getViewVector(1.0f).normalize();
        Vec3 sun = new Vec3(Math.cos(sunAngle), Math.sin(sunAngle), 0f).normalize();

        double dot = view.dot(sun);
        double threshold = 0.995;
        boolean lookingAtSun = dot >= threshold;

        String msg = String.format(Locale.ROOT,
                "SunAngle: %.3f | View: (%.3f, %.3f, %.3f) | SunDir: (%.3f, %.3f, %.3f) | Dot: %.3f | LookingAtSun: %s",
                sunAngle, view.x, view.y, view.z, sun.x, sun.y, sun.z, dot, lookingAtSun);

        player.sendSystemMessage(Component.literal(msg));
    }

    //Check everything have this
    public boolean isInSunLight(Player player) {
        return player.level().canSeeSky(player.blockPosition()) && player.level().isDay();
    }

    private void increaseDevotion(Player player) {
        if (isInSunLight(player)) {
            DevotionHandler.increaseDevotion(player, SOLTITIA, PASSIVE_GAIN_RATE);
        }
    }

    private void applyLittleSun(Player player) {
        int radius = 5; //Radius tbd

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
                            -2.0, //Reduce amount
                            AttributeModifier.Operation.ADD_VALUE
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
        if (player.getHealth() < player.getMaxHealth() || isInSunLight(player)) {
            player.heal((float) (double) 0.01f); //Heal regen need tweak
        }
    }

    private void applyBurningJudgment(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        RandomSource random = player.level().random;

        if (!isInSunLight(player))
            return;

        if (random.nextFloat() < 0.10f) {
            target.setRemainingFireTicks(100);
        }

        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            event.setAmount(event.getAmount() * 1.15f);
        }

        if (random.nextFloat() < 0.01f) {
            event.setAmount(target.getMaxHealth()); //kill
        }
    }

    //Add health-regeneration
    private void applySharedLight(Player player) {
        if (!isInSunLight(player))
            return;

        int radius = 5; //tbd

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
                            1.0,
                            AttributeModifier.Operation.ADD_VALUE
                    );
                    movementSpeed.addTransientModifier(modifier);
                    affectedPlayers.add(currentPlayer);
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

        Scoreboard scoreboard = player.getScoreboard();

        PlayerTeam playerTeam = scoreboard.getPlayerTeam("dragonsEyeTargets");
        if (playerTeam == null) {
            playerTeam = scoreboard.addPlayerTeam("dragonsEyeTargets");
            playerTeam.setColor(ChatFormatting.LIGHT_PURPLE);
        }

        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 30));
        scoreboard.addPlayerToTeam(target.getStringUUID(), playerTeam);

        if (target.hasEffect(MobEffects.GLOWING)) {
            event.getContainer().addModifier(DamageContainer.Reduction.ARMOR, (container, baseReduction) -> baseReduction * 0.75f); //Armor Reduction.
        }
    }

    private void applyCursedShadow(Player player) {
        if (!isInSunLight(player)) {
            DevotionHandler.decreaseDevotion(player, SOLTITIA, PASSIVE_GAIN_RATE / 2); //Create unique variable?
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null && movementSpeed.getModifier(CURSED_SHADOW_MODIFIER_ID) == null) {
                AttributeModifier speedModifier = new AttributeModifier(
                        CURSED_SHADOW_MODIFIER_ID,
                        -1.0,
                        AttributeModifier.Operation.ADD_VALUE
                );
                movementSpeed.addTransientModifier(speedModifier);
            }

            AttributeInstance damageTaken = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageTaken != null && damageTaken.getModifier(CURSED_SHADOW_DAMAGE_MODIFIER_ID) == null) {
                AttributeModifier damageModifier = new AttributeModifier(
                        CURSED_SHADOW_DAMAGE_MODIFIER_ID,
                        -5.0,
                        AttributeModifier.Operation.ADD_VALUE
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
            DevotionHandler.decreaseDevotion(player, SOLTITIA, PASSIVE_GAIN_RATE * 10); //Create unique variable?
        }
    }

    private void applyBetrayedLight(Player player) {
        DevotionHandler.decreaseDevotion(player, SOLTITIA, PASSIVE_GAIN_RATE * 20); //Create unique variable?
    }
}

