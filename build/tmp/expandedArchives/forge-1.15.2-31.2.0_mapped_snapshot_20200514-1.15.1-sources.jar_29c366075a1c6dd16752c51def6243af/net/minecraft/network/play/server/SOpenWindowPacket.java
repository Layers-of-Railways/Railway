package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private int menuId;
   private ITextComponent title;

   public SOpenWindowPacket() {
   }

   public SOpenWindowPacket(int windowIdIn, ContainerType<?> menuIdIn, ITextComponent titleIn) {
      this.windowId = windowIdIn;
      this.menuId = Registry.MENU.getId(menuIdIn);
      this.title = titleIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readVarInt();
      this.menuId = buf.readVarInt();
      this.title = buf.readTextComponent();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.windowId);
      buf.writeVarInt(this.menuId);
      buf.writeTextComponent(this.title);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleOpenWindowPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ContainerType<?> getContainerType() {
      return Registry.MENU.getByValue(this.menuId);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getTitle() {
      return this.title;
   }
}