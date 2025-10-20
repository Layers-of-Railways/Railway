/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.AbstractionUtils;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortableStorageInterfaceRenderer.class)
public class MixinPortableStorageInterfaceRenderer {
    @Inject(method = "getMiddleForState", at = @At("HEAD"), cancellable = true)
    private static void getMiddleForState(BlockState state, boolean lit, CallbackInfoReturnable<PartialModel> cir) {
        if (AbstractionUtils.portableFuelInterfaceBlockHasState(state))
            cir.setReturnValue(lit ? CRBlockPartials.PORTABLE_FUEL_INTERFACE_MIDDLE_POWERED
                    : CRBlockPartials.PORTABLE_FUEL_INTERFACE_MIDDLE);
    }

    @Inject(method = "getTopForState", at = @At("HEAD"), cancellable = true)
    private static void getTopForState(BlockState state, CallbackInfoReturnable<PartialModel> cir) {
        if (AbstractionUtils.portableFuelInterfaceBlockHasState(state))
            cir.setReturnValue(CRBlockPartials.PORTABLE_FUEL_INTERFACE_TOP);
    }
}
