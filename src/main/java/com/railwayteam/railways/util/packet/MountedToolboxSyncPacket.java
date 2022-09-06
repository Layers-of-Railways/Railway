package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.Conductor.ConductorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MountedToolboxSyncPacket {
  int id;
  CompoundTag nbt;

  public MountedToolboxSyncPacket(Entity target, CompoundTag nbt) {
    this.id = target.getId();
    this.nbt = nbt;
  }

  public MountedToolboxSyncPacket(FriendlyByteBuf buf) {
    id = buf.readInt();
    nbt = buf.readNbt();
  }

  public static void encode(MountedToolboxSyncPacket packet, FriendlyByteBuf buf) {
    buf.writeInt(packet.id);
    buf.writeNbt(packet.nbt);
  }

  public static void handle(MountedToolboxSyncPacket packet, Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(()-> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> ()-> __handle(packet, supplier));
    });
    supplier.get().setPacketHandled(true);
  }

  private static void __handle(MountedToolboxSyncPacket packet, Supplier<NetworkEvent.Context> supplier) {
    Level level = Minecraft.getInstance().level;
    if (level != null) {
      Entity target = level.getEntity(packet.id);
      if (target instanceof ConductorEntity conductor) {
        conductor.getOrCreateToolboxHolder().read(packet.nbt, true);
      }
    }
  }
}
