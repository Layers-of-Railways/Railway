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

package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.fuel.psi.PortableFuelInterfaceBlockEntity.InterfaceFluidHandler;
import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Pair;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = FluidNetwork.class, remap = false)
public class FluidNetworkMixin {
    @Shadow Set<Pair<BlockFace, PipeConnection>> frontier;
    @Shadow Storage<FluidVariant> source;

    @Inject(method = "keepPortableFluidInterfaceEngaged", at = @At("HEAD"))
    private void keepPortableFluidInterfaceEngaged(CallbackInfo ci) {
        Storage<FluidVariant> handler = source;
        if (!(handler instanceof InterfaceFluidHandler || handler instanceof PortableFluidInterfaceBlockEntity.InterfaceFluidHandler))
            return;
        if (frontier.isEmpty())
            return;
        if (handler instanceof InterfaceFluidHandler fluidHandler)
            fluidHandler.keepAlive();
    }
}
