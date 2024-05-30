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

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {
    @Shadow protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> cartType, Predicate<Entity> filter);

    @Inject(method = "getAnalogOutputSignal",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DetectorRailBlock;getInteractingMinecartOfType(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 1),
        cancellable = true)
    private void getOverrideAnalogOutputSignal(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        List<MinecartJukebox> jukeboxCarts = getInteractingMinecartOfType(level, pos, MinecartJukebox.class, cart -> true);
        if (!jukeboxCarts.isEmpty()) {
            cir.setReturnValue(jukeboxCarts.get(0).getComparatorOutput());
        }
    }
}
