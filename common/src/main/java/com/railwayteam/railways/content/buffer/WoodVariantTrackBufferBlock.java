package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.AdventureUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class WoodVariantTrackBufferBlock extends TrackBufferBlock<WoodVariantTrackBufferBlockEntity> {
    protected WoodVariantTrackBufferBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<WoodVariantTrackBufferBlockEntity> getBlockEntityClass() {
        return WoodVariantTrackBufferBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WoodVariantTrackBufferBlockEntity> getBlockEntityType() {
        return CRBlockEntities.TRACK_BUFFER_WOOD_VARIANT.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AdventureUtils.isAdventure(pPlayer))
            return InteractionResult.PASS;
        return onBlockEntityUse(pLevel, pPos, be -> be.applyMaterialIfValid(pPlayer.getItemInHand(pHand)));
    }
}
