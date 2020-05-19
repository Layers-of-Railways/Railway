package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMerchantOffersPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private MerchantOffers offers;
   private int level;
   private int xp;
   private boolean field_218740_e;
   private boolean field_223478_f;

   public SMerchantOffersPacket() {
   }

   public SMerchantOffersPacket(int id, MerchantOffers offersIn, int levelIn, int xpIn, boolean p_i51539_5_, boolean p_i51539_6_) {
      this.containerId = id;
      this.offers = offersIn;
      this.level = levelIn;
      this.xp = xpIn;
      this.field_218740_e = p_i51539_5_;
      this.field_223478_f = p_i51539_6_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.containerId = buf.readVarInt();
      this.offers = MerchantOffers.read(buf);
      this.level = buf.readVarInt();
      this.xp = buf.readVarInt();
      this.field_218740_e = buf.readBoolean();
      this.field_223478_f = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.containerId);
      this.offers.write(buf);
      buf.writeVarInt(this.level);
      buf.writeVarInt(this.xp);
      buf.writeBoolean(this.field_218740_e);
      buf.writeBoolean(this.field_223478_f);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleMerchantOffers(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public MerchantOffers getOffers() {
      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLevel() {
      return this.level;
   }

   @OnlyIn(Dist.CLIENT)
   public int getExp() {
      return this.xp;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_218735_f() {
      return this.field_218740_e;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_223477_g() {
      return this.field_223478_f;
   }
}