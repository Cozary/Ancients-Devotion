package com.cozary.ancients_devotion.gods;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.AbstractGodBehavior;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class Patigeo extends AbstractGodBehavior {

    public static final double MIN_DISTANCE = 4.0;
    public static final double MAX_DISTANCE = 8.0;
    public static final int SEARCH_RANGE = 10;
    private static final ResourceLocation MINING_REFLEXES_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "mining_reflexes_block_break_speed_modifier");
    private static final ResourceLocation TELLURIC_REACH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "telluric_reach_block_interaction_range_modifier");

    @Override
    public void onTick(Player player) {
        applyVoiceOfTheEarth(player);
        applyGeologicalRegeneration(player);
        applyMiningReflexes(player);
        applyTelluricReach(player);
    }

    @Override
    public void onAttackPlayer(Player player, Entity target, LivingIncomingDamageEvent event) {
        applyStoneShell(player, event);
    }

    @Override
    public void onPlayerBreakBlock(Player player, BlockEvent.BreakEvent event) {
        applyEchoOfTheDepths(player, event);
    }

    @Override
    public void onAttack(Player player, LivingEntity target, LivingIncomingDamageEvent event) {
        applyMinerArms(player, event);
        trySismo(player, target);
    }

    private static boolean isOre(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.COAL_ORE || block == Blocks.IRON_ORE || block == Blocks.COPPER_ORE ||
                block == Blocks.GOLD_ORE || block == Blocks.REDSTONE_ORE || block == Blocks.DIAMOND_ORE ||
                block == Blocks.EMERALD_ORE || block == Blocks.NETHER_QUARTZ_ORE || block == Blocks.ANCIENT_DEBRIS;
    }

    public static void applyStoneShell(Player player, LivingIncomingDamageEvent event) {
        if (isUndergroundBiome(player)) {
            float reduced = 10; //Percentage
            event.setAmount(event.getAmount() * (reduced / 100));
        }
    }

    //Make this a general check to other methods but not every method
    public static boolean isUndergroundBiome(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        return !(player.getY() > 60);

        //There isnt a way to detect to be in a cave.
       /* ResourceKey<Biome> biomeKey = level.getBiome(pos).unwrapKey().orElse(null);
        if (biomeKey != null) {
            String biomeName = biomeKey.location().toString();

            if (biomeName.contains("dripstone_caves") ||
                    biomeName.contains("lush_caves") ||
                    biomeName.contains("deep_dark") ||
                    biomeName.contains("stalactite_caves") ||
                    biomeName.contains("cave")) {
                return true;
            }

            if (biomeName.contains("nether") || biomeName.contains("the_end")) {
                return true;
            }
        }*/
    }

    //Make percentage
    public static void applyEchoOfTheDepths(Player player, BlockEvent.BreakEvent event) {
        if (isOre(event.getState())) {
            Level level = player.level();
            ItemStack itemStack = player.getMainHandItem();
            ItemStack fakeItemStack = new ItemStack(itemStack.getItem());
            ItemEnchantments enchantments = itemStack.get(DataComponents.ENCHANTMENTS);
            HolderLookup.Provider registries = level.registryAccess();
            HolderLookup<Enchantment> enchantmentRegistry = registries.lookupOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> silkTouch = enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH);
            Holder<Enchantment> fortune = enchantmentRegistry.getOrThrow(Enchantments.FORTUNE);
            if (enchantments != null && enchantments.getLevel(silkTouch) > 0) {
                return;
            }
            int bonusLevel = enchantments != null ? enchantments.getLevel(fortune) : 0;
            FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerLevel) level);
            fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, fakeItemStack);
            ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(enchantments != null ? enchantments : ItemEnchantments.EMPTY);
            mutableEnchantments.upgrade(fortune, bonusLevel);
            fakeItemStack.set(DataComponents.ENCHANTMENTS, mutableEnchantments.toImmutable());
            if (!(event.getState().getBlock() instanceof EntityBlock)) {
                LootTable loot = level.getServer().reloadableRegistries().getLootTable(event.getState().getBlock().getLootTable());
                LootParams context = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(event.getPos()))
                        .withParameter(LootContextParams.TOOL, fakeItemStack)
                        .withParameter(LootContextParams.BLOCK_STATE, event.getState())
                        .create(LootContextParamSets.BLOCK);
                List<ItemStack> drops = loot.getRandomItems(context);
                /*if (!drops.isEmpty()) {
                    drops.get(0).setCount(drops.get(0).getCount() - 1);
                }*/
                for (ItemStack drop : drops) {
                    ItemEntity itemEntity = new ItemEntity(level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), drop);
                    itemEntity.setDefaultPickUpDelay();
                    itemEntity.setPos(Vec3.atCenterOf(event.getPos()));
                    itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((level.random.nextFloat() - level.random.nextFloat()) * 0.1F, level.random.nextFloat() * 0.05F, (level.random.nextFloat() - level.random.nextFloat()) * 0.1F));
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

    public static void applyMinerArms(Player player, LivingIncomingDamageEvent event) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof PickaxeItem) {
            event.setAmount(event.getAmount() * 50);
        }
    }

    //Todo move block upwards
    //Todo make the entity to take damage when hit by the "shock wave."
    public static void trySismo(Player player, LivingEntity target) {
        Level level = player.level();
        float chance = 1;
        float launchPower = 0.5F;
        int blocksLaunched = 10;

        if (level.random.nextFloat() < chance) {
            BlockPos basePos = target.blockPosition();
            strongKnockback(target, player);

            Vec3 direction = target.getDeltaMovement().normalize();

            if (direction.lengthSqr() == 0)
                direction = player.getLookAngle().normalize();

            Vec3 horizontalDirection = new Vec3(direction.x, 0, direction.z).normalize();

            for (int i = 1; i <= blocksLaunched; i++) {
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                        basePos.getX() + (int) (horizontalDirection.x * i),
                        basePos.getY(),
                        basePos.getZ() + (int) (horizontalDirection.z * i)
                );

                while (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight()) {
                    pos.move(Direction.DOWN);
                }

                BlockState state = level.getBlockState(pos);

                if (state.isAir() || state.getBlock() == Blocks.BEDROCK) continue;

                level.removeBlock(pos, false);

                FallingBlockEntity fallingBlock = new FallingBlockEntity(
                        level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state
                );

                fallingBlock.setDeltaMovement(
                        horizontalDirection.x * launchPower,
                        launchPower,
                        horizontalDirection.z * launchPower
                );

                level.addFreshEntity(fallingBlock);
            }

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.CLOUD, target.getX(), target.getY(), target.getZ(),
                        20, 1, 0.5, 1, 0.1
                );
            }
        }
    }

    //Copy from Ravager
    private static void strongKnockback(Entity entity, Player player) {
        double d0 = entity.getX() - player.getX();
        double d1 = entity.getZ() - player.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        entity.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
    }

    //Check pickaxe
    public static void applyMiningReflexes(Player player) {
        if (player.getMainHandItem().getItem() instanceof PickaxeItem) {
            AttributeInstance blockBreakSpeed = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
            if (blockBreakSpeed != null && blockBreakSpeed.getModifier(MINING_REFLEXES_MODIFIER_ID) == null) {
                AttributeModifier blockBreakSpeedModifier = new AttributeModifier(
                        MINING_REFLEXES_MODIFIER_ID,
                        20.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );
                blockBreakSpeed.addTransientModifier(blockBreakSpeedModifier);
            }
        } else {
            AttributeInstance blockBreakSpeed = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
            if (blockBreakSpeed != null) {
                blockBreakSpeed.removeModifier(MINING_REFLEXES_MODIFIER_ID);
            }

        }
    }

    //Check pickaxe
    public static void applyTelluricReach(Player player) {
        if (player.getMainHandItem().getItem() instanceof PickaxeItem) {
            AttributeInstance blockInteractionRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
            if (blockInteractionRange != null && blockInteractionRange.getModifier(TELLURIC_REACH_MODIFIER_ID) == null) {
                AttributeModifier reachBlockInteractionRangeModifier = new AttributeModifier(
                        TELLURIC_REACH_MODIFIER_ID,
                        20.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );
                blockInteractionRange.addTransientModifier(reachBlockInteractionRangeModifier);
            }
        } else {
            AttributeInstance blockInteractionRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
            if (blockInteractionRange != null) {
                blockInteractionRange.removeModifier(TELLURIC_REACH_MODIFIER_ID);
            }

        }
    }

    private void applyVoiceOfTheEarth(Player player) {
        Level level = player.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-SEARCH_RANGE, -SEARCH_RANGE, -SEARCH_RANGE),
                playerPos.offset(SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE))) {
            BlockState state = level.getBlockState(pos);
            if (!isOre(state)) continue;

            double distance = player.position().distanceTo(Vec3.atCenterOf(pos));

            if (distance >= MIN_DISTANCE && distance <= MAX_DISTANCE) {
                // Within range
                if (!hasShulkerAtPos(serverLevel, pos)) {
                    spawnGlowingShulker(serverLevel, pos, getColorForOre(state));
                }
            } else {
                // Out of range
                removeShulkerAtPos(serverLevel, pos);
            }
        }
    }

    private static void spawnGlowingShulker(ServerLevel level, BlockPos pos, ChatFormatting color) {
        Shulker shulker = new Shulker(EntityType.SHULKER, level);
        shulker.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        shulker.setInvisible(true);
        shulker.setNoAi(true);
        shulker.setInvulnerable(true);
        shulker.setSilent(true);
        shulker.addTag("voice_of_the_earth_shulker");

        // Assign team
        String teamName = "voice_earth_" + color.getName();
        assignShulkerToTeam(level, shulker, teamName, color);

        level.addFreshEntity(shulker);
    }

    private static void assignShulkerToTeam(ServerLevel level, Shulker shulker, String teamName, ChatFormatting color) {
        Scoreboard scoreboard = level.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
            team.setColor(color);
            team.setSeeFriendlyInvisibles(false);
            team.setAllowFriendlyFire(false);
        }
        scoreboard.addPlayerToTeam(shulker.getStringUUID(), team);
        shulker.setGlowingTag(true);
    }

    private static void removeShulkerAtPos(ServerLevel level, BlockPos pos) {
        level.getEntitiesOfClass(Shulker.class, new AABB(pos).inflate(0.5),
                shulker -> shulker.getTags().contains("voice_of_the_earth_shulker")).forEach(shulker -> shulker.remove(Entity.RemovalReason.DISCARDED));
    }

    private static boolean hasShulkerAtPos(ServerLevel level, BlockPos pos) {
        return !level.getEntitiesOfClass(Shulker.class, new AABB(pos).inflate(0.5),
                shulker -> shulker.getTags().contains("voice_of_the_earth_shulker")).isEmpty();
    }

    private static ChatFormatting getColorForOre(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.COAL_ORE) return ChatFormatting.DARK_GRAY;
        if (block == Blocks.IRON_ORE) return ChatFormatting.GRAY;
        if (block == Blocks.COPPER_ORE) return ChatFormatting.GOLD;
        if (block == Blocks.GOLD_ORE) return ChatFormatting.YELLOW;
        if (block == Blocks.REDSTONE_ORE) return ChatFormatting.RED;
        if (block == Blocks.DIAMOND_ORE) return ChatFormatting.AQUA;
        if (block == Blocks.EMERALD_ORE) return ChatFormatting.GREEN;
        if (block == Blocks.NETHER_QUARTZ_ORE) return ChatFormatting.WHITE;
        if (block == Blocks.ANCIENT_DEBRIS) return ChatFormatting.DARK_RED;
        return ChatFormatting.LIGHT_PURPLE;
    }

    //Copy from Soltitia
    private void applyGeologicalRegeneration(Player player) {
        float healRegen = Math.min(0.5f, Math.max(0.05f, (DevotionHandler.getDevotion(player, DevotionHandler.getGod(DevotionHandler.getCurrentGod(player))) * 0.005f))); //Lv100 max value

        if (player.getHealth() < player.getMaxHealth()) {
            player.heal((float) (double) healRegen);
        }
    }
}