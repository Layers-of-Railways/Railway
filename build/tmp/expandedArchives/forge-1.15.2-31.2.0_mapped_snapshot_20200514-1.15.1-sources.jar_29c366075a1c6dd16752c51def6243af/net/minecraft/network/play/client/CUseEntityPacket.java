package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUseEntityPacket implements IPacket<IServerPlayNetHandler> {
   private int entityId;
   private CUseEntityPacket.Action action;
   private Vec3d hitVec;
   private Hand hand;

   public CUseEntityPacket() {
   }

   public CUseEntityPacket(Entity entityIn) {
      this.entityId = entityIn.getEntityId();
      this.action = CUseEntityPacket.Action.ATTACK;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity entityIn, Hand handIn) {
      this.entityId = entityIn.getEntityId();
      this.action = CUseEntityPacket.Action.INTERACT;
      this.hand = handIn;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity entityIn, Hand handIn, Vec3d hitVecIn) {
      this.entityId = entityIn.getEntityId();
      this.action = CUseEntityPacket.Action.INTERACT_AT;
      this.hand = handIn;
      this.hitVec = hitVecIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.action = buf.readEnumValue(CUseEntityPacket.Action.class);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.hitVec = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.hand = buf.readEnumValue(Hand.class);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeEnumValue(this.action);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         buf.writeFloat((float)this.hitVec.x);
         buf.writeFloat((float)this.hitVec.y);
         buf.writeFloat((float)this.hitVec.z);
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         buf.writeEnumValue(this.hand);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processUseEntity(this);
   }

   @Nullable
   public Entity getEntityFromWorld(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   public CUseEntityPacket.Action getAction() {
      return this.action;
   }

   public Hand getHand() {
      return this.hand;
   }

   public Vec3d getHitVec() {
      return this.hitVec;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}