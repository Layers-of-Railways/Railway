package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateJigsawBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos field_218790_a;
   private ResourceLocation field_218791_b;
   private ResourceLocation field_218792_c;
   private String field_218793_d;

   public CUpdateJigsawBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateJigsawBlockPacket(BlockPos p_i50757_1_, ResourceLocation p_i50757_2_, ResourceLocation p_i50757_3_, String p_i50757_4_) {
      this.field_218790_a = p_i50757_1_;
      this.field_218791_b = p_i50757_2_;
      this.field_218792_c = p_i50757_3_;
      this.field_218793_d = p_i50757_4_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_218790_a = buf.readBlockPos();
      this.field_218791_b = buf.readResourceLocation();
      this.field_218792_c = buf.readResourceLocation();
      this.field_218793_d = buf.readString(32767);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.field_218790_a);
      buf.writeResourceLocation(this.field_218791_b);
      buf.writeResourceLocation(this.field_218792_c);
      buf.writeString(this.field_218793_d);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.func_217262_a(this);
   }

   public BlockPos func_218789_b() {
      return this.field_218790_a;
   }

   public ResourceLocation func_218786_c() {
      return this.field_218792_c;
   }

   public ResourceLocation func_218787_d() {
      return this.field_218791_b;
   }

   public String func_218788_e() {
      return this.field_218793_d;
   }
}