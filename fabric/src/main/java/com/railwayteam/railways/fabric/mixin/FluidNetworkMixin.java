package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.fuel.psi.PortableFuelInterfaceBlockEntity;
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
        if (handler instanceof InterfaceFluidHandler)
            ((InterfaceFluidHandler) handler).keepAlive();
    }
}
