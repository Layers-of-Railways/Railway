package com.railwayteam.railways.content.buffer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WoodVariantTrackBufferBlockEntity extends TrackBufferBlockEntity implements IMaterialAdaptingBuffer {

    protected BlockState material;

    public WoodVariantTrackBufferBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        material = Blocks.SPRUCE_PLANKS.defaultBlockState();
    }

    @NotNull
    public BlockState getMaterial() {
        return material;
    }

    public InteractionResult applyMaterialIfValid(ItemStack stack) {
        if (!(stack.getItem()instanceof BlockItem blockItem))
            return InteractionResult.PASS;
        BlockState material = blockItem.getBlock()
            .defaultBlockState();
        if (material == this.material)
            return InteractionResult.PASS;
        if (!material.is(BlockTags.PLANKS))
            return InteractionResult.PASS;
        if (level.isClientSide() && !isVirtual())
            return InteractionResult.SUCCESS;
        this.material = material;
        notifyUpdate();
        level.levelEvent(2001, worldPosition, Block.getId(material));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        BlockState prevMaterial = material;
        if (!compound.contains("Material"))
            return;

        material = NbtUtils.readBlockState(blockHolderGetter(), compound.getCompound("Material"));
        if (material.isAir())
            material = Blocks.SPRUCE_PLANKS.defaultBlockState();

        if (clientPacket && prevMaterial != material)
            redraw();
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Material", NbtUtils.writeBlockState(material));
    }
}
