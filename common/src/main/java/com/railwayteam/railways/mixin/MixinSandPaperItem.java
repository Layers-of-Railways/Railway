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
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.util.BlockStateUtils;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.foundation.utility.Pair;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.AxeItemAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(SandPaperItem.class)
public class MixinSandPaperItem {
    @WrapOperation(
        method = "useOn",
        at = @At(
            value = "INVOKE",
            target = "Lio/github/fabricators_of_create/porting_lib/mixin/accessors/common/accessor/AxeItemAccessor;porting_lib$getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"
        )
    )
    private Optional<BlockState> stripLocometal(AxeItemAccessor instance, BlockState blockState, Operation<Optional<BlockState>> original) {
        Pair<Styles, PalettesColor> style = CRPalettes.getStyleForBlock(blockState.getBlock());
        if (style != null && !style.getSecond().isNetherite()) {
            return Optional.of(BlockStateUtils.blockWithProperties(
                style.getFirst().get(PalettesColor.NETHERITE).getDefaultState(),
                blockState
            ));
        } else {
            return original.call(instance, blockState);
        }
    }
}
