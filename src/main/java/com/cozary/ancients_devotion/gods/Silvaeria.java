package com.cozary.ancients_devotion.gods;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.AbstractGodBehavior;
import com.cozary.ancients_devotion.util.DevotionHandler;
import com.cozary.ancients_devotion.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.*;

public class Silvaeria extends AbstractGodBehavior {
    private static final ResourceLocation NATURAL_SHELTER_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "natural_shelter_armor_modifier");
    private static final float FIXED_GAIN_RATE = 0.1f;
    private static final List<Item> VEGETAL_FOODS = List.of(
            Items.CARROT,
            Items.POTATO,
            Items.BEETROOT,
            Items.MELON_SLICE,
            Items.PUMPKIN_SEEDS,
            Items.WHEAT,
            Items.BAKED_POTATO
    );

    private static final List<Item> IRON_OF_DIAMOND_ITEM = List.of(
            Items.IRON_SWORD,
            Items.IRON_PICKAXE,
            Items.IRON_AXE,
            Items.IRON_SHOVEL,
            Items.IRON_HOE,
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS,
            Items.DIAMOND_SWORD,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_AXE,
            Items.DIAMOND_SHOVEL,
            Items.DIAMOND_HOE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_AXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_HOE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS
    );


    @Override
    public void onTick(Player player) {
        super.onTick(player);
        applyNaturalMimicry(player);
        applySilentGrowth(player);
        applyNaturalShelter(player);
        applyOneWithTheWood(player);
    }

    @Override
    public void onAttackPlayer(Player player, Entity target, LivingIncomingDamageEvent event) {
        super.onAttackPlayer(player, target, event);
        applySpiritAlly(player, event);
    }

    @Override
    public void onPlayerBreakBlock(Player player, BlockEvent.BreakEvent event) {
        super.onPlayerBreakBlock(player, event);
        applyGreenResonance(player, event);
        increaseDevotion(player,event);
    }

    @Override
    public void onPlayerEatItem(Player player, LivingEntityUseItemEvent.Finish event) {
        increaseDevotionOther(player, event.getItem().getItem());
    }

    @Override
    public void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        applyGuardianCode(player, target);
    }

    @Override
    public void onPlayerUseItem(Player player, PlayerInteractEvent.LeftClickBlock event) {
        applyMetalHate(player, event.getItemStack().getItem(), event);
    }


    //Todo make this bonuses disappear when not holding.
    private void applyOneWithTheWood(Player player) {
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof TieredItem tieredItem && tieredItem.getTier() == Tiers.WOOD) {
            float devotion = DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)));
            if (devotion > 0) {
                float bonusAttackSpeed = 10f * devotion;
                float bonusAttackDamage = 10f * devotion;

                applyModifiers(mainHand, bonusAttackSpeed, bonusAttackDamage);
                modifyDurability(mainHand, devotion);

            }
        }
    }

    private void applyModifiers(ItemStack stack, float bonusAttackSpeed, float bonusAttackDamage) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        AttributeModifier attackSpeedModifier = new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "one_with_the_wood_attack_speed"),
                bonusAttackSpeed,
                AttributeModifier.Operation.ADD_VALUE
        );

        AttributeModifier attackDamageModifier = new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "one_with_the_wood_attack_damage"),
                bonusAttackDamage,
                AttributeModifier.Operation.ADD_VALUE
        );

        builder.add(Attributes.ATTACK_SPEED, attackSpeedModifier, EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_DAMAGE, attackDamageModifier, EquipmentSlotGroup.MAINHAND);

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private void modifyDurability(ItemStack stack, float devotion) {
        if (!(stack.getItem() instanceof TieredItem tieredItem)) return;

        int baseMaxDurability = tieredItem.getTier().getUses();
        int currentMaxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamageValue();

        int newMaxDurability = (int) (baseMaxDurability * Math.max(1f, devotion));

        AncientsDevotion.LOG.info("Current max: {}", currentMaxDamage);
        AncientsDevotion.LOG.info("Modified max: {}", newMaxDurability);

        if (newMaxDurability <= currentMaxDamage) return;

        float durabilityPercent = 1f - (currentDamage / (float) currentMaxDamage);
        int newDamageValue = (int) ((1f - durabilityPercent) * newMaxDurability);

        stack.set(DataComponents.MAX_DAMAGE, newMaxDurability);
        stack.set(DataComponents.DAMAGE, newDamageValue);
    }

    private void applyMetalHate(Player player, Item item, PlayerInteractEvent.LeftClickBlock event){
        if (item != Items.AIR) {

            if (IRON_OF_DIAMOND_ITEM.contains(item)) {
                event.setCanceled(true);

                ItemEntity itementity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), item.getDefaultInstance());
                itementity.setDefaultPickUpDelay();
                player.level().addFreshEntity(itementity);

                player.getInventory().removeItem(item.getDefaultInstance());
                player.getInventory().removeFromSelected(true);
            }
        }
    }

    //All creatures are passive creatures?
    private void applyGuardianCode(Player player, LivingEntity target) {
        if (target.getType().getCategory() == MobCategory.CREATURE) {
            DevotionHandler.decreaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), FIXED_GAIN_RATE * 10); //Create unique variable?
        }
    }

    private void increaseDevotion(Player player, BlockEvent.BreakEvent event) {
        BlockState state = event.getState();

        if (state.getBlock() instanceof CropBlock) {
            CropBlock cropBlock = (CropBlock) state.getBlock();
            int age = cropBlock.getAge(state);

            if (age == 7) {
                DevotionHandler.increaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), FIXED_GAIN_RATE);
            }
        }
    }

    private void increaseDevotionOther(Player player, Item item){
        if (VEGETAL_FOODS.contains(item)) {
            DevotionHandler.increaseDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player)), FIXED_GAIN_RATE);
        }
    }

    //from CropBlock idk why AT doesnt work
    protected static float getGrowthSpeed(BlockState blockState, BlockGetter p_52274_, BlockPos p_52275_) {
        Block p_52273_ = blockState.getBlock();
        float f = 1.0F;
        BlockPos blockpos = p_52275_.below();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                float f1 = 0.0F;
                BlockState blockstate = p_52274_.getBlockState(blockpos.offset(i, 0, j));
                net.neoforged.neoforge.common.util.TriState soilDecision = blockstate.canSustainPlant(p_52274_, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, blockState);
                if (soilDecision.isDefault() ? blockstate.getBlock() instanceof net.minecraft.world.level.block.FarmBlock : soilDecision.isTrue()) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(p_52274_, p_52275_.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = p_52275_.north();
        BlockPos blockpos2 = p_52275_.south();
        BlockPos blockpos3 = p_52275_.west();
        BlockPos blockpos4 = p_52275_.east();
        boolean flag = p_52274_.getBlockState(blockpos3).is(p_52273_) || p_52274_.getBlockState(blockpos4).is(p_52273_);
        boolean flag1 = p_52274_.getBlockState(blockpos1).is(p_52273_) || p_52274_.getBlockState(blockpos2).is(p_52273_);
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = p_52274_.getBlockState(blockpos3.north()).is(p_52273_)
                    || p_52274_.getBlockState(blockpos4.north()).is(p_52273_)
                    || p_52274_.getBlockState(blockpos4.south()).is(p_52273_)
                    || p_52274_.getBlockState(blockpos3.south()).is(p_52273_);
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public static void applyNaturalShelter(Player player) {
        if (player.level().getBiome(player.blockPosition()).is(BiomeTags.IS_FOREST)) {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor != null && armor.getModifier(NATURAL_SHELTER_MODIFIER_ID) == null) {
                AttributeModifier armorModifier = new AttributeModifier(
                        NATURAL_SHELTER_MODIFIER_ID,
                        4.0,
                        AttributeModifier.Operation.ADD_VALUE
                );
                armor.addTransientModifier(armorModifier);
            }
        } else {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor != null) {
                armor.removeModifier(NATURAL_SHELTER_MODIFIER_ID);
            }

        }
    }

    private void applyNaturalMimicry(Player player) {
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64))
                .forEach(mob -> {

                    AttributeInstance followRangeAttribute = mob.getAttribute(Attributes.FOLLOW_RANGE);

                    AttributeModifier attributeModifierMin = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "natural_mimicry_follow_range_max"),
                            -followRangeAttribute.getBaseValue() / 2,
                            AttributeModifier.Operation.ADD_VALUE
                    );


                    AttributeModifier attributeModifierMax = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "natural_mimicry_follow_range_min"),
                            -followRangeAttribute.getBaseValue() * 0.75,
                            AttributeModifier.Operation.ADD_VALUE
                    );

                    if (player.isShiftKeyDown()) {

                        EntityUtils.removeAttributeModifier(followRangeAttribute, attributeModifierMin);

                        EntityUtils.applyAttributeModifier(followRangeAttribute, attributeModifierMax);

                    } else {

                        EntityUtils.removeAttributeModifier(followRangeAttribute, attributeModifierMax);

                        EntityUtils.applyAttributeModifier(followRangeAttribute, attributeModifierMin);
                    }
                });
    }

    //Todo Maybe make bees not dying from this :c
    private void applySpiritAlly(Player player, LivingIncomingDamageEvent event) {
        if (event.getAmount() > 0) {
            player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(32))
                    .forEach(entity -> {
                        if (entity instanceof Wolf wolf && !wolf.isTame()) {
                            wolf.setTarget((LivingEntity) event.getSource().getEntity());
                        } else if (entity instanceof Fox fox) {
                            fox.setTarget((LivingEntity) event.getSource().getEntity());
                        } else if (entity instanceof Bee bee) {
                            bee.setTarget((LivingEntity) event.getSource().getEntity());
                        }
                    });
        }
    }

    private void applySilentGrowth(Player player) {
        BlockPos playerPos = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-3, -1, -3), playerPos.offset(3, 1, 3))) {
            if (player.level().getBlockState(pos).getBlock() instanceof CropBlock crop) {
                randomTick(player.level().getBlockState(pos), (ServerLevel) player.level(), pos, player.level().random, crop);
            }
        }
    }

    //from CropBlock idk why AT doesnt work
    //Todo trees and everything that can grow?
    //todo nerf
    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CropBlock cropBlock) {
        if (!serverLevel.isAreaLoaded(blockPos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            int i = cropBlock.getAge(blockState);
            if (i < cropBlock.getMaxAge()) {
                float f = getGrowthSpeed(blockState, serverLevel, blockPos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(serverLevel, blockPos, blockState, randomSource.nextInt((int) (25.0F / f) + 1) == 0)) {
                    serverLevel.setBlock(blockPos, cropBlock.getStateForAge(i + 1), 2);
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(serverLevel, blockPos, blockState);
                }
            }
        }
    }

    private void applyGreenResonance(Player player, BlockEvent.BreakEvent event) {
        if (event.getState().is(BlockTags.LOGS)) {
            chopTree(player, event.getPos());
            if (player.level().random.nextFloat() < 0.25f) { // 25% de probabilidad de madera extra
                ItemEntity extraWood = new ItemEntity(player.level(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(event.getState().getBlock().asItem()));
                player.level().addFreshEntity(extraWood);
            }
        }
    }

    private void chopTree(Player player, BlockPos pos) {
        Queue<BlockPos> blocksToBreak = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        blocksToBreak.add(pos);

        while (!blocksToBreak.isEmpty() && visited.size() < 64) {
            BlockPos currentPos = blocksToBreak.poll();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            if (player.level().getBlockState(currentPos).is(BlockTags.LOGS) || player.level().getBlockState(currentPos).is(BlockTags.LEAVES)) {
                player.level().destroyBlock(currentPos, true);
                for (Direction direction : Direction.values()) {
                    BlockPos newPos = currentPos.relative(direction);
                    if (!visited.contains(newPos)) {
                        blocksToBreak.add(newPos);
                    }
                }
            }
        }
    }


}
