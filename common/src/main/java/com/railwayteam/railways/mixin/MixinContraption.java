package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.mixin_interfaces.IPreAssembleCallback;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Contraption.class)
public class MixinContraption {
    @Inject(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
    private void applyPreTransformCallback(Level world, BlockPos offset, CallbackInfo ci, @Local(name="add") BlockPos add) {
        BlockEntity be = world.getBlockEntity(add);
        if (be instanceof IPreAssembleCallback preTransformCallback)
            preTransformCallback.snr$preAssemble();

        if (be instanceof SmartBlockEntity smartBE) {
            for (BlockEntityBehaviour behaviour : smartBE.getAllBehaviours()) {
                if (behaviour instanceof IPreAssembleCallback preTransformCallback)
                    preTransformCallback.snr$preAssemble();
            }
        }
    }
}
