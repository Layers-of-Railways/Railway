package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public class MixinDoorBlock {
    @Inject(method = "isWoodenDoor(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
    private static void doNotOpenSpecialDoors(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (level.getBlockEntity(pos) instanceof SlidingDoorMode.IHasDoorMode doorMode && doorMode.snr$getSlidingDoorMode() == SlidingDoorMode.SPECIAL) {
                cir.setReturnValue(false);
            }
        }
    }
}
