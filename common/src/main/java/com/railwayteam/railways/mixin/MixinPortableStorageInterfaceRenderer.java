package com.railwayteam.railways.mixin;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.AbstractionUtils;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
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
