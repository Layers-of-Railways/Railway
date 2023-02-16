package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class JukeboxCartPacket implements S2CPacket {
  final int id;
  final ItemStack record;

  public JukeboxCartPacket(Entity target, ItemStack disc) {
    id = target.getId();
    record = disc;
  }

  public JukeboxCartPacket(FriendlyByteBuf buf) {
    id = buf.readInt();
    record = buf.readItem();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeInt(this.id);
    buffer.writeItem(this.record);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc, FriendlyByteBuf buffer) {
    Level level = mc.level;
    if (level != null) {
      Entity target = level.getEntity(this.id);
      if (target instanceof MinecartJukebox juke) {
        juke.insertRecord(this.record);
      }
    }
  }
}
