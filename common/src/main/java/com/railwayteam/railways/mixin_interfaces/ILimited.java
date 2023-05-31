package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.station.StationEditPacket;
import net.minecraft.core.BlockPos;

public interface ILimited {
    void setLimitEnabled(boolean limitEnabled);
    boolean isLimitEnabled();

    static StationEditPacket makeLimitEnabledPacket(BlockPos pos, boolean limitEnabled) {
        StationEditPacket packet = new StationEditPacket(pos);
        ((ILimited) packet).setLimitEnabled(limitEnabled);
        return packet;
    }
}
