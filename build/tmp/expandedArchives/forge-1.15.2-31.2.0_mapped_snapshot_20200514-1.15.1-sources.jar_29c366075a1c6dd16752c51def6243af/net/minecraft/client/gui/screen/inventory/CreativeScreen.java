package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.HotbarSnapshot;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeScreen extends DisplayEffectsScreen<CreativeScreen.CreativeContainer> {
   /** The location of the creative inventory tabs texture */
   private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final Inventory TMP_INVENTORY = new Inventory(45);
   /** Currently selected creative inventory tab index. */
   private static int selectedTabIndex = ItemGroup.BUILDING_BLOCKS.getIndex();
   /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
   private float currentScroll;
   /** True if the scrollbar is being dragged */
   private boolean isScrolling;
   private TextFieldWidget searchField;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeCraftingListener listener;
   private boolean field_195377_F;
   private boolean field_199506_G;
   private final Map<ResourceLocation, Tag<Item>> tagSearchResults = Maps.newTreeMap();
   private static int tabPage = 0;
   private int maxPages = 0;

   public CreativeScreen(PlayerEntity player) {
      super(new CreativeScreen.CreativeContainer(player), player.inventory, new StringTextComponent(""));
      player.openContainer = this.container;
      this.passEvents = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   public void tick() {
      if (!this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchField != null) {
         this.searchField.tick();
      }

   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void handleMouseClick(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type) {
      if (this.hasTmpInventory(slotIn)) {
         this.searchField.setCursorPositionEnd();
         this.searchField.setSelectionPos(0);
      }

      boolean flag = type == ClickType.QUICK_MOVE;
      type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
      if (slotIn == null && selectedTabIndex != ItemGroup.INVENTORY.getIndex() && type != ClickType.QUICK_CRAFT) {
         PlayerInventory playerinventory1 = this.minecraft.player.inventory;
         if (!playerinventory1.getItemStack().isEmpty() && this.field_199506_G) {
            if (mouseButton == 0) {
               this.minecraft.player.dropItem(playerinventory1.getItemStack(), true);
               this.minecraft.playerController.sendPacketDropItem(playerinventory1.getItemStack());
               playerinventory1.setItemStack(ItemStack.EMPTY);
            }

            if (mouseButton == 1) {
               ItemStack itemstack6 = playerinventory1.getItemStack().split(1);
               this.minecraft.player.dropItem(itemstack6, true);
               this.minecraft.playerController.sendPacketDropItem(itemstack6);
            }
         }
      } else {
         if (slotIn != null && !slotIn.canTakeStack(this.minecraft.player)) {
            return;
         }

         if (slotIn == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.minecraft.player.container.getInventory().size(); ++j) {
               this.minecraft.playerController.sendSlotPacket(ItemStack.EMPTY, j);
            }
         } else if (selectedTabIndex == ItemGroup.INVENTORY.getIndex()) {
            if (slotIn == this.destroyItemSlot) {
               this.minecraft.player.inventory.setItemStack(ItemStack.EMPTY);
            } else if (type == ClickType.THROW && slotIn != null && slotIn.getHasStack()) {
               ItemStack itemstack = slotIn.decrStackSize(mouseButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
               ItemStack itemstack1 = slotIn.getStack();
               this.minecraft.player.dropItem(itemstack, true);
               this.minecraft.playerController.sendPacketDropItem(itemstack);
               this.minecraft.playerController.sendSlotPacket(itemstack1, ((CreativeScreen.CreativeSlot)slotIn).slot.slotNumber);
            } else if (type == ClickType.THROW && !this.minecraft.player.inventory.getItemStack().isEmpty()) {
               this.minecraft.player.dropItem(this.minecraft.player.inventory.getItemStack(), true);
               this.minecraft.playerController.sendPacketDropItem(this.minecraft.player.inventory.getItemStack());
               this.minecraft.player.inventory.setItemStack(ItemStack.EMPTY);
            } else {
               this.minecraft.player.container.slotClick(slotIn == null ? slotId : ((CreativeScreen.CreativeSlot)slotIn).slot.slotNumber, mouseButton, type, this.minecraft.player);
               this.minecraft.player.container.detectAndSendChanges();
            }
         } else if (type != ClickType.QUICK_CRAFT && slotIn.inventory == TMP_INVENTORY) {
            PlayerInventory playerinventory = this.minecraft.player.inventory;
            ItemStack itemstack5 = playerinventory.getItemStack();
            ItemStack itemstack7 = slotIn.getStack();
            if (type == ClickType.SWAP) {
               if (!itemstack7.isEmpty() && mouseButton >= 0 && mouseButton < 9) {
                  ItemStack itemstack10 = itemstack7.copy();
                  itemstack10.setCount(itemstack10.getMaxStackSize());
                  this.minecraft.player.inventory.setInventorySlotContents(mouseButton, itemstack10);
                  this.minecraft.player.container.detectAndSendChanges();
               }

               return;
            }

            if (type == ClickType.CLONE) {
               if (playerinventory.getItemStack().isEmpty() && slotIn.getHasStack()) {
                  ItemStack itemstack9 = slotIn.getStack().copy();
                  itemstack9.setCount(itemstack9.getMaxStackSize());
                  playerinventory.setItemStack(itemstack9);
               }

               return;
            }

            if (type == ClickType.THROW) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack8 = itemstack7.copy();
                  itemstack8.setCount(mouseButton == 0 ? 1 : itemstack8.getMaxStackSize());
                  this.minecraft.player.dropItem(itemstack8, true);
                  this.minecraft.playerController.sendPacketDropItem(itemstack8);
               }

               return;
            }

            if (!itemstack5.isEmpty() && !itemstack7.isEmpty() && itemstack5.isItemEqual(itemstack7) && ItemStack.areItemStackTagsEqual(itemstack5, itemstack7)) {
               if (mouseButton == 0) {
                  if (flag) {
                     itemstack5.setCount(itemstack5.getMaxStackSize());
                  } else if (itemstack5.getCount() < itemstack5.getMaxStackSize()) {
                     itemstack5.grow(1);
                  }
               } else {
                  itemstack5.shrink(1);
               }
            } else if (!itemstack7.isEmpty() && itemstack5.isEmpty()) {
               playerinventory.setItemStack(itemstack7.copy());
               itemstack5 = playerinventory.getItemStack();
               if (flag) {
                  itemstack5.setCount(itemstack5.getMaxStackSize());
               }
            } else if (mouseButton == 0) {
               playerinventory.setItemStack(ItemStack.EMPTY);
            } else {
               playerinventory.getItemStack().shrink(1);
            }
         } else if (this.container != null) {
            ItemStack itemstack3 = slotIn == null ? ItemStack.EMPTY : this.container.getSlot(slotIn.slotNumber).getStack();
            this.container.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, this.minecraft.player);
            if (Container.getDragEvent(mouseButton) == 2) {
               for(int k = 0; k < 9; ++k) {
                  this.minecraft.playerController.sendSlotPacket(this.container.getSlot(45 + k).getStack(), 36 + k);
               }
            } else if (slotIn != null) {
               ItemStack itemstack4 = this.container.getSlot(slotIn.slotNumber).getStack();
               this.minecraft.playerController.sendSlotPacket(itemstack4, slotIn.slotNumber - (this.container).inventorySlots.size() + 9 + 36);
               int i = 45 + mouseButton;
               if (type == ClickType.SWAP) {
                  this.minecraft.playerController.sendSlotPacket(itemstack3, i - (this.container).inventorySlots.size() + 9 + 36);
               } else if (type == ClickType.THROW && !itemstack3.isEmpty()) {
                  ItemStack itemstack2 = itemstack3.copy();
                  itemstack2.setCount(mouseButton == 0 ? 1 : itemstack2.getMaxStackSize());
                  this.minecraft.player.dropItem(itemstack2, true);
                  this.minecraft.playerController.sendPacketDropItem(itemstack2);
               }

               this.minecraft.player.container.detectAndSendChanges();
            }
         }
      }

   }

   private boolean hasTmpInventory(@Nullable Slot slotIn) {
      return slotIn != null && slotIn.inventory == TMP_INVENTORY;
   }

   protected void updateActivePotionEffects() {
      int i = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != i) {
         this.searchField.setX(this.guiLeft + 82);
      }

   }

   protected void init() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         super.init();
         this.minecraft.keyboardListener.enableRepeatEvents(true);
         int tabCount = ItemGroup.GROUPS.length;
         if (tabCount > 12) {
            addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft,              guiTop - 50, 20, 20, "<", b -> tabPage = Math.max(tabPage - 1, 0       )));
            addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft + xSize - 20, guiTop - 50, 20, 20, ">", b -> tabPage = Math.min(tabPage + 1, maxPages)));
            maxPages = (int) Math.ceil((tabCount - 12) / 10D);
         }
         this.searchField = new TextFieldWidget(this.font, this.guiLeft + 82, this.guiTop + 6, 80, 9, I18n.format("itemGroup.search"));
         this.searchField.setMaxStringLength(50);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         this.children.add(this.searchField);
         int i = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(ItemGroup.GROUPS[i]);
         this.minecraft.player.container.removeListener(this.listener);
         this.listener = new CreativeCraftingListener(this.minecraft);
         this.minecraft.player.container.addListener(this.listener);
      } else {
         this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.searchField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.searchField.setText(s);
      if (!this.searchField.getText().isEmpty()) {
         this.updateCreativeSearch();
      }

   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
         this.minecraft.player.container.removeListener(this.listener);
      }

      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.field_195377_F) {
         return false;
      } else if (!ItemGroup.GROUPS[selectedTabIndex].hasSearchBar()) {
         return false;
      } else {
         String s = this.searchField.getText();
         if (this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            if (!Objects.equals(s, this.searchField.getText())) {
               this.updateCreativeSearch();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      this.field_195377_F = false;
      if (!ItemGroup.GROUPS[selectedTabIndex].hasSearchBar()) {
         if (this.minecraft.gameSettings.keyBindChat.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            this.setCurrentCreativeTab(ItemGroup.SEARCH);
            return true;
         } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         boolean flag = !this.hasTmpInventory(this.hoveredSlot) || this.hoveredSlot != null && this.hoveredSlot.getHasStack();
         if (flag && this.func_195363_d(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            return true;
         } else {
            String s = this.searchField.getText();
            if (this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
               if (!Objects.equals(s, this.searchField.getText())) {
                  this.updateCreativeSearch();
               }

               return true;
            } else {
               return this.searchField.isFocused() && this.searchField.getVisible() && p_keyPressed_1_ != 256 ? true : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
            }
         }
      }
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      this.field_195377_F = false;
      return super.keyReleased(keyCode, scanCode, modifiers);
   }

   private void updateCreativeSearch() {
      (this.container).itemList.clear();
      this.tagSearchResults.clear();

      ItemGroup tab = ItemGroup.GROUPS[selectedTabIndex];
      if (tab.hasSearchBar() && tab != ItemGroup.SEARCH) {
         tab.fill(container.itemList);
         if (!this.searchField.getText().isEmpty()) {
            //TODO: Make this a SearchTree not a manual search
            String search = this.searchField.getText().toLowerCase(Locale.ROOT);
            java.util.Iterator<ItemStack> itr = container.itemList.iterator();
            while (itr.hasNext()) {
               ItemStack stack = itr.next();
               boolean matches = false;
               for (ITextComponent line : stack.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL)) {
                  if (TextFormatting.getTextWithoutFormattingCodes(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                     matches = true;
                     break;
                  }
               }
               if (!matches)
                  itr.remove();
            }
         }
         this.currentScroll = 0.0F;
         container.scrollTo(0.0F);
         return;
      }

      String s = this.searchField.getText();
      if (s.isEmpty()) {
         for(Item item : Registry.ITEM) {
            item.fillItemGroup(ItemGroup.SEARCH, (this.container).itemList);
         }
      } else {
         ISearchTree<ItemStack> isearchtree;
         if (s.startsWith("#")) {
            s = s.substring(1);
            isearchtree = this.minecraft.getSearchTree(SearchTreeManager.TAGS);
            this.searchTags(s);
         } else {
            isearchtree = this.minecraft.getSearchTree(SearchTreeManager.ITEMS);
         }

         (this.container).itemList.addAll(isearchtree.search(s.toLowerCase(Locale.ROOT)));
      }

      this.currentScroll = 0.0F;
      this.container.scrollTo(0.0F);
   }

   private void searchTags(String search) {
      int i = search.indexOf(58);
      Predicate<ResourceLocation> predicate;
      if (i == -1) {
         predicate = (p_214084_1_) -> {
            return p_214084_1_.getPath().contains(search);
         };
      } else {
         String s = search.substring(0, i).trim();
         String s1 = search.substring(i + 1).trim();
         predicate = (p_214081_2_) -> {
            return p_214081_2_.getNamespace().contains(s) && p_214081_2_.getPath().contains(s1);
         };
      }

      TagCollection<Item> tagcollection = ItemTags.getCollection();
      tagcollection.getRegisteredTags().stream().filter(predicate).forEach((p_214082_2_) -> {
         Tag tag = this.tagSearchResults.put(p_214082_2_, tagcollection.get(p_214082_2_));
      });
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];
      if (itemgroup != null && itemgroup.drawInForegroundOfTab()) {
         RenderSystem.disableBlend();
         this.font.drawString(I18n.format(itemgroup.getTranslationKey()), 8.0F, 6.0F, itemgroup.getLabelColor());
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         double d0 = p_mouseClicked_1_ - (double)this.guiLeft;
         double d1 = p_mouseClicked_3_ - (double)this.guiTop;

         for(ItemGroup itemgroup : ItemGroup.GROUPS) {
            if (itemgroup != null && this.isMouseOverGroup(itemgroup, d0, d1)) {
               return true;
            }
         }

         if (selectedTabIndex != ItemGroup.INVENTORY.getIndex() && this.func_195376_a(p_mouseClicked_1_, p_mouseClicked_3_)) {
            this.isScrolling = this.needsScrollBars();
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0) {
         double d0 = p_mouseReleased_1_ - (double)this.guiLeft;
         double d1 = p_mouseReleased_3_ - (double)this.guiTop;
         this.isScrolling = false;

         for(ItemGroup itemgroup : ItemGroup.GROUPS) {
            if (itemgroup != null && this.isMouseOverGroup(itemgroup, d0, d1)) {
               this.setCurrentCreativeTab(itemgroup);
               return true;
            }
         }
      }

      return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   /**
    * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of items)
    */
   private boolean needsScrollBars() {
      if (ItemGroup.GROUPS[selectedTabIndex] == null) return false;
      return selectedTabIndex != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTabIndex].hasScrollbar() && this.container.canScroll();
   }

   /**
    * Sets the current creative tab, restructuring the GUI as needed.
    */
   private void setCurrentCreativeTab(ItemGroup tab) {
      if (tab == null) return;
      int i = selectedTabIndex;
      selectedTabIndex = tab.getIndex();
      slotColor = tab.getSlotColor();
      this.dragSplittingSlots.clear();
      (this.container).itemList.clear();
      if (tab == ItemGroup.HOTBAR) {
         CreativeSettings creativesettings = this.minecraft.getCreativeSettings();

         for(int j = 0; j < 9; ++j) {
            HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(j);
            if (hotbarsnapshot.isEmpty()) {
               for(int k = 0; k < 9; ++k) {
                  if (k == j) {
                     ItemStack itemstack = new ItemStack(Items.PAPER);
                     itemstack.getOrCreateChildTag("CustomCreativeLock");
                     String s = this.minecraft.gameSettings.keyBindsHotbar[j].getLocalizedName();
                     String s1 = this.minecraft.gameSettings.keyBindSaveToolbar.getLocalizedName();
                     itemstack.setDisplayName(new TranslationTextComponent("inventory.hotbarInfo", s1, s));
                     (this.container).itemList.add(itemstack);
                  } else {
                     (this.container).itemList.add(ItemStack.EMPTY);
                  }
               }
            } else {
               (this.container).itemList.addAll(hotbarsnapshot);
            }
         }
      } else if (tab != ItemGroup.SEARCH) {
         tab.fill((this.container).itemList);
      }

      if (tab == ItemGroup.INVENTORY) {
         Container container = this.minecraft.player.container;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf((this.container).inventorySlots);
         }

         (this.container).inventorySlots.clear();

         for(int l = 0; l < container.inventorySlots.size(); ++l) {
            int i1;
            int j1;
            if (l >= 5 && l < 9) {
               int l1 = l - 5;
               int j2 = l1 / 2;
               int l2 = l1 % 2;
               i1 = 54 + j2 * 54;
               j1 = 6 + l2 * 27;
            } else if (l >= 0 && l < 5) {
               i1 = -2000;
               j1 = -2000;
            } else if (l == 45) {
               i1 = 35;
               j1 = 20;
            } else {
               int k1 = l - 9;
               int i2 = k1 % 9;
               int k2 = k1 / 9;
               i1 = 9 + i2 * 18;
               if (l >= 36) {
                  j1 = 112;
               } else {
                  j1 = 54 + k2 * 18;
               }
            }

            Slot slot = new CreativeScreen.CreativeSlot(container.inventorySlots.get(l), l, i1, j1);
            (this.container).inventorySlots.add(slot);
         }

         this.destroyItemSlot = new Slot(TMP_INVENTORY, 0, 173, 112);
         (this.container).inventorySlots.add(this.destroyItemSlot);
      } else if (i == ItemGroup.INVENTORY.getIndex()) {
         (this.container).inventorySlots.clear();
         (this.container).inventorySlots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (this.searchField != null) {
         if (tab.hasSearchBar()) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused2(true);
            if (i != tab.getIndex()) {
               this.searchField.setText("");
            }
            this.searchField.setWidth(tab.getSearchbarWidth());
            this.searchField.x = this.guiLeft + (82 /*default left*/ + 89 /*default width*/) - this.searchField.getWidth();

            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused2(false);
            this.searchField.setText("");
         }
      }

      this.currentScroll = 0.0F;
      this.container.scrollTo(0.0F);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (!this.needsScrollBars()) {
         return false;
      } else {
         int i = ((this.container).itemList.size() + 9 - 1) / 9 - 5;
         this.currentScroll = (float)((double)this.currentScroll - p_mouseScrolled_5_ / (double)i);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         this.container.scrollTo(this.currentScroll);
         return true;
      }
   }

   protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
      boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
      this.field_199506_G = flag && !this.isMouseOverGroup(ItemGroup.GROUPS[selectedTabIndex], mouseX, mouseY);
      return this.field_199506_G;
   }

   protected boolean func_195376_a(double p_195376_1_, double p_195376_3_) {
      int i = this.guiLeft;
      int j = this.guiTop;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_195376_1_ >= (double)k && p_195376_3_ >= (double)l && p_195376_1_ < (double)i1 && p_195376_3_ < (double)j1;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.isScrolling) {
         int i = this.guiTop + 18;
         int j = i + 112;
         this.currentScroll = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         this.container.scrollTo(this.currentScroll);
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);

      int start = tabPage * 10;
      int end = Math.min(ItemGroup.GROUPS.length, ((tabPage + 1) * 10) + 2);
      if (tabPage != 0) start += 2;
      boolean rendered = false;

      for (int x = start; x < end; x++) {
         ItemGroup itemgroup = ItemGroup.GROUPS[x];
         if (itemgroup != null && this.renderCreativeInventoryHoveringText(itemgroup, p_render_1_, p_render_2_)) {
            rendered = true;
            break;
         }
      }
      if (!rendered && !renderCreativeInventoryHoveringText(ItemGroup.SEARCH, p_render_1_, p_render_2_))
         renderCreativeInventoryHoveringText(ItemGroup.INVENTORY, p_render_1_, p_render_2_);

      if (this.destroyItemSlot != null && selectedTabIndex == ItemGroup.INVENTORY.getIndex() && this.isPointInRegion(this.destroyItemSlot.xPos, this.destroyItemSlot.yPos, 16, 16, (double)p_render_1_, (double)p_render_2_)) {
         this.renderTooltip(I18n.format("inventory.binSlot"), p_render_1_, p_render_2_);
      }

      if (maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
         RenderSystem.disableLighting();
         this.setBlitOffset(300);
         this.itemRenderer.zLevel = 300.0F;
         font.drawString(page, guiLeft + (xSize / 2) - (font.getStringWidth(page) / 2), guiTop - 44, -1);
         this.setBlitOffset(0);
         this.itemRenderer.zLevel = 0.0F;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      if (selectedTabIndex == ItemGroup.SEARCH.getIndex()) {
         List<ITextComponent> list = p_renderTooltip_1_.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
         List<String> list1 = Lists.newArrayListWithCapacity(list.size());

         for(ITextComponent itextcomponent : list) {
            list1.add(itextcomponent.getFormattedText());
         }

         Item item = p_renderTooltip_1_.getItem();
         ItemGroup itemgroup1 = item.getGroup();
         if (itemgroup1 == null && item == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_renderTooltip_1_);
            if (map.size() == 1) {
               Enchantment enchantment = map.keySet().iterator().next();

               for(ItemGroup itemgroup : ItemGroup.GROUPS) {
                  if (itemgroup.hasRelevantEnchantmentType(enchantment.type)) {
                     itemgroup1 = itemgroup;
                     break;
                  }
               }
            }
         }

         this.tagSearchResults.forEach((p_214083_2_, p_214083_3_) -> {
            if (p_214083_3_.contains(item)) {
               list1.add(1, "" + TextFormatting.BOLD + TextFormatting.DARK_PURPLE + "#" + p_214083_2_);
            }

         });
         if (itemgroup1 != null) {
            list1.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.format(itemgroup1.getTranslationKey()));
         }

         for(int i = 0; i < list1.size(); ++i) {
            if (i == 0) {
               list1.set(i, p_renderTooltip_1_.getRarity().color + (String)list1.get(i));
            } else {
               list1.set(i, TextFormatting.GRAY + (String)list1.get(i));
            }
         }

         net.minecraft.client.gui.FontRenderer font = p_renderTooltip_1_.getItem().getFontRenderer(p_renderTooltip_1_);
         net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(p_renderTooltip_1_);
         this.renderTooltip(list1, p_renderTooltip_2_, p_renderTooltip_3_, (font == null ? this.font : font));
         net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
      } else {
         super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
      }

   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];

      int start = tabPage * 10;
      int end = Math.min(ItemGroup.GROUPS.length, ((tabPage + 1) * 10 + 2));
      if (tabPage != 0) start += 2;

      for (int idx = start; idx < end; idx++) {
         ItemGroup itemgroup1 = ItemGroup.GROUPS[idx];
         if (itemgroup1 != null && itemgroup1.getIndex() != selectedTabIndex) {
            this.minecraft.getTextureManager().bindTexture(itemgroup1.getTabsImage());
            this.drawTab(itemgroup1);
         }
      }

      if (tabPage != 0) {
         if (itemgroup != ItemGroup.SEARCH) {
            this.minecraft.getTextureManager().bindTexture(ItemGroup.SEARCH.getTabsImage());
            drawTab(ItemGroup.SEARCH);
         }
         if (itemgroup != ItemGroup.INVENTORY) {
            this.minecraft.getTextureManager().bindTexture(ItemGroup.INVENTORY.getTabsImage());
            drawTab(ItemGroup.INVENTORY);
         }
      }

      this.minecraft.getTextureManager().bindTexture(itemgroup.getBackgroundImage());
      this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.render(mouseX, mouseY, partialTicks);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.guiLeft + 175;
      int j = this.guiTop + 18;
      int k = j + 112;
      this.minecraft.getTextureManager().bindTexture(itemgroup.getTabsImage());
      if (itemgroup.hasScrollbar()) {
         this.blit(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      if ((itemgroup == null || itemgroup.getTabPage() != tabPage) && (itemgroup != ItemGroup.SEARCH && itemgroup != ItemGroup.INVENTORY))
         return;

      this.drawTab(itemgroup);
      if (itemgroup == ItemGroup.INVENTORY) {
         InventoryScreen.drawEntityOnScreen(this.guiLeft + 88, this.guiTop + 45, 20, (float)(this.guiLeft + 88 - mouseX), (float)(this.guiTop + 45 - 30 - mouseY), this.minecraft.player);
      }

   }

   protected boolean isMouseOverGroup(ItemGroup p_195375_1_, double p_195375_2_, double p_195375_4_) {
      if (p_195375_1_.getTabPage() != tabPage && p_195375_1_ != ItemGroup.SEARCH && p_195375_1_ != ItemGroup.INVENTORY) return false;
      int i = p_195375_1_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_195375_1_.isAlignedRight()) {
         j = this.xSize - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_195375_1_.isOnTopRow()) {
         k = k - 32;
      } else {
         k = k + this.ySize;
      }

      return p_195375_2_ >= (double)j && p_195375_2_ <= (double)(j + 28) && p_195375_4_ >= (double)k && p_195375_4_ <= (double)(k + 32);
   }

   /**
    * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
    * Params: current creative tab to be checked, current mouse x position, current mouse y position.
    */
   protected boolean renderCreativeInventoryHoveringText(ItemGroup tab, int mouseX, int mouseY) {
      int i = tab.getColumn();
      int j = 28 * i;
      int k = 0;
      if (tab.isAlignedRight()) {
         j = this.xSize - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (tab.isOnTopRow()) {
         k = k - 32;
      } else {
         k = k + this.ySize;
      }

      if (this.isPointInRegion(j + 3, k + 3, 23, 27, (double)mouseX, (double)mouseY)) {
         this.renderTooltip(I18n.format(tab.getTranslationKey()), mouseX, mouseY);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Draws the given tab and its background, deciding whether to highlight the tab or not based off of the selected
    * index.
    */
   protected void drawTab(ItemGroup tab) {
      boolean flag = tab.getIndex() == selectedTabIndex;
      boolean flag1 = tab.isOnTopRow();
      int i = tab.getColumn();
      int j = i * 28;
      int k = 0;
      int l = this.guiLeft + 28 * i;
      int i1 = this.guiTop;
      int j1 = 32;
      if (flag) {
         k += 32;
      }

      if (tab.isAlignedRight()) {
         l = this.guiLeft + this.xSize - 28 * (6 - i);
      } else if (i > 0) {
         l += i;
      }

      if (flag1) {
         i1 = i1 - 28;
      } else {
         k += 64;
         i1 = i1 + (this.ySize - 4);
      }

      RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
      RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
      this.blit(l, i1, j, k, 28, 32);
      this.setBlitOffset(100);
      this.itemRenderer.zLevel = 100.0F;
      l = l + 6;
      i1 = i1 + 8 + (flag1 ? 1 : -1);
      RenderSystem.enableRescaleNormal();
      ItemStack itemstack = tab.getIcon();
      this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, l, i1);
      this.itemRenderer.renderItemOverlays(this.font, itemstack, l, i1);
      this.itemRenderer.zLevel = 0.0F;
      this.setBlitOffset(0);
   }

   /**
    * Returns the index of the currently selected tab.
    */
   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   public static void handleHotbarSnapshots(Minecraft client, int index, boolean load, boolean save) {
      ClientPlayerEntity clientplayerentity = client.player;
      CreativeSettings creativesettings = client.getCreativeSettings();
      HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(index);
      if (load) {
         for(int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
            ItemStack itemstack = hotbarsnapshot.get(i).copy();
            clientplayerentity.inventory.setInventorySlotContents(i, itemstack);
            client.playerController.sendSlotPacket(itemstack, 36 + i);
         }

         clientplayerentity.container.detectAndSendChanges();
      } else if (save) {
         for(int j = 0; j < PlayerInventory.getHotbarSize(); ++j) {
            hotbarsnapshot.set(j, clientplayerentity.inventory.getStackInSlot(j).copy());
         }

         String s = client.gameSettings.keyBindsHotbar[index].getLocalizedName();
         String s1 = client.gameSettings.keyBindLoadToolbar.getLocalizedName();
         client.ingameGUI.setOverlayMessage(new TranslationTextComponent("inventory.hotbarSaved", s1, s), false);
         creativesettings.save();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class CreativeContainer extends Container {
      /** the list of items in this container */
      public final NonNullList<ItemStack> itemList = NonNullList.create();

      public CreativeContainer(PlayerEntity player) {
         super((ContainerType<?>)null, 0);
         PlayerInventory playerinventory = player.inventory;

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new CreativeScreen.LockedSlot(CreativeScreen.TMP_INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerinventory, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      /**
       * Determines whether supplied player can use this container
       */
      public boolean canInteractWith(PlayerEntity playerIn) {
         return true;
      }

      /**
       * Updates the gui slots ItemStack's based on scroll position.
       */
      public void scrollTo(float pos) {
         int i = (this.itemList.size() + 9 - 1) / 9 - 5;
         int j = (int)((double)(pos * (float)i) + 0.5D);
         if (j < 0) {
            j = 0;
         }

         for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
               int i1 = l + (k + j) * 9;
               if (i1 >= 0 && i1 < this.itemList.size()) {
                  CreativeScreen.TMP_INVENTORY.setInventorySlotContents(l + k * 9, this.itemList.get(i1));
               } else {
                  CreativeScreen.TMP_INVENTORY.setInventorySlotContents(l + k * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.itemList.size() > 45;
      }

      /**
       * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
       * inventory and the other inventory(s).
       */
      public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
         if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
               slot.putStack(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      /**
       * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
       * is null for the initial slot that was double-clicked.
       */
      public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
         return slotIn.inventory != CreativeScreen.TMP_INVENTORY;
      }

      /**
       * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if
       * the slot can be added to a list of Slots to split the held ItemStack across.
       */
      public boolean canDragIntoSlot(Slot slotIn) {
         return slotIn.inventory != CreativeScreen.TMP_INVENTORY;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CreativeSlot extends Slot {
      private final Slot slot;

      public CreativeSlot(Slot p_i229959_1_, int p_i229959_2_, int p_i229959_3_, int p_i229959_4_) {
         super(p_i229959_1_.inventory, p_i229959_2_, p_i229959_3_, p_i229959_4_);
         this.slot = p_i229959_1_;
      }

      public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
         return this.slot.onTake(thePlayer, stack);
      }

      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      public boolean isItemValid(ItemStack stack) {
         return this.slot.isItemValid(stack);
      }

      /**
       * Helper fnct to get the stack in the slot.
       */
      public ItemStack getStack() {
         return this.slot.getStack();
      }

      /**
       * Returns if this slot contains a stack.
       */
      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      /**
       * Helper method to put a stack in the slot.
       */
      public void putStack(ItemStack stack) {
         this.slot.putStack(stack);
      }

      /**
       * Called when the stack in a Slot changes
       */
      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      /**
       * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
       * case of armor slots)
       */
      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      public int getItemStackLimit(ItemStack stack) {
         return this.slot.getItemStackLimit(stack);
      }

      @Nullable
      public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
         return this.slot.func_225517_c_();
      }

      /**
       * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
       * stack.
       */
      public ItemStack decrStackSize(int amount) {
         return this.slot.decrStackSize(amount);
      }

      /**
       * Actualy only call when we want to render the white square effect over the slots. Return always True, except for
       * the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
       */
      public boolean isEnabled() {
         return this.slot.isEnabled();
      }

      /**
       * Return whether this slot's stack can be taken from this slot.
       */
      public boolean canTakeStack(PlayerEntity playerIn) {
         return this.slot.canTakeStack(playerIn);
      }

      @Override
      public int getSlotIndex() {
         return this.slot.getSlotIndex();
      }

      @Override
      public boolean isSameInventory(Slot other) {
         return this.slot.isSameInventory(other);
      }

      @Override
      public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
         this.slot.setBackground(atlas, sprite);
         return this;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LockedSlot extends Slot {
      public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      /**
       * Return whether this slot's stack can be taken from this slot.
       */
      public boolean canTakeStack(PlayerEntity playerIn) {
         if (super.canTakeStack(playerIn) && this.getHasStack()) {
            return this.getStack().getChildTag("CustomCreativeLock") == null;
         } else {
            return !this.getHasStack();
         }
      }
   }
}