package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRespawnPacket implements IPacket<IClientPlayNetHandler> {
   private DimensionType dimensionID;
   /** First 8 bytes of the SHA-256 hash of the world's seed */
   private long hashedSeed;
   private GameType gameType;
   private WorldType worldType;
   private int dimensionInt;

   public SRespawnPacket() {
   }

   public SRespawnPacket(DimensionType p_i226091_1_, long p_i226091_2_, WorldType p_i226091_4_, GameType p_i226091_5_) {
      this.dimensionID = p_i226091_1_;
      this.hashedSeed = p_i226091_2_;
      this.gameType = p_i226091_5_;
      this.worldType = p_i226091_4_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleRespawn(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.dimensionInt = buf.readInt();
      this.hashedSeed = buf.readLong();
      this.gameType = GameType.getByID(buf.readUnsignedByte());
      this.worldType = WorldType.byName(buf.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.dimensionID.getId());
      buf.writeLong(this.hashedSeed);
      buf.writeByte(this.gameType.getID());
      buf.writeString(this.worldType.getName());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimension() {
      return this.dimensionID == null ? this.dimensionID = net.minecraftforge.fml.network.NetworkHooks.getDummyDimType(this.dimensionInt) : this.dimensionID;
   }

   /**
    * get value
    */
   @OnlyIn(Dist.CLIENT)
   public long getHashedSeed() {
      return this.hashedSeed;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }
}