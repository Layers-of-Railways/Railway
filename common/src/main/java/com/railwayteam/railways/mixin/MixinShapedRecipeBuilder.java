/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
