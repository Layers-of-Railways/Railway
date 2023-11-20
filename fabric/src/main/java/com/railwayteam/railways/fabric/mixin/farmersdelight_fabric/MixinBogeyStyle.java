package com.railwayteam.railways.fabric.mixin.farmersdelight_fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.mixin.CRMixinPlugin;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Hacky fix since the farmers delight fabric dev doesn't want to fix it on their side.
 * Checks if Farmers delight is loaded inside of {@link CRMixinPlugin#shouldApplyMixin}
 */
@Mixin(BogeyStyle.class)
public class MixinBogeyStyle {
    /*
     the DefaultedRegistry.get is a generic method, and thus returns an Object, with a CHECKCAST in bytecode
     ```
     INVOKEVIRTUAL net/minecraft/core/DefaultedRegistry.get (Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;
     CHECKCAST net/minecraft/world/level/block/Block
     ```
     */
    @WrapOperation(method = "getBlockOfSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;"))
    private Object getBlockOfSize(DefaultedRegistry<Block> instance, ResourceLocation name, Operation<Object> original) {
        if (name == null) {
            return Blocks.AIR;
        }
        return original.call(instance, name);
    }
}
