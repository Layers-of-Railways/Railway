package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SExplosionPacket implements IPacket<IClientPlayNetHandler> {
   private double posX;
   private double posY;
   private double posZ;
   private float strength;
   private List<BlockPos> affectedBlockPositions;
   private float motionX;
   private float motionY;
   private float motionZ;

   public SExplosionPacket() {
   }

   public SExplosionPacket(double xIn, double yIn, double zIn, float strengthIn, List<BlockPos> affectedBlockPositionsIn, Vec3d motion) {
      this.posX = xIn;
      this.posY = yIn;
      this.posZ = zIn;
      this.strength = strengthIn;
      this.affectedBlockPositions = Lists.newArrayList(affectedBlockPositionsIn);
      if (motion != null) {
         this.motionX = (float)motion.x;
         this.motionY = (float)motion.y;
         this.motionZ = (float)motion.z;
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.posX = (double)buf.readFloat();
      this.posY = (double)buf.readFloat();
      this.posZ = (double)buf.readFloat();
      this.strength = buf.readFloat();
      int i = buf.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(i);
      int j = MathHelper.floor(this.posX);
      int k = MathHelper.floor(this.posY);
      int l = MathHelper.floor(this.posZ);

      for(int i1 = 0; i1 < i; ++i1) {
         int j1 = buf.readByte() + j;
         int k1 = buf.readByte() + k;
         int l1 = buf.readByte() + l;
         this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
      }

      this.motionX = buf.readFloat();
      this.motionY = buf.readFloat();
      this.motionZ = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat((float)this.posX);
      buf.writeFloat((float)this.posY);
      buf.writeFloat((float)this.posZ);
      buf.writeFloat(this.strength);
      buf.writeInt(this.affectedBlockPositions.size());
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY);
      int k = MathHelper.floor(this.posZ);

      for(BlockPos blockpos : this.affectedBlockPositions) {
         int l = blockpos.getX() - i;
         int i1 = blockpos.getY() - j;
         int j1 = blockpos.getZ() - k;
         buf.writeByte(l);
         buf.writeByte(i1);
         buf.writeByte(j1);
      }

      buf.writeFloat(this.motionX);
      buf.writeFloat(this.motionY);
      buf.writeFloat(this.motionZ);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleExplosion(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionZ() {
      return this.motionZ;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public float getStrength() {
      return this.strength;
   }

   @OnlyIn(Dist.CLIENT)
   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}