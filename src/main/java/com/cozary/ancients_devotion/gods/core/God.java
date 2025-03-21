package com.cozary.ancients_devotion.gods.core;


import net.neoforged.neoforge.attachment.AttachmentType;

public class God {
    private final String name;
    private final AttachmentType<Float> devotionType;
    private final float maxDevotion;
    private final GodBehaviour behavior;

    public God(String name, AttachmentType<Float> devotionType, float maxDevotion, GodBehaviour behavior) {
        this.name = name;
        this.devotionType = devotionType;
        this.maxDevotion = maxDevotion;
        this.behavior = behavior;
    }

    public String getName() {
        return name;
    }

    public AttachmentType<Float> getDevotionType() {
        return devotionType;
    }

    public float getMaxDevotion() {
        return maxDevotion;
    }

    public GodBehaviour getBehavior() {
        return behavior;
    }

    //Testing
    @Override
    public String toString() {
        return "God{name='" + name + "', devotionType=" + devotionType;
    }

}

