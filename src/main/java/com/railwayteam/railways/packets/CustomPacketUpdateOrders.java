package com.railwayteam.railways.packets;

import com.railwayteam.railways.Containers;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysPacketHandler;
import com.railwayteam.railways.StationListContainer;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import com.railwayteam.railways.items.StationLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CustomPacketUpdateOrders extends RailwaysPacketHandler.CustomPacketBase {
  private String target;
  private ArrayList<String> stations;

  public CustomPacketUpdateOrders (String target, ArrayList<String> list) {
    this.target = target;
    stations = list;
  }

  public CustomPacketUpdateOrders (PacketBuffer buffer) {
    target = buffer.readString();
    int size = buffer.readInt();
    stations = new ArrayList<String>();
    for (int entry=0; entry<size; entry++) {
      stations.add(buffer.readString());
    }
  }

  @Override
  public void write (PacketBuffer buf) {
    buf.writeString(target);
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
        if (target.startsWith("minecart")) {
        //  Railways.LOGGER.debug("handling minecart packet...");
          Entity targetEntity = player.world.getEntityByID(Integer.parseInt(target.replace("minecart","")));
          if (targetEntity instanceof AbstractMinecartEntity) {
          //  Railways.LOGGER.debug("  minecart is minecart");
            ((AbstractMinecartEntity)targetEntity).getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
            //  Railways.LOGGER.debug("    adding " + stations.size() + " stations...");
              capability.clear();
              for (String loc : stations) {
                capability.add(loc);
              }
            });
          }
        }
      }
    });
    context.get().setPacketHandled(true);
  }
}
