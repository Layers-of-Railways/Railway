package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class OilburnerSmokeStackBlock extends SmokeStackBlock {

    public static final BooleanProperty ENCASED = BooleanProperty.create("encased");

    public OilburnerSmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape, boolean createsStationarySmoke) {
        super(properties, type, shape, createsStationarySmoke);
        registerDefaultState(defaultBlockState().setValue(ENCASED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(ENCASED));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        boolean encased = state.getValue(ENCASED);
        Level level = context.getLevel();
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        if (encased) {
            level.setBlockAndUpdate(context.getClickedPos(), state.setValue(ENCASED, false));
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(),
                Block.getId(AllBlocks.INDUSTRIAL_IRON_BLOCK.getDefaultState()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hitResult) {
        boolean encased = state.getValue(ENCASED);
        if (!AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(player.getItemInHand(hand)))
            return super.use(state, level, pos, player, hand, hitResult);
        if (encased)
            return super.use(state, level, pos, player, hand, hitResult);
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        level.setBlockAndUpdate(pos, state.setValue(ENCASED, true));
        level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.05f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos,
                                        @NotNull CollisionContext pContext) {
        if (pState.getValue(ENCASED))
            return CRShapes.BLOCK;
        return super.getShape(pState, pLevel, pPos, pContext);
    }
}
