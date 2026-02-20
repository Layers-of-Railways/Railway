/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotatoCannonItem.class)
public class MixinPotatoCannonItem {
    @WrapOperation(method = "getAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack splitPitcher(Player instance, ItemStack weaponStack, Operation<ItemStack> original) {
        ItemStack itemStack = original.call(instance, weaponStack);
        if (itemStack.getItem() instanceof PaintPitcherItem item) {
            int levels = item.getLevels(itemStack);
            int usedLevels = Math.min(levels, PaintPitcherItem.LEVELS_PER_CANNON_SHOT);

            ItemStack returnStack = item.copyAsFilledStack(itemStack, usedLevels);
            if (!instance.isCreative()) {
                item.setFillInPlace(itemStack, levels - usedLevels);
            }
            return returnStack;
        }
        return itemStack;
    }
}
