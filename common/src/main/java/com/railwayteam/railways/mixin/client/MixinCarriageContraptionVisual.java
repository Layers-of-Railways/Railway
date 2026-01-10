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
import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageContraptionVisual.class)
public abstract class MixinCarriageContraptionVisual extends ContraptionVisual<CarriageContraptionEntity> implements IUpdateCount {
	private MixinCarriageContraptionVisual(VisualizationContext ctx, CarriageContraptionEntity entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

	@Shadow(remap = false)
	@Final
	public static int MAX_NUM_BOGEYS;
	
	@Mutable
	@Shadow(remap = false)
	@Final
	private BogeyVisual[] visuals;

	@Shadow private int numBogeys;
	@Unique
	private int railways$updateCount = 0;

	@Override
	public int railways$getUpdateCount() {
		return railways$updateCount;
	}

	@Override
	public void railways$fromParent(IUpdateCount parent) {
		railways$updateCount = parent.railways$getUpdateCount();
	}

	@Override
	public void railways$markUpdate() {
		railways$updateCount++;
	}

	@Inject(method = "beginFrame", at = @At("HEAD"), remap = false)
	private void railways$refreshBogeys(CallbackInfo ci) {
		if (IUpdateCount.outOfSync(this, (IUpdateCount) this.entity)) {
			for (BogeyVisual visual : visuals) {
				if (visual != null) {
					visual.delete();
				}
			}
			visuals = new BogeyVisual[MAX_NUM_BOGEYS];
			numBogeys = 0;
			this.railways$fromParent((IUpdateCount) this.entity);
		}
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
