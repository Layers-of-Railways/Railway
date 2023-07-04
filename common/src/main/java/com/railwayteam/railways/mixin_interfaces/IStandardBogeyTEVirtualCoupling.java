package com.railwayteam.railways.mixin_interfaces;

import net.minecraft.core.Direction;

public interface IStandardBogeyTEVirtualCoupling {
    // -1 indicates no coupling
    void setCouplingDistance(double distance);
    double getCouplingDistance();

    void setCouplingDirection(Direction direction);
    Direction getCouplingDirection();

    void setFront(boolean front);
    boolean getFront();
}
