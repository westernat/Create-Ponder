package com.simibubi.create.content.schematics;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllStructureProcessorTypes;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class SchematicProcessor extends StructureProcessor {
	public static final SchematicProcessor INSTANCE = new SchematicProcessor();
	public static final Codec<SchematicProcessor> CODEC = Codec.unit(() -> INSTANCE);

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockPos anotherPos, StructureTemplate.@NotNull StructureBlockInfo rawInfo, StructureTemplate.StructureBlockInfo info, @NotNull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
		if (info.nbt() != null && info.state().hasBlockEntity()) {
			BlockEntity be = ((EntityBlock) info.state().getBlock()).newBlockEntity(info.pos(), info.state());
			if (be != null) {
				CompoundTag nbt = NBTProcessors.process(be, info.nbt(), false);
				if (nbt != info.nbt()) {
					return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), nbt);
				}
			}
		}
		return info;
	}

	@Nullable
	@Override
	public StructureTemplate.StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureEntityInfo rawInfo, StructureTemplate.StructureEntityInfo info, @NotNull StructurePlaceSettings settings, @NotNull StructureTemplate template) {
		return EntityType.by(info.nbt).flatMap(type -> {
			if (world instanceof Level) {
				Entity e = type.create((Level) world);
				if (e != null && !e.onlyOpCanSetNbt()) {
					return Optional.of(info);
				}
			}
			return Optional.empty();
		}).orElse(null);
	}

	@Override
	protected @NotNull StructureProcessorType<?> getType() {
		return AllStructureProcessorTypes.SCHEMATIC.get();
	}
}
