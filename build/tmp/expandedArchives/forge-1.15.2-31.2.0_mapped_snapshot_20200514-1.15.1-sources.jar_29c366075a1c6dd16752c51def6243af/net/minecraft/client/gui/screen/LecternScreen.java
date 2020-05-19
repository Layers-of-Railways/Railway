package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternScreen extends ReadBookScreen implements IHasContainer<LecternContainer> {
   private final LecternContainer field_214182_c;
   private final IContainerListener field_214183_d = new IContainerListener() {
      /**
       * update the crafting window inventory with the items in the list
       */
      public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
         LecternScreen.this.func_214175_g();
      }

      /**
       * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
       * contents of that slot.
       */
      public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
         LecternScreen.this.func_214175_g();
      }

      /**
       * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing
       * progress, and enchanting level. Normally the first int identifies which variable to update, and the second
       * contains the new value. Both are truncated to shorts in non-local SMP.
       */
      public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
         if (varToUpdate == 0) {
            LecternScreen.this.func_214176_h();
         }

      }
   };

   public LecternScreen(LecternContainer p_i51082_1_, PlayerInventory p_i51082_2_, ITextComponent p_i51082_3_) {
      this.field_214182_c = p_i51082_1_;
   }

   public LecternContainer getContainer() {
      return this.field_214182_c;
   }

   protected void init() {
      super.init();
      this.field_214182_c.addListener(this.field_214183_d);
   }

   public void onClose() {
      this.minecraft.player.closeScreen();
      super.onClose();
   }

   public void removed() {
      super.removed();
      this.field_214182_c.removeListener(this.field_214183_d);
   }

   protected void addDoneButton() {
      if (this.minecraft.player.isAllowEdit()) {
         this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("gui.done"), (p_214181_1_) -> {
            this.minecraft.displayGuiScreen((Screen)null);
         }));
         this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("lectern.take_book"), (p_214178_1_) -> {
            this.func_214179_c(3);
         }));
      } else {
         super.addDoneButton();
      }

   }

   /**
    * Moves the display back one page
    */
   protected void previousPage() {
      this.func_214179_c(1);
   }

   /**
    * Moves the display forward one page
    */
   protected void nextPage() {
      this.func_214179_c(2);
   }

   /**
    * I'm not sure why this exists. The function it calls is public and does all of the work
    */
   protected boolean showPage2(int pageNum) {
      if (pageNum != this.field_214182_c.getPage()) {
         this.func_214179_c(100 + pageNum);
         return true;
      } else {
         return false;
      }
   }

   private void func_214179_c(int p_214179_1_) {
      this.minecraft.playerController.sendEnchantPacket(this.field_214182_c.windowId, p_214179_1_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void func_214175_g() {
      ItemStack itemstack = this.field_214182_c.getBook();
      this.func_214155_a(ReadBookScreen.IBookInfo.func_216917_a(itemstack));
   }

   private void func_214176_h() {
      this.showPage(this.field_214182_c.getPage());
   }
}