package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JukeboxCartPacket extends SimplePacketBase {
  final int id;
  final ItemStack record;

  public JukeboxCartPacket (Entity target, ItemStack disc) {
    id = target.getId();
    record = disc;
  }

  public JukeboxCartPacket (FriendlyByteBuf buf) {
    id = buf.readInt();
    record = buf.readItem();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeInt(this.id);
    buffer.writeItemStack(this.record, false);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.__handle(context)));
    context.get().setPacketHandled(true);
  }

  @Environment(EnvType.CLIENT)
  private void __handle (Supplier<NetworkEvent.Context> supplier) {
    Level level = Minecraft.getInstance().level;
    if (level != null) {
      Entity target = level.getEntity(this.id);
      if (target instanceof MinecartJukebox juke) {
        juke.insertRecord(this.record);
      }
    }
  }
}
