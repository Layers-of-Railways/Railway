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

package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.coupling.VirtualCouplerRendering;
import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BogeyBlockEntityRenderer.class, remap = false)
public class MixinBogeyBlockEntityRenderer {
    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("RETURN"), remap = false)
    private void railways$renderVirtualCoupling(AbstractBogeyBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (be instanceof StandardBogeyBlockEntity sbte && be instanceof IStandardBogeyTEVirtualCoupling virtualCoupling) {
            double couplingDistance = virtualCoupling.getCouplingDistance();
            if (couplingDistance > 0) {
                VirtualCouplerRendering.renderCoupler(virtualCoupling.getCouplingDirection(), couplingDistance,
                    virtualCoupling.getFront(), partialTicks, ms, buffer, light, overlay, sbte);
            }
        }
    }
}
