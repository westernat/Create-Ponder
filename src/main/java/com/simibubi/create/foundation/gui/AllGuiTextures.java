package com.simibubi.create.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum AllGuiTextures implements ScreenElement {
    LOGO("logo", 256, 256),
    CURSEFORGE_LOGO("platform_icons/curseforge", 256, 256),
    MODRINTH_LOGO("platform_icons/modrinth", 256, 256),
    SPEECH_TOOLTIP_BACKGROUND("widgets", 0, 24, 8, 8),
    SPEECH_TOOLTIP_COLOR("widgets", 8, 24, 8, 8);

    public final ResourceLocation location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    AllGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    AllGuiTextures(String location, int startX, int startY, int width, int height) {
        this(Create.ID, location, startX, startY, width, height);
    }

    AllGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
