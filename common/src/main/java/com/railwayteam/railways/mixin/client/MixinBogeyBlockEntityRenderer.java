package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.coupling.VirtualCouplerRendering;
import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BogeyBlockEntityRenderer.class, remap = false)
public class MixinBogeyBlockEntityRenderer {
    @Inject(method = "renderSafe", at = @At("RETURN"), remap = false)
    private <T extends BlockEntity> void railways$renderVirtualCoupling(T te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (te instanceof StandardBogeyBlockEntity sbte && te instanceof IStandardBogeyTEVirtualCoupling virtualCoupling) {
            double couplingDistance = virtualCoupling.getCouplingDistance();
            if (couplingDistance > 0) {
                VirtualCouplerRendering.renderCoupler(virtualCoupling.getCouplingDirection(), couplingDistance,
                    virtualCoupling.getFront(), partialTicks, ms, buffer, light, overlay, sbte);
            }
        }
    }
}
