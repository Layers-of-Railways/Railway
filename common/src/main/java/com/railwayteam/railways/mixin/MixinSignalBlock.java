package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SignalBlock.class, remap = false)
public abstract class MixinSignalBlock extends Block { //TODO _track api (ok not really, but this should just be in Create, if it already is, remove this) - it is, remove it

    private MixinSignalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }
}
