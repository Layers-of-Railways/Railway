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
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CarriageContraptionVisual.class)
public abstract class MixinCarriageContraptionVisual extends ContraptionVisual<CarriageContraptionEntity> {
    private MixinCarriageContraptionVisual(VisualizationContext ctx, CarriageContraptionEntity entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

    @WrapOperation(method = "animate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/BogeyVisual;update(Lnet/minecraft/nbt/CompoundTag;FLcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void updateEntity(BogeyVisual instance, CompoundTag compoundTag, float v, PoseStack poseStack, Operation<Void> original) {
        if (instance instanceof BogeyDisplayHolder holder) {
            holder.runWithDisplay(display -> {
                if (display instanceof BogeyDisplay.EntityAware entityAware) {
                    entityAware.entityUpdate(this.entity);
                }
            });
        }
        original.call(instance, compoundTag, v, poseStack);
    }
}
