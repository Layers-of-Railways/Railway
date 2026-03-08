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
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.util.ItemUtils;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotatoCannonItem.class)
public class MixinPotatoCannonItem {
    @Inject(method = "lambda$use$2", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/potatoCannon/PotatoProjectileTypeManager;getTypeForStack(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"))
    private void splitPitcher(
        Player player,
        ItemStack stack,
        InteractionHand hand,
        Level world,
        ItemStack itemStack,
        CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
        @Local(argsOnly = true, index = 5, name="itemStack") LocalRef<ItemStack> itemStack$
    ) {
        if (itemStack.getItem() instanceof PaintPitcherItem item) {
            int levels = item.getLevels(itemStack);
            int usedLevels = Math.min(levels, PaintPitcherItem.LEVELS_PER_CANNON_SHOT);

            itemStack$.set(item.copyAsFilledStack(itemStack, usedLevels));
            if (!player.isCreative() && !ItemUtils.isUnbreakable(itemStack)) {
                item.setFillInPlace(itemStack, levels - usedLevels);
            }
        }
    }

    // todo PR to Create
    @WrapOperation(method = "lambda$use$2", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/potatoCannon/PotatoProjectileEntity;setItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private void preventClearInSurvival(PotatoProjectileEntity instance, ItemStack stack, Operation<Void> original) {
        original.call(instance, stack.copy());
    }
}
