package com.simibubi.create.foundation.mixin.fabric;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(BlockableEventLoop.class)
public interface BlockableEventLoopAccessor {
	@Invoker
	CompletableFuture<Void> callSubmitAsync(Runnable task);
}
