package com.railwayteam.railways.mixin.compat.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.annotation.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.mixin_interfaces.IForceRenderingSodium;
import me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(BlockOcclusionCache.class)
public class MixinBlockOcclusionCache {
    @Inject(method = "shouldDrawSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"), cancellable = true)
    private void forceRendering(BlockState selfState, BlockGetter view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir, @Local(name = "adjState") BlockState adjState) {
        if (selfState.getBlock() instanceof IForceRenderingSodium forceRendering) {
            if (forceRendering.forceRenderingSodium(selfState, adjState, facing))
                cir.setReturnValue(true);
        }
    }
}
