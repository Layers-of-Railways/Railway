package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeCraftingListener implements IContainerListener {
   private final Minecraft mc;

   public CreativeCraftingListener(Minecraft mc) {
      this.mc = mc;
   }

   /**
    * update the crafting window inventory with the items in the list
    */
   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      this.mc.playerController.sendSlotPacket(stack, slotInd);
   }

   /**
    * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
    * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
    * value. Both are truncated to shorts in non-local SMP.
    */
   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
   }
}