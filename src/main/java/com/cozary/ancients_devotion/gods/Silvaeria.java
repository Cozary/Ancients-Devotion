package com.cozary.ancients_devotion.gods;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.AbstractGodBehavior;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    private boolean isIronOrDiamondItem(ItemStack item) {
        Item itemItem = item.getItem();
        return itemItem == Items.IRON_SWORD ||
                itemItem == Items.IRON_PICKAXE ||
                itemItem == Items.IRON_AXE ||
                itemItem == Items.IRON_SHOVEL ||
                itemItem == Items.IRON_HOE ||
                itemItem == Items.IRON_HELMET ||
                itemItem == Items.IRON_CHESTPLATE ||
                itemItem == Items.IRON_LEGGINGS ||
                itemItem == Items.IRON_BOOTS ||
                itemItem == Items.DIAMOND_SWORD ||
                itemItem == Items.DIAMOND_PICKAXE ||
                itemItem == Items.DIAMOND_AXE ||
                itemItem == Items.DIAMOND_SHOVEL ||
                itemItem == Items.DIAMOND_HOE ||
                itemItem == Items.DIAMOND_HELMET ||
                itemItem == Items.DIAMOND_CHESTPLATE ||
                itemItem == Items.DIAMOND_LEGGINGS ||
                itemItem == Items.DIAMOND_BOOTS ||
                itemItem == Items.NETHERITE_SWORD ||
                itemItem == Items.NETHERITE_PICKAXE ||
                itemItem == Items.NETHERITE_AXE ||
                itemItem == Items.NETHERITE_SHOVEL ||
                itemItem == Items.NETHERITE_HOE ||
                itemItem == Items.NETHERITE_HELMET ||
                itemItem == Items.NETHERITE_CHESTPLATE ||
                itemItem == Items.NETHERITE_LEGGINGS ||
                itemItem == Items.NETHERITE_BOOTS;
    }

    @Override
    public void onTick(Player player) {
        super.onTick(player);
        applyNaturalMimicry(player);
        applySilentGrowth(player);
        applyNaturalShelter(player);
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

    private void applyMetalHate(Player player, Item item, PlayerInteractEvent.LeftClickBlock event){
        if (item != Items.AIR) {

            if (isIronOrDiamondItem(item.getDefaultInstance())) {
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

    //Doesnt reset
    private void applyNaturalMimicry(Player player) {
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64))
                .forEach(mob -> {
                    if (player.isShiftKeyDown()) {
                        Objects.requireNonNull(mob.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attributes.FOLLOW_RANGE)).getBaseValue() / 2);
                    } else {
                        Objects.requireNonNull(mob.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attributes.FOLLOW_RANGE)).getBaseValue());
                    }
                });
    }

    //Maybe make bees not dying from this :c
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

        while (!blocksToBreak.isEmpty()) {
            BlockPos currentPos = blocksToBreak.poll();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            if (player.level().getBlockState(currentPos).is(BlockTags.LOGS)) {
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
