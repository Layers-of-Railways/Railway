/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.simibubi.create.content.trains.bogey.BogeyInstance;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionInstance;
import com.simibubi.create.foundation.utility.Couple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageContraptionInstance.class)
public abstract class MixinCarriageContraptionInstance extends EntityInstance<CarriageContraptionEntity> implements IUpdateCount {
    private int updateCount = 0;

    private MixinCarriageContraptionInstance(MaterialManager materialManager, CarriageContraptionEntity entity) {
        super(materialManager, entity);
    }

    @Override
    public int railways$getUpdateCount() {
        return updateCount;
    }

    @Override
    public void railways$fromParent(IUpdateCount parent) {
        updateCount = parent.railways$getUpdateCount();
    }

    @Override
    public void railways$markUpdate() {
        updateCount++;
    }

    @Shadow(remap = false)
    private Couple<BogeyInstance> bogeys;

    @Inject(method = "beginFrame", at = @At("HEAD"), remap = false)
    private void railways$refreshBogeys(CallbackInfo ci) {
        if (IUpdateCount.outOfSync(this, (IUpdateCount) this.entity)) {
            if (bogeys != null) {
                bogeys.forEach(instance -> {
                    if (instance != null) {
                        instance.renderer.remove();
                        instance.commonRenderer.ifPresent(BogeyRenderer::remove);
                    }
                });
                bogeys = null;
            }
            this.railways$fromParent((IUpdateCount) this.entity);
        }
    }
}
