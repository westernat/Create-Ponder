package com.simibubi.create.foundation.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
	// This field cannot be ATed because its type is patched by Forge
	@Accessor("providers")
	Int2ObjectMap<ParticleProvider<?>> create$getProviders();
}
