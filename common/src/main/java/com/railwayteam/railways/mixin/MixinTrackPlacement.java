package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TrackPlacement.class, remap = false)
public class MixinTrackPlacement {
    // minimum curve length for wide gauge
    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "tryConnect", at = {
        @At(value = "CONSTANT", args = "doubleValue=7"),
        @At(value = "CONSTANT", args = "doubleValue=3.25")
    })
    private static double widerCurveForWideGauge(double value, Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack) {
        if (TrackMaterial.fromItem(stack.getItem()).trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE)
            return value * 2;

        return value;
    }
}
