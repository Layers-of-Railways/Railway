package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRemoveEntityEffectPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private Effect effectId;

   public SRemoveEntityEffectPacket() {
   }

   public SRemoveEntityEffectPacket(int entityIdIn, Effect potionIn) {
      this.entityId = entityIdIn;
      this.effectId = potionIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.effectId = Effect.get(buf.readUnsignedByte());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeByte(Effect.getId(this.effectId));
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleRemoveEntityEffect(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect getPotion() {
      return this.effectId;
   }
}