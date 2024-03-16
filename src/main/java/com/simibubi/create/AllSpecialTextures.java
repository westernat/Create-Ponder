package com.simibubi.create;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public enum AllSpecialTextures {
    BLANK("blank.png");

    public static final String ASSET_PATH = "textures/special/";
    private final ResourceLocation location;

    AllSpecialTextures(String filename) {
        location = Create.asResource(ASSET_PATH + filename);
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    public ResourceLocation getLocation() {
        return location;
    }
}