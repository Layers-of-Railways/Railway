package com.railwayteam.railways.mixin_interfaces;

import java.util.List;
import java.util.UUID;

public interface ICarriageConductors {
    List<UUID> railways$getControllingConductors();
}
