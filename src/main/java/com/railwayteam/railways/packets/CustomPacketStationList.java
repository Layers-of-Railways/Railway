package com.railwayteam.railways.packets;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysPacketHandler;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import io.netty.util.AttributeKey;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class CustomPacketStationList extends RailwaysPacketHandler.CustomPacketBase {
  private int id;
  private String msg;

  public CustomPacketStationList() {
    this(0, "");
  }

  public CustomPacketStationList (int id, String entry) {
    this.id = id;
    msg = entry;
  }

  public CustomPacketStationList (PacketBuffer buffer) {
    id = buffer.readInt();
    msg = buffer.readString();
  }

  @Override
  public void write (PacketBuffer buf) {
    buf.writeInt(id);
    buf.writeString(msg);
  }

  @Override
  public void handle (Supplier<NetworkEvent.Context> context) {
    NetworkEvent.Context ctx = context.get();
    if (Minecraft.getInstance().player == null) LogManager.getLogger(Railways.MODID).debug("null sender");
    ctx.enqueueWork( ()-> {
      Entity target = Minecraft.getInstance().player.world.getEntityByID(id);
      if (target instanceof MinecartEntity) {
        target.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
          capability.setEntry(msg);
        });
        LogManager.getLogger(Railways.MODID).debug("handled packet: success");
      } else {
        LogManager.getLogger(Railways.MODID).debug("handled packet: not minecart");
      }
    });
    ctx.setPacketHandled(true);
  }
}
