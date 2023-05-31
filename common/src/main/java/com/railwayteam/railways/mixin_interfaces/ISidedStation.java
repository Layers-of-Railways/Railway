package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.station.StationEditPacket;
import net.minecraft.core.BlockPos;

public interface ISidedStation {
    boolean opensRight();
    boolean opensLeft();

    void setOpensRight(boolean opensRight);
    void setOpensLeft(boolean opensLeft);

    static StationEditPacket makeOpenRightPacket(BlockPos pos, boolean openRight) {
        StationEditPacket packet = new StationEditPacket(pos);
        ((ISidedStation) packet).setOpensRight(openRight);
        return packet;
    }

    static StationEditPacket makeOpenLeftPacket(BlockPos pos, boolean openLeft) {
        StationEditPacket packet = new StationEditPacket(pos);
        ((ISidedStation) packet).setOpensLeft(openLeft);
        return packet;
    }
}
