package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityPropertiesPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private final List<SEntityPropertiesPacket.Snapshot> snapshots = Lists.newArrayList();

   public SEntityPropertiesPacket() {
   }

   public SEntityPropertiesPacket(int entityIdIn, Collection<IAttributeInstance> instances) {
      this.entityId = entityIdIn;

      for(IAttributeInstance iattributeinstance : instances) {
         this.snapshots.add(new SEntityPropertiesPacket.Snapshot(iattributeinstance.getAttribute().getName(), iattributeinstance.getBaseValue(), iattributeinstance.func_225505_c_()));
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      int i = buf.readInt();

      for(int j = 0; j < i; ++j) {
         String s = buf.readString(64);
         double d0 = buf.readDouble();
         List<AttributeModifier> list = Lists.newArrayList();
         int k = buf.readVarInt();

         for(int l = 0; l < k; ++l) {
            UUID uuid = buf.readUniqueId();
            list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", buf.readDouble(), AttributeModifier.Operation.byId(buf.readByte())));
         }

         this.snapshots.add(new SEntityPropertiesPacket.Snapshot(s, d0, list));
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeInt(this.snapshots.size());

      for(SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot : this.snapshots) {
         buf.writeString(sentitypropertiespacket$snapshot.getName());
         buf.writeDouble(sentitypropertiespacket$snapshot.getBaseValue());
         buf.writeVarInt(sentitypropertiespacket$snapshot.getModifiers().size());

         for(AttributeModifier attributemodifier : sentitypropertiespacket$snapshot.getModifiers()) {
            buf.writeUniqueId(attributemodifier.getID());
            buf.writeDouble(attributemodifier.getAmount());
            buf.writeByte(attributemodifier.getOperation().getId());
         }
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityProperties(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<SEntityPropertiesPacket.Snapshot> getSnapshots() {
      return this.snapshots;
   }

   public class Snapshot {
      private final String name;
      private final double baseValue;
      private final Collection<AttributeModifier> modifiers;

      public Snapshot(String nameIn, double baseValueIn, Collection<AttributeModifier> modifiersIn) {
         this.name = nameIn;
         this.baseValue = baseValueIn;
         this.modifiers = modifiersIn;
      }

      public String getName() {
         return this.name;
      }

      public double getBaseValue() {
         return this.baseValue;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}