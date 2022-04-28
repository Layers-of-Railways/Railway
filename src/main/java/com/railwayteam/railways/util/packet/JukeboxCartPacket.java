package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JukeboxCartPacket {
  int id;
  ItemStack record;

  public JukeboxCartPacket (Entity target, ItemStack disc) {
    id = target.getId();
    record = disc;
  }

  public JukeboxCartPacket (FriendlyByteBuf buf) {
    id = buf.readInt();
    record = buf.readItem();
  }

  public static void encode (JukeboxCartPacket packet, FriendlyByteBuf buf) {
    buf.writeInt(packet.id);
    buf.writeItemStack(packet.record, false);
  }

  public static void handle (JukeboxCartPacket packet, Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(()-> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> ()-> __handle(packet, supplier));
    });
    supplier.get().setPacketHandled(true);
  }

  private static void __handle (JukeboxCartPacket packet, Supplier<NetworkEvent.Context> supplier) {
    Level level = Minecraft.getInstance().level;
    if (level != null) {
      Entity target = level.getEntity(packet.id);
      if (target instanceof MinecartJukebox juke) {
        juke.insertRecord(packet.record);
      }
    }
  }
}
