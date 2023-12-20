package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.util.BlockStateUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DyeableBlockEntity extends SmartBlockEntity implements IDyedBuffer {
    @Nullable
    protected DyeColor color;

    public DyeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @Nullable DyeColor getColor() {
        return color;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (color != null)
            tag.putInt("Color", color.getId());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        DyeColor prevColor = color;

        if (tag.contains("Color"))
            color = DyeColor.byId(tag.getInt("Color"));
        else
            color = null;

        if (clientPacket && prevColor != color)
            redraw();
    }

    protected void redraw() {
        if (hasLevel()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            level.getChunkSource()
                .getLightEngine()
                .checkBlock(worldPosition);
        }
    }

    public InteractionResult applyDyeIfValid(ItemStack stack) {
        if (!(stack.getItem()instanceof DyeItem dyeItem))
            return InteractionResult.PASS;
        DyeColor color = dyeItem.getDyeColor();
        if (color == this.color)
            return InteractionResult.PASS;
        if (level.isClientSide() && !isVirtual())
            return InteractionResult.SUCCESS;
        this.color = color;
        notifyUpdate();
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, worldPosition, Block.getId(BlockStateUtils.getWoolBlock(color).defaultBlockState()));
        return InteractionResult.SUCCESS;
    }
}
