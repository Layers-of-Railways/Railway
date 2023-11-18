package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageBogey;

import java.util.function.Supplier;

public interface CarriageBogeyUtils {
    static AbstractBogeyBlock<?> getType(CarriageBogey bogey) {
        Supplier<Supplier<AbstractBogeyBlock<?>>> supplier = () -> () -> ((AccessorCarriageBogey) bogey).getType(); // classload protection
        return supplier.get().get();
    }
}
