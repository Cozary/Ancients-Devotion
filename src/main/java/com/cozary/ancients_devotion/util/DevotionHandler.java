package com.cozary.ancients_devotion.util;

import com.cozary.ancients_devotion.gods.core.God;
import com.cozary.ancients_devotion.init.GodRegistry;
import net.minecraft.world.entity.player.Player;

import static com.cozary.ancients_devotion.init.ModAttachmentTypes.CURRENT_GOD;

public class DevotionHandler {

    public static float getDevotion(Player player, God god) {
        return player.getData(god.getDevotionType());
    }

    public static void setDevotion(Player player, God god, float value) {
        player.setData(god.getDevotionType(), Math.min(value, god.getMaxDevotion()));
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


