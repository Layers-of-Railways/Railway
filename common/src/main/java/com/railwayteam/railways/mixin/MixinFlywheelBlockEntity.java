package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ICarriageFlywheel;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FlywheelBlockEntity.class)
public class MixinFlywheelBlockEntity implements ICarriageFlywheel {
    @Shadow(remap = false) float angle;

    @Override
    public float railways$getAngle() {
        return angle;
    }

    @Override
    public void railways$setAngle(float angle) {
        this.angle = angle;
    }
}
