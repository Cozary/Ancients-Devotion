package com.cozary.ancients_devotion.init;

import com.cozary.ancients_devotion.gods.Patigeo;
import com.cozary.ancients_devotion.gods.Silvaeria;
import com.cozary.ancients_devotion.gods.Soltitia;
import com.cozary.ancients_devotion.gods.core.God;

import java.util.HashMap;
import java.util.Map;

public class GodRegistry {

    public static final Map<String, God> GODS = new HashMap<>();

    public static void registerGods() {
        God soltitia = new God("Soltitia", ModAttachmentTypes.SOLTITIA_DEVOTION.get(), 100.0f, new Soltitia());
        God silvaeria = new God("Silvaeria", ModAttachmentTypes.SILVAERIA_DEVOTION.get(), 100.0f, new Silvaeria());
        God patigeo = new God("Patigeo", ModAttachmentTypes.PATIGEO_DEVOTION.get(), 100.0f, new Patigeo());

        GODS.put("soltitia", soltitia);
        GODS.put("silvaeria", silvaeria);
        GODS.put("patigeo", patigeo);
    }
}
