package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.content.fuel.psi.PortableFuelInterfaceBlockEntity.InterfaceFluidHandler;
import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = FluidNetwork.class, remap = false)
public class FluidNetworkMixin {
    @Shadow Set<Pair<BlockFace, PipeConnection>> frontier;
    @Shadow LazyOptional<IFluidHandler> source;

    @Inject(method = "keepPortableFluidInterfaceEngaged", at = @At("HEAD"))
    private void keepPortableFluidInterfaceEngaged(CallbackInfo ci) {
        IFluidHandler handler = source.orElse(null);
        if (!(handler instanceof InterfaceFluidHandler || handler instanceof PortableFluidInterfaceBlockEntity.InterfaceFluidHandler))
            return;
        if (frontier.isEmpty())
            return;
        if (handler instanceof InterfaceFluidHandler)
            ((InterfaceFluidHandler) handler).keepAlive();
    }
}
