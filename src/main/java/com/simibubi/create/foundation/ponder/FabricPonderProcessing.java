package com.simibubi.create.foundation.ponder;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Processing for Ponder schematics to allow using the same ones on Forge and Fabric.
 */
public class FabricPonderProcessing {
	public static final Codec<Processor> PROCESSOR_CODEC = ResourceLocation.CODEC
			.fieldOf("structureId")
			.xmap(Processor::new, processor -> processor.structureId)
			.codec();

	public static final StructureProcessorType<Processor> PROCESSOR_TYPE = Registry.register(
			BuiltInRegistries.STRUCTURE_PROCESSOR,
			Create.asResource("fabric_ponder_processor"),
			() -> PROCESSOR_CODEC
	);

	/**
	 * A predicate that makes all processes apply to all schematics.
	 */
	public static final ProcessingPredicate ALWAYS = (id, process) -> true;

	private static final Map<String, ProcessingPredicate> predicates = new HashMap<>();

	/**
	 * Register a {@link ProcessingPredicate} for a mod.
	 * Only one predicate may be registered for each mod.
	 * The predicate determines which {@link Process}es will be applied to which schematics.
	 */
	public static ProcessingPredicate register(String modId, ProcessingPredicate predicate) {
		ProcessingPredicate existing = predicates.get(modId);
		if (existing != null) {
			throw new IllegalStateException(
					"Tried to register ProcessingPredicate [%s] for mod '%s', while one already exists: [%s]"
							.formatted(predicate, modId, existing)
			);
		}
	    predicates.put(modId, predicate);
		return predicate;
	}

	public static StructurePlaceSettings makePlaceSettings(ResourceLocation structureId) {
		return new StructurePlaceSettings().addProcessor(new Processor(structureId));
	}

	@Internal
	public static void init() {
		register(Create.ID, ALWAYS);
	}

	public enum Process {
		FLUID_TANK_AMOUNTS
	}

	@FunctionalInterface
	public interface ProcessingPredicate {
		boolean shouldApplyProcess(ResourceLocation schematicId, Process process);
	}

	public static class Processor extends StructureProcessor {
		public final ResourceLocation structureId;

		public Processor(ResourceLocation structureId) {
			this.structureId = structureId;
		}

		@Nullable
		@Override
		public StructureTemplate.StructureBlockInfo processBlock(
				@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockPos pivot,
				@NotNull StructureBlockInfo blockInfo, @NotNull StructureBlockInfo relativeBlockInfo,
				@NotNull StructurePlaceSettings settings) {
			// no processes were applied
			return relativeBlockInfo;
		}

		@Override
		@NotNull
		protected StructureProcessorType<?> getType() {
			return PROCESSOR_TYPE;
		}
	}
}
