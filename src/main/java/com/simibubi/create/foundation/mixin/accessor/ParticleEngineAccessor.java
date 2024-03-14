package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    // This field cannot be ATed because its type is patched by Forge
    @Accessor("providers")
    Map<ResourceLocation, ParticleProvider<?>> create$getProviders();
}
