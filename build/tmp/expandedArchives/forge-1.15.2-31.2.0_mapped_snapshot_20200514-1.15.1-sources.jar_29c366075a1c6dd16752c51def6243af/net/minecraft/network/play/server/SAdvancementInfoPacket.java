package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAdvancementInfoPacket implements IPacket<IClientPlayNetHandler> {
   private boolean firstSync;
   private Map<ResourceLocation, Advancement.Builder> advancementsToAdd;
   private Set<ResourceLocation> advancementsToRemove;
   private Map<ResourceLocation, AdvancementProgress> progressUpdates;

   public SAdvancementInfoPacket() {
   }

   public SAdvancementInfoPacket(boolean p_i47519_1_, Collection<Advancement> p_i47519_2_, Set<ResourceLocation> p_i47519_3_, Map<ResourceLocation, AdvancementProgress> p_i47519_4_) {
      this.firstSync = p_i47519_1_;
      this.advancementsToAdd = Maps.newHashMap();

      for(Advancement advancement : p_i47519_2_) {
         this.advancementsToAdd.put(advancement.getId(), advancement.copy());
      }

      this.advancementsToRemove = p_i47519_3_;
      this.progressUpdates = Maps.newHashMap(p_i47519_4_);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleAdvancementInfo(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.firstSync = buf.readBoolean();
      this.advancementsToAdd = Maps.newHashMap();
      this.advancementsToRemove = Sets.newLinkedHashSet();
      this.progressUpdates = Maps.newHashMap();
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = buf.readResourceLocation();
         Advancement.Builder advancement$builder = Advancement.Builder.readFrom(buf);
         this.advancementsToAdd.put(resourcelocation, advancement$builder);
      }

      i = buf.readVarInt();

      for(int k = 0; k < i; ++k) {
         ResourceLocation resourcelocation1 = buf.readResourceLocation();
         this.advancementsToRemove.add(resourcelocation1);
      }

      i = buf.readVarInt();

      for(int l = 0; l < i; ++l) {
         ResourceLocation resourcelocation2 = buf.readResourceLocation();
         this.progressUpdates.put(resourcelocation2, AdvancementProgress.fromNetwork(buf));
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBoolean(this.firstSync);
      buf.writeVarInt(this.advancementsToAdd.size());

      for(Entry<ResourceLocation, Advancement.Builder> entry : this.advancementsToAdd.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         Advancement.Builder advancement$builder = entry.getValue();
         buf.writeResourceLocation(resourcelocation);
         advancement$builder.writeTo(buf);
      }

      buf.writeVarInt(this.advancementsToRemove.size());

      for(ResourceLocation resourcelocation1 : this.advancementsToRemove) {
         buf.writeResourceLocation(resourcelocation1);
      }

      buf.writeVarInt(this.progressUpdates.size());

      for(Entry<ResourceLocation, AdvancementProgress> entry1 : this.progressUpdates.entrySet()) {
         buf.writeResourceLocation(entry1.getKey());
         entry1.getValue().serializeToNetwork(buf);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, Advancement.Builder> getAdvancementsToAdd() {
      return this.advancementsToAdd;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<ResourceLocation> getAdvancementsToRemove() {
      return this.advancementsToRemove;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, AdvancementProgress> getProgressUpdates() {
      return this.progressUpdates;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFirstSync() {
      return this.firstSync;
   }
}