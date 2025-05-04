package com.cozary.ancients_devotion.util;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.gods.core.God;
import com.cozary.ancients_devotion.init.GodRegistry;
import com.cozary.ancients_devotion.network.data.GodData;
import com.cozary.ancients_devotion.network.data.PatigeoDevotionData;
import com.cozary.ancients_devotion.network.data.SilvaeriaDevotionData;
import com.cozary.ancients_devotion.network.data.SoltitiaDevotionData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cozary.ancients_devotion.init.ModAttachmentTypes.CURRENT_GOD;

public class DevotionHandler {

    public static float getDevotion(Player player, God god) {
        return player.getData(god.getDevotionType());
    }

    public static void setDevotion(Player player, God god, float value) {
        value = Math.min(value, god.getMaxDevotion());
        player.setData(god.getDevotionType(), value);

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        switch (god.getName().toLowerCase()) {
            case "soltitia" -> PacketDistributor.sendToPlayer(serverPlayer, new SoltitiaDevotionData(value));
            case "silvaeria" -> PacketDistributor.sendToPlayer(serverPlayer, new SilvaeriaDevotionData(value));
            case "patigeo" -> PacketDistributor.sendToPlayer(serverPlayer, new PatigeoDevotionData(value));
            default -> AncientsDevotion.LOG.warn("God is dead: " + god.getName());
        }
    }


    public static void increaseDevotion(Player player, God god, float amount) {
        float current = getDevotion(player, god);
        setDevotion(player, god, current + amount);
    }

    public static void decreaseDevotion(Player player, God god, float amount) {
        float current = getDevotion(player, god);
        setDevotion(player, god, Math.max(0.0f, current - amount));
    }

    public static boolean hasDevotion(Player player, God god, float minimum) {
        return getDevotion(player, god) >= minimum;
    }

    public static String getCurrentGod(Player player) {
        return player.getData(CURRENT_GOD);
    }

    public static void setCurrentGod(Player player, String godName) {
        player.setData(CURRENT_GOD, godName);
    }

    public static God getGod(String name) {
        return GodRegistry.GODS.get(name.toLowerCase());
    }

}


