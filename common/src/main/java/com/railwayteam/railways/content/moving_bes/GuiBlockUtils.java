/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.moving_bes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GuiBlockUtils {
    public static void checkAccess(ContainerLevelAccess containerLevelAccess, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (containerLevelAccess instanceof GuiBlockLevelAccess) {
            cir.setReturnValue(containerLevelAccess.evaluate((level, pos) -> player.distanceToSqr(
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D) <= 64.0D,
                    true
            ));
        }
    }

    // Called Via ASM, do not remove
    @SuppressWarnings("unused")
    @Nullable
    public static GuiBlockLevelAccess createNewGuiContraptionWorld(Level level) {
        if (level instanceof GuiBlockContraptionWorld guiBlockContraptionWorld) {
            return new GuiBlockLevelAccess(
                    guiBlockContraptionWorld.getLevel(),
                    guiBlockContraptionWorld.contraption.entity,
                    guiBlockContraptionWorld.blockPos
            );
        }
        return null;
    }
}
