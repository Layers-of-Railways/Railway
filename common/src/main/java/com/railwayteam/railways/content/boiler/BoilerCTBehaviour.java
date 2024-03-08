package com.railwayteam.railways.content.boiler;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoilerCTBehaviour extends ConnectedTextureBehaviour {
    private final CTSpriteShiftEntry shift;

    public BoilerCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return shift;
    }

    @Override
    public @Nullable CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        CTSpriteShiftEntry shift = getShift(state, direction, null);
        if (shift == null)
            return null;
        return shift.getType();
    }

    private boolean connectsTo(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction offset) {
        BlockPos otherPos = pos.offset(offset.getNormal());
        BlockState other = reader.getBlockState(otherPos);

        if (other.getBlock() != state.getBlock())
            return false;

        return other.getValue(BoilerBlock.HORIZONTAL_AXIS) == state.getValue(BoilerBlock.HORIZONTAL_AXIS);
    }

    @Override
    public CTContext buildContext(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face, ContextRequirement requirement) {
        CTContext context = new CTContext();

        Direction offset1 = Direction.fromAxisAndDirection(state.getValue(BoilerBlock.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
        Direction offset2 = Direction.fromAxisAndDirection(state.getValue(BoilerBlock.HORIZONTAL_AXIS), Direction.AxisDirection.NEGATIVE);

        context.right = connectsTo(reader, pos, state, offset1);
        context.left = connectsTo(reader, pos, state, offset2);

        return context;
    }
}
