package com.railwayteam.railways.mixin.compat.farmersdelight_fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

/*
 * Hacky fix since the farmers delight fabric dev doesn't want to fix it on their side.
 * Checks if Farmers delight is loaded inside of com.railwayteam.railways.mixin.CRMixinPlugin#shouldApplyMixin
 */
@Mixin(BogeyStyle.class)
public class MixinBogeyStyle {
    @Shadow private Map<BogeySizes.BogeySize, ResourceLocation> sizes;

    @WrapOperation(method = "getBlockOfSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;"))
    private Block getBlockOfSize(BogeyStyle instance, BogeySizes.BogeySize size, Operation<Block> original) {
        if (sizes.get(size) == null) {
            return Blocks.AIR;
        }
        return original.call(instance, size);
    }
}
