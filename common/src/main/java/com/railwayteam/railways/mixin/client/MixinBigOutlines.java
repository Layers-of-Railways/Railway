package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.util.IHasBigOutline;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.foundation.block.BigOutlines;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(BigOutlines.class)
public class MixinBigOutlines {
    @WrapOperation(method = "lambda$pick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private static Block genericCrossingsAreCustom(BlockState instance, Operation<Block> originalOperation) {
        Block original = originalOperation.call(instance);
        if (original instanceof GenericCrossingBlock)
            return AllBlocks.TRACK.get();
        return original;
    }

    // TODO - Remove when https://github.com/Creators-of-Create/Create/pull/6187 is merged
    // targets the `instanceof SlidingDoorBlock` and add's one for IHasBigOutline
    @Deprecated
    @WrapOperation(method = "lambda$pick$0", constant = @Constant(classValue = SlidingDoorBlock.class))
    private static boolean railways$tempBigOutlinesAPI(Object object, Operation<Boolean> original) {
        return original.call(object) || object instanceof IHasBigOutline;
    }
}
