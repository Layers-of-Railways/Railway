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

package com.railwayteam.railways.forge.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.util.IHaveCustomGoggleIcon;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO - Remove when https://github.com/Creators-of-Create/Create/pull/5900 is merged
// Has been merged, awaiting release
@Deprecated
@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayRendererMixin {
    @WrapOperation(
            method = "renderOverlay",
            at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/ItemEntry;asStack()Lnet/minecraft/world/item/ItemStack;")
    )
    private static ItemStack changeDisplayItem(ItemEntry<GogglesItem> instance, Operation<ItemStack> original,
                                               @Local BlockEntity be, @Local(name = "wearingGoggles") boolean wearingGoggles) {
        if (be instanceof IHaveCustomGoggleIcon gte && wearingGoggles) {
            return gte.railways$setGoggleIcon(Minecraft.getInstance().player.isShiftKeyDown());
        }

        return original.call(instance);
    }
}
