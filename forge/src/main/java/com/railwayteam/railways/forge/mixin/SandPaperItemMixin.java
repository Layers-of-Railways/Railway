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

package com.railwayteam.railways.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.util.BlockStateUtils;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SandPaperItem.class)
public class SandPaperItemMixin {
    @WrapOperation(
        method = "useOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getToolModifiedState(Lnet/minecraft/world/item/context/UseOnContext;Lnet/minecraftforge/common/ToolAction;Z)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState stripLocometal(BlockState instance, UseOnContext context, ToolAction toolAction, boolean simulate, Operation<BlockState> original) {
        Pair<Styles, PalettesColor> style = CRPalettes.getStyleForBlock(instance.getBlock());
        if (style != null && !style.getSecond().isNetherite()) {
            return BlockStateUtils.blockWithProperties(
                style.getFirst().get(PalettesColor.NETHERITE).getDefaultState(),
                instance
            );
        } else {
            return original.call(instance, context, toolAction, simulate);
        }
    }
}
