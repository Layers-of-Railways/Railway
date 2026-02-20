/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.palettes.PalettesFlywheelBlock;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.content.kinetics.flywheel.FlywheelVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlywheelVisual.class)
public class MixinFlywheelVisual {
    @WrapOperation(method = "<init>", at = @At(value = "FIELD", target = "Lcom/simibubi/create/AllPartialModels;FLYWHEEL:Ldev/engine_room/flywheel/lib/model/baked/PartialModel;", opcode = Opcodes.GETSTATIC))
    private PartialModel palettesFlywheel(Operation<PartialModel> original, VisualizationContext context, FlywheelBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getBlock() instanceof PalettesFlywheelBlock pfb)
            return CRBlockPartials.FLYWHEELS.get(pfb.getColor());

        return original.call();
    }
}
