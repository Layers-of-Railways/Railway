package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.simibubi.create.content.logistics.trains.track.StandardBogeyTileEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = StandardBogeyTileEntity.class, remap = false)
public class MixinStandardBogeyTileEntity implements IStandardBogeyTEVirtualCoupling {
    private double coupling = -1;
    @Override
    public void setCouplingDistance(double distance) {
        coupling = distance;
    }

    @Override
    public double getCouplingDistance() {
        return coupling;
    }

    private Direction direction = Direction.UP;

    @Override
    public void setCouplingDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getCouplingDirection() {
        return direction;
    }

    private boolean front = false;

    @Override
    public void setFront(boolean front) {
        this.front = front;
    }

    @Override
    public boolean getFront() {
        return front;
    }
}
