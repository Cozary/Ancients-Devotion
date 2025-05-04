package com.cozary.ancients_devotion.events;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.init.ModItems;
import com.cozary.ancients_devotion.network.data.GodData;
import com.cozary.ancients_devotion.network.data.SilvaeriaCropsCountData;
import com.cozary.ancients_devotion.network.data.SoltitiaDevotionData;
import com.cozary.ancients_devotion.util.DevotionHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

import static com.cozary.ancients_devotion.gods.Soltitia.isInSunLight;
import static com.cozary.ancients_devotion.init.ModAttachmentTypes.SILVAERIA_CROPS_COUNT;

@EventBusSubscriber(modid = AncientsDevotion.MOD_ID)
public class GodSetterEvents {
    private static int lookingAtSunTicks = 0;
    private static final int startDevotionTicks = 20 * 60;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        isPlayerLookingAtSun(player);
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;

        havePlayerHarvest(player, event.getState(), event.getState().getBlock());
        havePlayerBreakDiamond(player, event.getState(), event.getState().getBlock());
    }

    //Soltitia
    public static void isPlayerLookingAtSun(Player player) {
        Level level = player.level();

        if (!isInSunLight(player) || !(player.getInventory().countItem(ModItems.MEDALLION.get()) > 0)) { //check if have medallion in inventory
            return;
        }

        float sunAngle = level.getSunAngle(player.tickCount) + (float) Math.PI / 2; //-pi/2 moon
        Vec3 view = player.getViewVector(1.0f).normalize();
        Vec3 sun = new Vec3(Math.cos(sunAngle), Math.sin(sunAngle), 0f).normalize();

        double dot = view.dot(sun);
        double threshold = 0.995;
        boolean lookingAtSun = dot >= threshold;

        if(lookingAtSun){
            lookingAtSunTicks += 1;
            AncientsDevotion.LOG.info(String.valueOf(lookingAtSunTicks));
            if(lookingAtSunTicks >= startDevotionTicks){
                DevotionHandler.setCurrentGod(player, "Soltitia");
                PacketDistributor.sendToPlayer((ServerPlayer) player, new GodData("Soltitia"));

            }
        }
        else {
            lookingAtSunTicks = 0;
        }

        String msg = String.format(Locale.ROOT,
                "SunAngle: %.3f | View: (%.3f, %.3f, %.3f) | SunDir: (%.3f, %.3f, %.3f) | Dot: %.3f | LookingAtSun: %s",
                sunAngle, view.x, view.y, view.z, sun.x, sun.y, sun.z, dot, lookingAtSun);

        //AncientsDevotion.LOG.info(msg);
    }


    //Silvaeria
    public static void havePlayerHarvest(Player player, BlockState blockState, Block block) {
        Level level = player.level();

        if (!(player.getInventory().countItem(ModItems.MEDALLION.get()) > 0)) { //check if have medallion in inventory
            return;
        }

        if (block instanceof CropBlock cropBlock) {
            if (!level.isClientSide && cropBlock.isMaxAge(blockState)) {
                int count = player.getData(SILVAERIA_CROPS_COUNT);
                count++;
                player.setData(SILVAERIA_CROPS_COUNT, count);

                PacketDistributor.sendToPlayer((ServerPlayer) player, new SilvaeriaCropsCountData(count));

                if (count >= 64) {
                    DevotionHandler.setCurrentGod(player, "Silvaeria");
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new GodData("Silvaeria"));
                }
            }
        }

    }

    //Patigeo
    public static void havePlayerBreakDiamond(Player player, BlockState state, Block block) {
        Level level = player.level();

        if (!(player.getInventory().countItem(ModItems.MEDALLION.get()) > 0)) { //check if have medallion in inventory
            return;
        }

        if (block == Blocks.DIAMOND_ORE && player.getMainHandItem().is(Items.AIR)) {
            DevotionHandler.setCurrentGod(player, "Patigeo");
            PacketDistributor.sendToPlayer((ServerPlayer) player, new GodData("Patigeo"));
        }

    }

}
