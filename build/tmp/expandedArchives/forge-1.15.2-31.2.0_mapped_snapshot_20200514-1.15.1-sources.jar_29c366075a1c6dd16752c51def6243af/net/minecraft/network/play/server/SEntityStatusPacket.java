package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityStatusPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private byte logicOpcode;

   public SEntityStatusPacket() {
   }

   public SEntityStatusPacket(Entity entityIn, byte opcodeIn) {
      this.entityId = entityIn.getEntityId();
      this.logicOpcode = opcodeIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readInt();
      this.logicOpcode = buf.readByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.entityId);
      buf.writeByte(this.logicOpcode);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityStatus(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public byte getOpCode() {
      return this.logicOpcode;
   }
}