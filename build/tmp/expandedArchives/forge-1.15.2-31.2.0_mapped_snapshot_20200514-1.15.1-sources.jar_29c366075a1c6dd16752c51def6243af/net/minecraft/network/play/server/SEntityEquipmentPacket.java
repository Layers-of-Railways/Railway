package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityEquipmentPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private EquipmentSlotType equipmentSlot;
   private ItemStack itemStack = ItemStack.EMPTY;

   public SEntityEquipmentPacket() {
   }

   public SEntityEquipmentPacket(int entityIdIn, EquipmentSlotType equipmentSlotIn, ItemStack itemStackIn) {
      this.entityID = entityIdIn;
      this.equipmentSlot = equipmentSlotIn;
      this.itemStack = itemStackIn.copy();
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarInt();
      this.equipmentSlot = buf.readEnumValue(EquipmentSlotType.class);
      this.itemStack = buf.readItemStack();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityID);
      buf.writeEnumValue(this.equipmentSlot);
      buf.writeItemStack(this.itemStack);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityEquipment(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public EquipmentSlotType getEquipmentSlot() {
      return this.equipmentSlot;
   }
}