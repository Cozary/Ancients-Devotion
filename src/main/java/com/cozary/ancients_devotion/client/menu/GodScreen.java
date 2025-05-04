package com.cozary.ancients_devotion.client.menu;

import com.cozary.ancients_devotion.AncientsDevotion;
import com.cozary.ancients_devotion.util.DevotionHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static com.cozary.ancients_devotion.util.DevotionHandler.*;

public class GodScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AncientsDevotion.MOD_ID, "textures/gui/god_menu.png");
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private int leftPos;
    private int topPos;

    private final String godName;
    private final float devotion;

    public GodScreen(String godName, float devotion) {
        super(Component.literal("God Info"));
        this.godName = godName;
        this.devotion = devotion;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        poseStack.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (godName == null || getGod(godName) == null) {
            AncientsDevotion.LOG.warn("El dios con nombre '{}' no se encontró.", godName);
        } else {
            poseStack.drawString(minecraft.font, "God: " + godName, leftPos + 10, topPos + 20, 0xFFFFFF);
            poseStack.drawString(minecraft.font, "Devotion: " + devotion, leftPos + 10, topPos + 40, 0xFFFFFF);
        }
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
