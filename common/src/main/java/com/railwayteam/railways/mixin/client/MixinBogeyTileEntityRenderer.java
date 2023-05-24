package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.coupling.VirtualCouplerRendering;
import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.simibubi.create.content.logistics.trains.BogeyTileEntityRenderer;
import com.simibubi.create.content.logistics.trains.track.StandardBogeyTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BogeyTileEntityRenderer.class, remap = false)
public class MixinBogeyTileEntityRenderer {
    @Inject(method = "renderSafe", at = @At("RETURN"), remap = false)
    private <T extends BlockEntity> void railways$renderVirtualCoupling(T te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (te instanceof StandardBogeyTileEntity sbte && te instanceof IStandardBogeyTEVirtualCoupling virtualCoupling) {
            double couplingDistance = virtualCoupling.getCouplingDistance();
            if (couplingDistance > 0) {
                VirtualCouplerRendering.renderCoupler(virtualCoupling.getCouplingDirection(), couplingDistance,
                    virtualCoupling.getFront(), partialTicks, ms, buffer, light, overlay, sbte);
            }
        }
    }
}
