package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.block.BigOutlines;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BigOutlines.class)
public class MixinBigOutlines {
    @WrapOperation(method = "lambda$pick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private static Block genericCrossingsAreCustom(BlockState instance, Operation<Block> originalOperation) {
        Block original = originalOperation.call(instance);
        if (original instanceof GenericCrossingBlock)
            return AllBlocks.TRACK.get();
        return original;
    }
}
