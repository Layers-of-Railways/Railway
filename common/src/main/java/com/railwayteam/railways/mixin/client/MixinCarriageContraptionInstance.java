/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.createmod.catnip.data.Couple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageContraptionVisual.class)
public abstract class MixinCarriageContraptionInstance extends ContraptionVisual<CarriageContraptionEntity> implements IUpdateCount {
    @Unique
    private int railways$updateCount = 0;

    public MixinCarriageContraptionInstance(VisualizationContext ctx, CarriageContraptionEntity entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

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

    @Shadow(remap = false)
    private Couple<BogeyVisual> bogeys;

    @Inject(method = "beginFrame", at = @At("HEAD"), remap = false)
    private void railways$refreshBogeys(CallbackInfo ci) {
        if (IUpdateCount.outOfSync(this, (IUpdateCount) this.entity)) {
            if (bogeys != null) {
                bogeys.forEach(visual -> {
                    if (visual != null) {
                        visual.delete();
                    }
                });
                bogeys = null;
            }
            this.railways$fromParent((IUpdateCount) this.entity);
        }
    }
}
