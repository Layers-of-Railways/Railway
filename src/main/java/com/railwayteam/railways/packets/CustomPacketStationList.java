package com.railwayteam.railways.packets;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysPacketHandler;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomPacketStationList extends RailwaysPacketHandler.CustomPacketBase {
  private int id;
  private List<String> stations;

  //public CustomPacketStationList() { this(0, ""); }
  public CustomPacketStationList() { this(0, new ArrayList<String>()); }

  public CustomPacketStationList (int id, List<String> stations) {
    this.id = id;
    this.stations = stations;
  }

  public CustomPacketStationList (PacketBuffer buffer) {
    id = buffer.readInt();
    int len = buffer.readInt();
    if (stations == null) stations = new ArrayList<String>();
    for (int i=0; i<len; i++) {
      this.stations.add(buffer.readUtf());
    }
  }

  @Override
  public void write(PacketBuffer buf) {
    buf.writeInt(id);
    buf.writeInt(stations.size());
    for (String station : stations) {
      buf.writeUtf(station);
    }
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    NetworkEvent.Context ctx = context.get();
    if (Minecraft.getInstance().player == null) LogManager.getLogger(Railways.MODID).debug("null sender");
    ctx.enqueueWork( ()-> {
      Entity target = Minecraft.getInstance().player.level.getEntity(id);
      if (target instanceof MinecartEntity) {
        target.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
          for (String station : stations) {
            capability.add(station);
          }
        });
    //    LogManager.getLogger(Railways.MODID).debug("handled packet: success");
    //  } else {
    //    LogManager.getLogger(Railways.MODID).debug("handled packet: not minecart");
      }
    });
    ctx.setPacketHandled(true);
  }
}
