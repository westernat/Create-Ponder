package com.simibubi.create.content.schematics;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.BBHelper;
import com.simibubi.create.foundation.utility.NBTProcessors;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SchematicWorld extends WrappedWorld implements ServerLevelAccessor {

    protected Map<BlockPos, BlockState> blocks;
    protected Map<BlockPos, BlockEntity> blockEntities;
    protected List<BlockEntity> renderedBlockEntities;
    protected List<Entity> entities;
    protected BoundingBox bounds;

    public BlockPos anchor;
    public boolean renderMode;

    public SchematicWorld(Level original) {
        this(BlockPos.ZERO, original);
    }

    public SchematicWorld(BlockPos anchor, Level original) {
        super(original);
        setChunkSource(new SchematicChunkSource(this));
        this.blocks = new HashMap<>();
        this.blockEntities = new HashMap<>();
        this.bounds = new BoundingBox(BlockPos.ZERO);
        this.anchor = anchor;
        this.entities = new ArrayList<>();
        this.renderedBlockEntities = new ArrayList<>();
    }

    public Set<BlockPos> getAllPositions() {
        return blocks.keySet();
    }

    @Override
    public boolean addFreshEntity(@NotNull Entity entityIn) {
        if (entityIn instanceof ItemFrame itemFrame) {
            itemFrame.setItem(NBTProcessors.withUnsafeNBTDiscarded(itemFrame.getItem()));
        }
        if (entityIn instanceof ArmorStand armorStand) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                armorStand.setItemSlot(equipmentSlot, NBTProcessors.withUnsafeNBTDiscarded(armorStand.getItemBySlot(equipmentSlot)));
            }
        }
        return entities.add(entityIn);
    }

    public Stream<Entity> getEntityStream() {
        return entities.stream();
    }

    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        if (isOutsideBuildHeight(pos)) {
            return null;
        }
        if (blockEntities.containsKey(pos)) {
            return blockEntities.get(pos);
        }
        if (!blocks.containsKey(pos.subtract(anchor))) {
            return null;
        }

        BlockState blockState = getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            try {
                BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(pos, blockState);
                if (blockEntity != null) {
                    onBEadded(blockEntity, pos);
                    blockEntities.put(pos, blockEntity);
                    renderedBlockEntities.add(blockEntity);
                }
                return blockEntity;
            } catch (Exception e) {
                Create.LOGGER.debug("Could not create BlockEntity of block " + blockState, e);
            }
        }
        return null;
    }

    protected void onBEadded(BlockEntity blockEntity, BlockPos pos) {
        blockEntity.setLevel(this);
    }

    @Override
    public @NotNull BlockState getBlockState(BlockPos globalPos) {
        BlockPos pos = globalPos.subtract(anchor);

        if (pos.getY() - bounds.minY() == -1 && !renderMode) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (getBounds().isInside(pos) && blocks.containsKey(pos)) {
            return processBlockStateForPrinting(blocks.get(pos));
        }
        return Blocks.AIR.defaultBlockState();
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blocks;
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Override
    public @NotNull Holder<Biome> getBiome(@NotNull BlockPos pos) {
        return world.registryAccess()
            .lookupOrThrow(Registries.BIOME)
            .getOrThrow(Biomes.PLAINS);
    }

    @Override
    public int getBrightness(@NotNull LightLayer lightLayer, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public float getShade(@NotNull Direction face, boolean hasShade) {
        return 1f;
    }

    @Override
    public @NotNull LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public @NotNull LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public @NotNull List<Entity> getEntities(Entity arg0, @NotNull AABB arg1, @NotNull Predicate<? super Entity> arg2) {
        return Collections.emptyList();
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntitiesOfClass(@NotNull Class<T> arg0, @NotNull AABB arg1, @NotNull Predicate<? super T> arg2) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<? extends Player> players() {
        return Collections.emptyList();
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public boolean isStateAtPosition(@NotNull BlockPos pos, @NotNull Predicate<BlockState> predicate) {
        return predicate.test(getBlockState(pos));
    }

    @Override
    public boolean destroyBlock(@NotNull BlockPos arg0, boolean arg1) {
        return setBlock(arg0, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public boolean removeBlock(@NotNull BlockPos arg0, boolean arg1) {
        return setBlock(arg0, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public boolean setBlock(@NotNull BlockPos pos, @NotNull BlockState arg1, int arg2) {
        pos = pos.immutable().subtract(anchor);
        bounds = BBHelper.encapsulate(bounds, pos);
        blocks.put(pos, arg1);
        if (blockEntities.containsKey(pos)) {
            BlockEntity blockEntity = blockEntities.get(pos);
            if (!blockEntity.getType().isValid(arg1)) {
                blockEntities.remove(pos);
                renderedBlockEntities.remove(blockEntity);
            }
        }

        BlockEntity blockEntity = getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntities.put(pos, blockEntity);
        }
        return true;
    }

    @Override
    public void sendBlockUpdated(@NotNull BlockPos pos, @NotNull BlockState oldState, @NotNull BlockState newState, int flags) {
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public Iterable<BlockEntity> getBlockEntities() {
        return blockEntities.values();
    }

    public Iterable<BlockEntity> getRenderedBlockEntities() {
        return renderedBlockEntities;
    }

    protected BlockState processBlockStateForPrinting(BlockState state) {
        if (state.getBlock() instanceof AbstractFurnaceBlock && state.hasProperty(BlockStateProperties.LIT)) {
            state = state.setValue(BlockStateProperties.LIT, false);
        }
        return state;
    }

    @Override
    public @NotNull ServerLevel getLevel() {
        if (this.world instanceof ServerLevel serverLevel) {
            return serverLevel;
        }
        throw new IllegalStateException("Cannot use IServerWorld#getWorld in a client environment");
    }
}
