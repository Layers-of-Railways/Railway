package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.mixin.AccessorCarriageContraptionEntity;
import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.railwayteam.railways.multiloader.environment.Env;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CarriageContraptionEntityUpdatePacket extends SimplePacketBase {
  final int id;
  final int carriageIndex;
  final UUID trainId;

  public CarriageContraptionEntityUpdatePacket(CarriageContraptionEntity target, Train train) {
    id = target.getId();
    carriageIndex = target.carriageIndex;
    trainId = train.id;
  }

  public CarriageContraptionEntityUpdatePacket(FriendlyByteBuf buf) {
    id = buf.readInt();
    carriageIndex = buf.readInt();
    trainId = buf.readUUID();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeInt(this.id);
    buffer.writeInt(this.carriageIndex);
    buffer.writeUUID(this.trainId);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> Env.CLIENT.runIfCurrent(() -> () -> this.__handle(context)));
    context.get().setPacketHandled(true);
  }

  @Environment(EnvType.CLIENT)
  private void __handle (Supplier<NetworkEvent.Context> supplier) {
    Level level = Minecraft.getInstance().level;
    if (level != null) {
      Entity target = level.getEntity(this.id);
      if (target instanceof CarriageContraptionEntity cce) {
        cce.trainId = trainId;
        ((AccessorCarriageContraptionEntity) cce).snr_setCarriage(null);
        cce.carriageIndex = carriageIndex;
        ((AccessorCarriageContraptionEntity) cce).snr_bindCarriage();
        ((IUpdateCount) cce).snr_markUpdate();
      }
    }
  }
}
