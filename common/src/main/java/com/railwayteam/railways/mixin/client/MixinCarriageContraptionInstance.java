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
    public int snr_getUpdateCount() {
        return updateCount;
    }

    @Override
    public void snr_fromParent(IUpdateCount parent) {
        updateCount = parent.snr_getUpdateCount();
    }

    @Override
    public void snr_markUpdate() {
        updateCount++;
    }

    @Shadow(remap = false)
    private Couple<BogeyInstance> bogeys;

    @Inject(method = "beginFrame", at = @At("HEAD"), remap = false)
    private void snr_refreshBogeys(CallbackInfo ci) {
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
            this.snr_fromParent((IUpdateCount) this.entity);
        }
    }
}
