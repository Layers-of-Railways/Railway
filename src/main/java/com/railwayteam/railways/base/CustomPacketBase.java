package com.railwayteam.railways.base;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class CustomPacketBase {
    public abstract void write(PacketBuffer buf);

    public abstract void handle(Supplier<NetworkEvent.Context> context);
}
