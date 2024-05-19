package com.simibubi.create.foundation.block.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MultiPosDestructionHandler {
	/**
	 * Returned set must be mutable and must not be changed after it is returned.
	 */
	@Nullable
	@Environment(EnvType.CLIENT)
	Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress);
}
