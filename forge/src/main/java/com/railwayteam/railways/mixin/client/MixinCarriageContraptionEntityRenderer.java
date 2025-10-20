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

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplayHolder;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CarriageContraptionEntityRenderer.class)
public class MixinCarriageContraptionEntityRenderer {
    @WrapOperation(method = "lambda$render$1", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/BogeyStyle;render(Lcom/simibubi/create/content/trains/bogey/BogeySizes$BogeySize;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIFLnet/minecraft/nbt/CompoundTag;Z)V"))
    private static void updateEntity(BogeyStyle instance, BogeySizes.BogeySize size, float partialTick,
                                     PoseStack poseStack, MultiBufferSource buffers, int light, int overlay,
                                     float wheelAngle, CompoundTag bogeyData, boolean inContraption,
                                     Operation<Void> original, CarriageContraptionEntity entity) {
        BogeyStyle.SizeRenderer renderer = ((AccessorBogeyStyle) instance).getSizeRenderers().get(size);
        if (renderer != null && renderer.renderer() instanceof BogeyDisplayHolder holder) {
            holder.runWithDisplay(display -> {
                if (display instanceof BogeyDisplay.EntityAware entityAware) {
                    entityAware.entityUpdate(entity);
                }
            });
        }
        original.call(instance, size, partialTick, poseStack, buffers, light, overlay, wheelAngle, bogeyData, inContraption);
    }
}
