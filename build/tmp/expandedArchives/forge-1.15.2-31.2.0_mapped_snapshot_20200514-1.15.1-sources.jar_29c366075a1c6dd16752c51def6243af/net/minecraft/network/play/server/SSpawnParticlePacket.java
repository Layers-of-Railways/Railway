package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnParticlePacket implements IPacket<IClientPlayNetHandler> {
   private double xCoord;
   private double yCoord;
   private double zCoord;
   private float xOffset;
   private float yOffset;
   private float zOffset;
   private float particleSpeed;
   private int particleCount;
   private boolean longDistance;
   private IParticleData particle;

   public SSpawnParticlePacket() {
   }

   public <T extends IParticleData> SSpawnParticlePacket(T p_i229960_1_, boolean p_i229960_2_, double p_i229960_3_, double p_i229960_5_, double p_i229960_7_, float p_i229960_9_, float p_i229960_10_, float p_i229960_11_, float p_i229960_12_, int p_i229960_13_) {
      this.particle = p_i229960_1_;
      this.longDistance = p_i229960_2_;
      this.xCoord = p_i229960_3_;
      this.yCoord = p_i229960_5_;
      this.zCoord = p_i229960_7_;
      this.xOffset = p_i229960_9_;
      this.yOffset = p_i229960_10_;
      this.zOffset = p_i229960_11_;
      this.particleSpeed = p_i229960_12_;
      this.particleCount = p_i229960_13_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.getByValue(buf.readInt());
      if (particletype == null) {
         particletype = ParticleTypes.BARRIER;
      }

      this.longDistance = buf.readBoolean();
      this.xCoord = buf.readDouble();
      this.yCoord = buf.readDouble();
      this.zCoord = buf.readDouble();
      this.xOffset = buf.readFloat();
      this.yOffset = buf.readFloat();
      this.zOffset = buf.readFloat();
      this.particleSpeed = buf.readFloat();
      this.particleCount = buf.readInt();
      this.particle = this.readParticle(buf, particletype);
   }

   private <T extends IParticleData> T readParticle(PacketBuffer p_199855_1_, ParticleType<T> p_199855_2_) {
      return p_199855_2_.getDeserializer().read(p_199855_2_, p_199855_1_);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      buf.writeBoolean(this.longDistance);
      buf.writeDouble(this.xCoord);
      buf.writeDouble(this.yCoord);
      buf.writeDouble(this.zCoord);
      buf.writeFloat(this.xOffset);
      buf.writeFloat(this.yOffset);
      buf.writeFloat(this.zOffset);
      buf.writeFloat(this.particleSpeed);
      buf.writeInt(this.particleCount);
      this.particle.write(buf);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLongDistance() {
      return this.longDistance;
   }

   /**
    * Gets the x coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getXCoordinate() {
      return this.xCoord;
   }

   /**
    * Gets the y coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getYCoordinate() {
      return this.yCoord;
   }

   /**
    * Gets the z coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getZCoordinate() {
      return this.zCoord;
   }

   /**
    * Gets the x coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getXOffset() {
      return this.xOffset;
   }

   /**
    * Gets the y coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getYOffset() {
      return this.yOffset;
   }

   /**
    * Gets the z coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getZOffset() {
      return this.zOffset;
   }

   /**
    * Gets the speed of the particle animation (used in client side rendering).
    */
   @OnlyIn(Dist.CLIENT)
   public float getParticleSpeed() {
      return this.particleSpeed;
   }

   /**
    * Gets the amount of particles to spawn
    */
   @OnlyIn(Dist.CLIENT)
   public int getParticleCount() {
      return this.particleCount;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getParticle() {
      return this.particle;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleParticles(this);
   }
}