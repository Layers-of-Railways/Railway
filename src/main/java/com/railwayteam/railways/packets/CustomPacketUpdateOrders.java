package com.railwayteam.railways.packets;

import com.railwayteam.railways.Containers;
import com.railwayteam.railways.RailwaysPacketHandler;
import com.railwayteam.railways.StationListContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CustomPacketUpdateOrders extends RailwaysPacketHandler.CustomPacketBase {
  private ArrayList<String> stations;

  public CustomPacketUpdateOrders (ArrayList<String> list) {
    stations = list;
  }

  public CustomPacketUpdateOrders (PacketBuffer buffer) {
    int size = buffer.readInt();
    stations = new ArrayList<String>();
    for (int entry=0; entry<size; entry++) {
      stations.add(buffer.readString());
    }
  }

  @Override
  public void write (PacketBuffer buf) {
    buf.writeInt(stations.size());
    for (String entry : stations) {
      buf.writeString(entry);
    }
  }

  @Override
  public void handle (Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork( ()-> {
      ServerPlayerEntity player = context.get().getSender();
      if (player == null) return;
      if (player.openContainer instanceof StationListContainer) {
        ((StationListContainer)player.openContainer).updateStationList(stations);
      }
    });
    context.get().setPacketHandled(true);
  }
}
