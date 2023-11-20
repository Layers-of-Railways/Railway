package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public class MixinWalkNodeEvaluator {
    @Inject(method = "getBlockPathTypeRaw", at = @At("RETURN"), cancellable = true)
    private static void doNotOpenSpecialDoors(BlockGetter level, BlockPos pos, CallbackInfoReturnable<BlockPathTypes> cir) {
        if (cir.getReturnValue() == BlockPathTypes.DOOR_WOOD_CLOSED) {
            if (level.getBlockEntity(pos) instanceof SlidingDoorMode.IHasDoorMode doorMode && doorMode.snr$getSlidingDoorMode() == SlidingDoorMode.SPECIAL) {
                cir.setReturnValue(BlockPathTypes.DOOR_IRON_CLOSED);
            }
        }
    }
}
