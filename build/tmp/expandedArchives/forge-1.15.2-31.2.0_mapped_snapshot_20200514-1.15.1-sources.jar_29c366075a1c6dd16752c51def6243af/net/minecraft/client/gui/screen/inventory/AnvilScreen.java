package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilScreen extends ContainerScreen<RepairContainer> implements IContainerListener {
   private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
   private TextFieldWidget nameField;

   public AnvilScreen(RepairContainer p_i51103_1_, PlayerInventory p_i51103_2_, ITextComponent p_i51103_3_) {
      super(p_i51103_1_, p_i51103_2_, p_i51103_3_);
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.nameField = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, I18n.format("container.repair"));
      this.nameField.setCanLoseFocus(false);
      this.nameField.changeFocus(true);
      this.nameField.setTextColor(-1);
      this.nameField.setDisabledTextColour(-1);
      this.nameField.setEnableBackgroundDrawing(false);
      this.nameField.setMaxStringLength(35);
      this.nameField.setResponder(this::func_214075_a);
      this.children.add(this.nameField);
      this.container.addListener(this);
      this.setFocusedDefault(this.nameField);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.nameField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameField.setText(s);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardListener.enableRepeatEvents(false);
      this.container.removeListener(this);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.minecraft.player.closeScreen();
      }

      return !this.nameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.nameField.canWrite() ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      RenderSystem.disableBlend();
      this.font.drawString(this.title.getFormattedText(), 60.0F, 6.0F, 4210752);
      int i = this.container.getMaximumCost();
      if (i > 0) {
         int j = 8453920;
         boolean flag = true;
         String s = I18n.format("container.repair.cost", i);
         if (i >= 40 && !this.minecraft.player.abilities.isCreativeMode) {
            s = I18n.format("container.repair.expensive");
            j = 16736352;
         } else if (!this.container.getSlot(2).getHasStack()) {
            flag = false;
         } else if (!this.container.getSlot(2).canTakeStack(this.playerInventory.player)) {
            j = 16736352;
         }

         if (flag) {
            int k = this.xSize - 8 - this.font.getStringWidth(s) - 2;
            int l = 69;
            fill(k - 2, 67, this.xSize - 8, 79, 1325400064);
            this.font.drawStringWithShadow(s, (float)k, 69.0F, j);
         }
      }

   }

   private void func_214075_a(String p_214075_1_) {
      if (!p_214075_1_.isEmpty()) {
         String s = p_214075_1_;
         Slot slot = this.container.getSlot(0);
         if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && p_214075_1_.equals(slot.getStack().getDisplayName().getString())) {
            s = "";
         }

         this.container.updateItemName(s);
         this.minecraft.player.connection.sendPacket(new CRenameItemPacket(s));
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      RenderSystem.disableBlend();
      this.nameField.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(ANVIL_RESOURCE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      this.blit(i + 59, j + 20, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
      if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack()) {
         this.blit(i + 99, j + 45, this.xSize, 0, 28, 21);
      }

   }

   /**
    * update the crafting window inventory with the items in the list
    */
   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
      this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (slotInd == 0) {
         this.nameField.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
         this.nameField.setEnabled(!stack.isEmpty());
      }

   }

   /**
    * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
    * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
    * value. Both are truncated to shorts in non-local SMP.
    */
   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
   }
}