package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class MountedToolboxSyncPacket implements S2CPacket {
  final int id;
  final CompoundTag nbt;

  public MountedToolboxSyncPacket(Entity target, CompoundTag nbt) {
    this.id = target.getId();
    this.nbt = nbt;
  }

  public MountedToolboxSyncPacket(FriendlyByteBuf buf) {
    id = buf.readInt();
    nbt = buf.readNbt();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeInt(this.id);
    buffer.writeNbt(this.nbt);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc, FriendlyByteBuf buffer) {
    Level level = mc.level;
    if (level != null) {
      Entity target = level.getEntity(this.id);
      if (target instanceof ConductorEntity conductor) {
        conductor.getOrCreateToolboxHolder().read(this.nbt, true);
      }
    }
  }
}
