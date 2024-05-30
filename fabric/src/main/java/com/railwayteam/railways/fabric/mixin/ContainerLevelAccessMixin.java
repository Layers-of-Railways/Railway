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

package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.moving_bes.GuiBlockContraptionWorld;
import com.railwayteam.railways.content.moving_bes.GuiBlockLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Priority of 2000 to be applied after any other @Overwrite's
@Mixin(value = ContainerLevelAccess.class, priority = 2000)
public interface ContainerLevelAccessMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void create(Level level, BlockPos pos, CallbackInfoReturnable<ContainerLevelAccess> cir) {
        if (level instanceof GuiBlockContraptionWorld guiBlockContraptionWorld) {
            cir.setReturnValue(new GuiBlockLevelAccess(
                    guiBlockContraptionWorld.getLevel(),
                    guiBlockContraptionWorld.contraption.entity,
                    guiBlockContraptionWorld.blockPos
            ));
        }
    }
}
