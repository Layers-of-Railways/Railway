package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapedRecipeBuilder.class)
public class MixinShapedRecipeBuilder {
    @WrapOperation(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getItemCategory()Lnet/minecraft/world/item/CreativeModeTab;"))
    private CreativeModeTab useDefaultCategoryForNullCategory(Item instance, Operation<CreativeModeTab> original) {
        CreativeModeTab tab = original.call(instance);
        if (tab == null) {
            return CRItems.mainCreativeTab;
        } else {
            return tab;
        }
    }
}
