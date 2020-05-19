package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STabCompletePacket implements IPacket<IClientPlayNetHandler> {
   private int transactionId;
   private Suggestions suggestions;

   public STabCompletePacket() {
   }

   public STabCompletePacket(int p_i47941_1_, Suggestions p_i47941_2_) {
      this.transactionId = p_i47941_1_;
      this.suggestions = p_i47941_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transactionId = buf.readVarInt();
      int i = buf.readVarInt();
      int j = buf.readVarInt();
      StringRange stringrange = StringRange.between(i, i + j);
      int k = buf.readVarInt();
      List<Suggestion> list = Lists.newArrayListWithCapacity(k);

      for(int l = 0; l < k; ++l) {
         String s = buf.readString(32767);
         ITextComponent itextcomponent = buf.readBoolean() ? buf.readTextComponent() : null;
         list.add(new Suggestion(stringrange, s, itextcomponent));
      }

      this.suggestions = new Suggestions(stringrange, list);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transactionId);
      buf.writeVarInt(this.suggestions.getRange().getStart());
      buf.writeVarInt(this.suggestions.getRange().getLength());
      buf.writeVarInt(this.suggestions.getList().size());

      for(Suggestion suggestion : this.suggestions.getList()) {
         buf.writeString(suggestion.getText());
         buf.writeBoolean(suggestion.getTooltip() != null);
         if (suggestion.getTooltip() != null) {
            buf.writeTextComponent(TextComponentUtils.toTextComponent(suggestion.getTooltip()));
         }
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleTabComplete(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransactionId() {
      return this.transactionId;
   }

   @OnlyIn(Dist.CLIENT)
   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}