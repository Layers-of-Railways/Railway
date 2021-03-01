package com.railwayteam.railways.packets;

import com.railwayteam.railways.RailwaysPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CustomPacketCartSchedule extends RailwaysPacketHandler.CustomPacketBase {
  public CustomPacketCartSchedule () {
    // pass
  }

  public CustomPacketCartSchedule (PacketBuffer buf) {
    // pass
  }

  @Override
  public void write (PacketBuffer buf) {
    // pass
  }

  @Override
  public void handle (Supplier<NetworkEvent.Context> context) {
    boolean success = true;
    context.get().enqueueWork( ()-> {
      // do stuff here
    });
    context.get().setPacketHandled(success);
  }
}
