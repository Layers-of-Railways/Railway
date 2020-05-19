package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditBookScreen extends Screen {
   private final PlayerEntity editingPlayer;
   private final ItemStack book;
   /** Whether the book's title or contents has been modified since being opened */
   private boolean bookIsModified;
   /** Determines if the signing screen is open */
   private boolean bookGettingSigned;
   /** Update ticks since the gui was opened */
   private int updateCount;
   private int currPage;
   private final List<String> bookPages = Lists.newArrayList();
   private String bookTitle = "";
   /** Note that this can be less than selectionStart if you select text right-to-left */
   private int selectionEnd;
   /** Note that this will be greater than selectionEnd if you select text right-to-left */
   private int selectionStart;
   /** In milliseconds */
   private long lastClickTime;
   private int cachedPage = -1;
   private ChangePageButton buttonNextPage;
   private ChangePageButton buttonPreviousPage;
   private Button buttonDone;
   private Button buttonSign;
   private Button buttonFinalize;
   private Button buttonCancel;
   private final Hand hand;

   public EditBookScreen(PlayerEntity player, ItemStack bookIn, Hand handIn) {
      super(NarratorChatListener.EMPTY);
      this.editingPlayer = player;
      this.book = bookIn;
      this.hand = handIn;
      CompoundNBT compoundnbt = bookIn.getTag();
      if (compoundnbt != null) {
         ListNBT listnbt = compoundnbt.getList("pages", 8).copy();

         for(int i = 0; i < listnbt.size(); ++i) {
            this.bookPages.add(listnbt.getString(i));
         }
      }

      if (this.bookPages.isEmpty()) {
         this.bookPages.add("");
      }

   }

   /**
    * Returns the number of pages in the book
    */
   private int getPageCount() {
      return this.bookPages.size();
   }

   public void tick() {
      super.tick();
      ++this.updateCount;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.buttonSign = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("book.signButton"), (p_214201_1_) -> {
         this.bookGettingSigned = true;
         this.updateButtons();
      }));
      this.buttonDone = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("gui.done"), (p_214204_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         this.sendBookToServer(false);
      }));
      this.buttonFinalize = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("book.finalizeButton"), (p_214195_1_) -> {
         if (this.bookGettingSigned) {
            this.sendBookToServer(true);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.buttonCancel = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel"), (p_214212_1_) -> {
         if (this.bookGettingSigned) {
            this.bookGettingSigned = false;
         }

         this.updateButtons();
      }));
      int i = (this.width - 192) / 2;
      int j = 2;
      this.buttonNextPage = this.addButton(new ChangePageButton(i + 116, 159, true, (p_214208_1_) -> {
         this.nextPage();
      }, true));
      this.buttonPreviousPage = this.addButton(new ChangePageButton(i + 43, 159, false, (p_214205_1_) -> {
         this.previousPage();
      }, true));
      this.updateButtons();
   }

   /**
    * Returns a copy of the input string with character 127 (del) and character 167 (section sign) removed
    */
   private String removeUnprintableChars(String text) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : text.toCharArray()) {
         if (c0 != 167 && c0 != 127) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   /**
    * Displays the previous page
    */
   private void previousPage() {
      if (this.currPage > 0) {
         --this.currPage;
         this.selectionEnd = 0;
         this.selectionStart = this.selectionEnd;
      }

      this.updateButtons();
   }

   /**
    * Displays the next page (creating it if necessary)
    */
   private void nextPage() {
      if (this.currPage < this.getPageCount() - 1) {
         ++this.currPage;
         this.selectionEnd = 0;
         this.selectionStart = this.selectionEnd;
      } else {
         this.addNewPage();
         if (this.currPage < this.getPageCount() - 1) {
            ++this.currPage;
         }

         this.selectionEnd = 0;
         this.selectionStart = this.selectionEnd;
      }

      this.updateButtons();
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   /**
    * Sets visibility for book buttons
    */
   private void updateButtons() {
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonNextPage.visible = !this.bookGettingSigned;
      this.buttonDone.visible = !this.bookGettingSigned;
      this.buttonSign.visible = !this.bookGettingSigned;
      this.buttonCancel.visible = this.bookGettingSigned;
      this.buttonFinalize.visible = this.bookGettingSigned;
      this.buttonFinalize.active = !this.bookTitle.trim().isEmpty();
   }

   private void trimEmptyPages() {
      ListIterator<String> listiterator = this.bookPages.listIterator(this.bookPages.size());

      while(listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
         listiterator.remove();
      }

   }

   private void sendBookToServer(boolean publish) {
      if (this.bookIsModified) {
         this.trimEmptyPages();
         ListNBT listnbt = new ListNBT();
         this.bookPages.stream().map(StringNBT::valueOf).forEach(listnbt::add);
         if (!this.bookPages.isEmpty()) {
            this.book.setTagInfo("pages", listnbt);
         }

         if (publish) {
            this.book.setTagInfo("author", StringNBT.valueOf(this.editingPlayer.getGameProfile().getName()));
            this.book.setTagInfo("title", StringNBT.valueOf(this.bookTitle.trim()));
         }

         this.minecraft.getConnection().sendPacket(new CEditBookPacket(this.book, publish, this.hand));
      }
   }

   /**
    * Adds a new page to the book (capped at 100 pages)
    */
   private void addNewPage() {
      if (this.getPageCount() < 100) {
         this.bookPages.add("");
         this.bookIsModified = true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         return this.bookGettingSigned ? this.keyPressedInTitle(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : this.keyPressedInBook(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
         return true;
      } else if (this.bookGettingSigned) {
         if (this.bookTitle.length() < 16 && SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
            this.bookTitle = this.bookTitle + Character.toString(p_charTyped_1_);
            this.updateButtons();
            this.bookIsModified = true;
            return true;
         } else {
            return false;
         }
      } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
         this.insertTextIntoPage(Character.toString(p_charTyped_1_));
         return true;
      } else {
         return false;
      }
   }

   /**
    * Handles keypresses, clipboard functions, and page turning
    */
   private boolean keyPressedInBook(int keyCode, int scanCode, int modifiers) {
      String s = this.getCurrPageText();
      if (Screen.isSelectAll(keyCode)) {
         this.selectionStart = 0;
         this.selectionEnd = s.length();
         return true;
      } else if (Screen.isCopy(keyCode)) {
         this.minecraft.keyboardListener.setClipboardString(this.getSelectedText());
         return true;
      } else if (Screen.isPaste(keyCode)) {
         this.insertTextIntoPage(this.removeUnprintableChars(TextFormatting.getTextWithoutFormattingCodes(this.minecraft.keyboardListener.getClipboardString().replaceAll("\\r", ""))));
         this.selectionStart = this.selectionEnd;
         return true;
      } else if (Screen.isCut(keyCode)) {
         this.minecraft.keyboardListener.setClipboardString(this.getSelectedText());
         this.removeSelectedText();
         return true;
      } else {
         switch(keyCode) {
         case 257:
         case 335:
            this.insertTextIntoPage("\n");
            return true;
         case 259:
            this.backspacePressed(s);
            return true;
         case 261:
            this.deletePressed(s);
            return true;
         case 262:
            this.rightPressed(s);
            return true;
         case 263:
            this.leftPressed(s);
            return true;
         case 264:
            this.downPressed(s);
            return true;
         case 265:
            this.upPressed(s);
            return true;
         case 266:
            this.buttonPreviousPage.onPress();
            return true;
         case 267:
            this.buttonNextPage.onPress();
            return true;
         case 268:
            this.homePressed(s);
            return true;
         case 269:
            this.endPressed(s);
            return true;
         default:
            return false;
         }
      }
   }

   /**
    * Called when backspace is pressed 
    * Removes the character to the left of the cursor (or the entire selection)
    */
   private void backspacePressed(String pageText) {
      if (!pageText.isEmpty()) {
         if (this.selectionStart != this.selectionEnd) {
            this.removeSelectedText();
         } else if (this.selectionEnd > 0) {
            String s = (new StringBuilder(pageText)).deleteCharAt(Math.max(0, this.selectionEnd - 1)).toString();
            this.func_214217_j(s);
            this.selectionEnd = Math.max(0, this.selectionEnd - 1);
            this.selectionStart = this.selectionEnd;
         }
      }

   }

   /**
    * Called when delete is pressed 
    * Removes the character to the right of the cursor (or the entire selection)
    */
   private void deletePressed(String pageText) {
      if (!pageText.isEmpty()) {
         if (this.selectionStart != this.selectionEnd) {
            this.removeSelectedText();
         } else if (this.selectionEnd < pageText.length()) {
            String s = (new StringBuilder(pageText)).deleteCharAt(Math.max(0, this.selectionEnd)).toString();
            this.func_214217_j(s);
         }
      }

   }

   /**
    * Called when the left directional arrow on the keyboard is pressed
    */
   private void leftPressed(String pageText) {
      int i = this.font.getBidiFlag() ? 1 : -1;
      if (Screen.hasControlDown()) {
         this.selectionEnd = this.font.getWordPosition(pageText, i, this.selectionEnd, true);
      } else {
         this.selectionEnd = Math.max(0, this.selectionEnd + i);
      }

      if (!Screen.hasShiftDown()) {
         this.selectionStart = this.selectionEnd;
      }

   }

   /**
    * Called when the right directional arrow on the keyboard is pressed
    */
   private void rightPressed(String pageText) {
      int i = this.font.getBidiFlag() ? -1 : 1;
      if (Screen.hasControlDown()) {
         this.selectionEnd = this.font.getWordPosition(pageText, i, this.selectionEnd, true);
      } else {
         this.selectionEnd = Math.min(pageText.length(), this.selectionEnd + i);
      }

      if (!Screen.hasShiftDown()) {
         this.selectionStart = this.selectionEnd;
      }

   }

   /**
    * Called when the up directional arrow on the keyboard is pressed
    */
   private void upPressed(String pageText) {
      if (!pageText.isEmpty()) {
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(pageText, this.selectionEnd);
         if (editbookscreen$point.y == 0) {
            this.selectionEnd = 0;
            if (!Screen.hasShiftDown()) {
               this.selectionStart = this.selectionEnd;
            }
         } else {
            int i = this.func_214203_a(pageText, new EditBookScreen.Point(editbookscreen$point.x + this.func_214206_a(pageText, this.selectionEnd) / 3, editbookscreen$point.y - 9));
            if (i >= 0) {
               this.selectionEnd = i;
               if (!Screen.hasShiftDown()) {
                  this.selectionStart = this.selectionEnd;
               }
            }
         }
      }

   }

   /**
    * Called when the down arrow on the keyboard is pressed
    */
   private void downPressed(String pageText) {
      if (!pageText.isEmpty()) {
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(pageText, this.selectionEnd);
         int i = this.font.getWordWrappedHeight(pageText + "" + TextFormatting.BLACK + "_", 114);
         if (editbookscreen$point.y + 9 == i) {
            this.selectionEnd = pageText.length();
            if (!Screen.hasShiftDown()) {
               this.selectionStart = this.selectionEnd;
            }
         } else {
            int j = this.func_214203_a(pageText, new EditBookScreen.Point(editbookscreen$point.x + this.func_214206_a(pageText, this.selectionEnd) / 3, editbookscreen$point.y + 9));
            if (j >= 0) {
               this.selectionEnd = j;
               if (!Screen.hasShiftDown()) {
                  this.selectionStart = this.selectionEnd;
               }
            }
         }
      }

   }

   /**
    * Called when the home button on the keyboard is pressed
    */
   private void homePressed(String pageText) {
      this.selectionEnd = this.func_214203_a(pageText, new EditBookScreen.Point(0, this.func_214194_c(pageText, this.selectionEnd).y));
      if (!Screen.hasShiftDown()) {
         this.selectionStart = this.selectionEnd;
      }

   }

   /**
    * Called when the end button on the keyboard is pressed
    */
   private void endPressed(String pageText) {
      this.selectionEnd = this.func_214203_a(pageText, new EditBookScreen.Point(113, this.func_214194_c(pageText, this.selectionEnd).y));
      if (!Screen.hasShiftDown()) {
         this.selectionStart = this.selectionEnd;
      }

   }

   /**
    * Removes the text between selectionStart and selectionEnd from the currrent page
    */
   private void removeSelectedText() {
      if (this.selectionStart != this.selectionEnd) {
         String s = this.getCurrPageText();
         int i = Math.min(this.selectionEnd, this.selectionStart);
         int j = Math.max(this.selectionEnd, this.selectionStart);
         String s1 = s.substring(0, i) + s.substring(j);
         this.selectionEnd = i;
         this.selectionStart = this.selectionEnd;
         this.func_214217_j(s1);
      }
   }

   private int func_214206_a(String p_214206_1_, int p_214206_2_) {
      return (int)this.font.getCharWidth(p_214206_1_.charAt(MathHelper.clamp(p_214206_2_, 0, p_214206_1_.length() - 1)));
   }

   /**
    * Handles special keys pressed while editing the book's title
    */
   private boolean keyPressedInTitle(int keyCode, int scanCode, int modifiers) {
      switch(keyCode) {
      case 257:
      case 335:
         if (!this.bookTitle.isEmpty()) {
            this.sendBookToServer(true);
            this.minecraft.displayGuiScreen((Screen)null);
         }

         return true;
      case 259:
         if (!this.bookTitle.isEmpty()) {
            this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
            this.updateButtons();
         }

         return true;
      default:
         return false;
      }
   }

   /**
    * Returns the contents of the current page as a string (or an empty string if the currPage isn't a valid page index)
    */
   private String getCurrPageText() {
      return this.currPage >= 0 && this.currPage < this.bookPages.size() ? this.bookPages.get(this.currPage) : "";
   }

   private void func_214217_j(String p_214217_1_) {
      if (this.currPage >= 0 && this.currPage < this.bookPages.size()) {
         this.bookPages.set(this.currPage, p_214217_1_);
         this.bookIsModified = true;
      }

   }

   /**
    * Inserts text into the current page at the between selectionStart and selectionEnd (erasing any highlighted text)
    */
   private void insertTextIntoPage(String text) {
      if (this.selectionStart != this.selectionEnd) {
         this.removeSelectedText();
      }

      String s = this.getCurrPageText();
      this.selectionEnd = MathHelper.clamp(this.selectionEnd, 0, s.length());
      String s1 = (new StringBuilder(s)).insert(this.selectionEnd, text).toString();
      int i = this.font.getWordWrappedHeight(s1 + "" + TextFormatting.BLACK + "_", 114);
      if (i <= 128 && s1.length() < 1024) {
         this.func_214217_j(s1);
         this.selectionStart = this.selectionEnd = Math.min(this.getCurrPageText().length(), this.selectionEnd + text.length());
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.setFocused((IGuiEventListener)null);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(ReadBookScreen.BOOK_TEXTURES);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.blit(i, 2, 0, 0, 192, 192);
      if (this.bookGettingSigned) {
         String s = this.bookTitle;
         if (this.updateCount / 6 % 2 == 0) {
            s = s + "" + TextFormatting.BLACK + "_";
         } else {
            s = s + "" + TextFormatting.GRAY + "_";
         }

         String s1 = I18n.format("book.editTitle");
         int k = this.getTextWidth(s1);
         this.font.drawString(s1, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
         int l = this.getTextWidth(s);
         this.font.drawString(s, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
         String s2 = I18n.format("book.byAuthor", this.editingPlayer.getName().getString());
         int i1 = this.getTextWidth(s2);
         this.font.drawString(TextFormatting.DARK_GRAY + s2, (float)(i + 36 + (114 - i1) / 2), 60.0F, 0);
         String s3 = I18n.format("book.finalizeWarning");
         this.font.drawSplitString(s3, i + 36, 82, 114, 0);
      } else {
         String s4 = I18n.format("book.pageIndicator", this.currPage + 1, this.getPageCount());
         String s5 = this.getCurrPageText();
         int j1 = this.getTextWidth(s4);
         this.font.drawString(s4, (float)(i - j1 + 192 - 44), 18.0F, 0);
         this.font.drawSplitString(s5, i + 36, 32, 114, 0);
         this.highlightSelectedText(s5);
         if (this.updateCount / 6 % 2 == 0) {
            EditBookScreen.Point editbookscreen$point = this.func_214194_c(s5, this.selectionEnd);
            if (this.font.getBidiFlag()) {
               this.func_214227_a(editbookscreen$point);
               editbookscreen$point.x = editbookscreen$point.x - 4;
            }

            this.func_214224_c(editbookscreen$point);
            if (this.selectionEnd < s5.length()) {
               AbstractGui.fill(editbookscreen$point.x, editbookscreen$point.y - 1, editbookscreen$point.x + 1, editbookscreen$point.y + 9, -16777216);
            } else {
               this.font.drawString("_", (float)editbookscreen$point.x, (float)editbookscreen$point.y, 0);
            }
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   /**
    * Returns the width of text
    */
   private int getTextWidth(String text) {
      return this.font.getStringWidth(this.font.getBidiFlag() ? this.font.bidiReorder(text) : text);
   }

   private int func_214216_b(String p_214216_1_, int p_214216_2_) {
      return this.font.sizeStringToWidth(p_214216_1_, p_214216_2_);
   }

   /**
    * Returns any selected text on the current page
    */
   private String getSelectedText() {
      String s = this.getCurrPageText();
      int i = Math.min(this.selectionEnd, this.selectionStart);
      int j = Math.max(this.selectionEnd, this.selectionStart);
      return s.substring(i, j);
   }

   /**
    * Uses drawSelectionBox to draw one or more boxes behind any selected text
    */
   private void highlightSelectedText(String pageText) {
      if (this.selectionStart != this.selectionEnd) {
         int i = Math.min(this.selectionEnd, this.selectionStart);
         int j = Math.max(this.selectionEnd, this.selectionStart);
         String s = pageText.substring(i, j);
         int k = this.font.getWordPosition(pageText, 1, j, true);
         String s1 = pageText.substring(i, k);
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(pageText, i);
         EditBookScreen.Point editbookscreen$point1 = new EditBookScreen.Point(editbookscreen$point.x, editbookscreen$point.y + 9);

         while(!s.isEmpty()) {
            int l = this.func_214216_b(s1, 114 - editbookscreen$point.x);
            if (s.length() <= l) {
               editbookscreen$point1.x = editbookscreen$point.x + this.getTextWidth(s);
               this.drawSelectionBox(editbookscreen$point, editbookscreen$point1);
               break;
            }

            l = Math.min(l, s.length() - 1);
            String s2 = s.substring(0, l);
            char c0 = s.charAt(l);
            boolean flag = c0 == ' ' || c0 == '\n';
            s = TextFormatting.getFormatString(s2) + s.substring(l + (flag ? 1 : 0));
            s1 = TextFormatting.getFormatString(s2) + s1.substring(l + (flag ? 1 : 0));
            editbookscreen$point1.x = editbookscreen$point.x + this.getTextWidth(s2 + " ");
            this.drawSelectionBox(editbookscreen$point, editbookscreen$point1);
            editbookscreen$point.x = 0;
            editbookscreen$point.y = editbookscreen$point.y + 9;
            editbookscreen$point1.y = editbookscreen$point1.y + 9;
         }

      }
   }

   /**
    * Draws the blue text selection box, defined by the two point parameters
    */
   private void drawSelectionBox(EditBookScreen.Point topLeft, EditBookScreen.Point bottomRight) {
      EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point(topLeft.x, topLeft.y);
      EditBookScreen.Point editbookscreen$point1 = new EditBookScreen.Point(bottomRight.x, bottomRight.y);
      if (this.font.getBidiFlag()) {
         this.func_214227_a(editbookscreen$point);
         this.func_214227_a(editbookscreen$point1);
         int i = editbookscreen$point1.x;
         editbookscreen$point1.x = editbookscreen$point.x;
         editbookscreen$point.x = i;
      }

      this.func_214224_c(editbookscreen$point);
      this.func_214224_c(editbookscreen$point1);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)editbookscreen$point.x, (double)editbookscreen$point1.y, 0.0D).endVertex();
      bufferbuilder.pos((double)editbookscreen$point1.x, (double)editbookscreen$point1.y, 0.0D).endVertex();
      bufferbuilder.pos((double)editbookscreen$point1.x, (double)editbookscreen$point.y, 0.0D).endVertex();
      bufferbuilder.pos((double)editbookscreen$point.x, (double)editbookscreen$point.y, 0.0D).endVertex();
      tessellator.draw();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   private EditBookScreen.Point func_214194_c(String pageText, int p_214194_2_) {
      EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point();
      int i = 0;
      int j = 0;

      for(String s = pageText; !s.isEmpty(); j = i) {
         int k = this.func_214216_b(s, 114);
         if (s.length() <= k) {
            String s3 = s.substring(0, Math.min(Math.max(p_214194_2_ - j, 0), s.length()));
            editbookscreen$point.x = editbookscreen$point.x + this.getTextWidth(s3);
            break;
         }

         String s1 = s.substring(0, k);
         char c0 = s.charAt(k);
         boolean flag = c0 == ' ' || c0 == '\n';
         s = TextFormatting.getFormatString(s1) + s.substring(k + (flag ? 1 : 0));
         i += s1.length() + (flag ? 1 : 0);
         if (i - 1 >= p_214194_2_) {
            String s2 = s1.substring(0, Math.min(Math.max(p_214194_2_ - j, 0), s1.length()));
            editbookscreen$point.x = editbookscreen$point.x + this.getTextWidth(s2);
            break;
         }

         editbookscreen$point.y = editbookscreen$point.y + 9;
      }

      return editbookscreen$point;
   }

   private void func_214227_a(EditBookScreen.Point p_214227_1_) {
      if (this.font.getBidiFlag()) {
         p_214227_1_.x = 114 - p_214227_1_.x;
      }

   }

   private void func_214210_b(EditBookScreen.Point p_214210_1_) {
      p_214210_1_.x = p_214210_1_.x - (this.width - 192) / 2 - 36;
      p_214210_1_.y = p_214210_1_.y - 32;
   }

   private void func_214224_c(EditBookScreen.Point p_214224_1_) {
      p_214224_1_.x = p_214224_1_.x + (this.width - 192) / 2 + 36;
      p_214224_1_.y = p_214224_1_.y + 32;
   }

   private int func_214226_d(String p_214226_1_, int p_214226_2_) {
      if (p_214226_2_ < 0) {
         return 0;
      } else {
         float f1 = 0.0F;
         boolean flag = false;
         String s = p_214226_1_ + " ";

         for(int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);
            float f2 = this.font.getCharWidth(c0);
            if (c0 == 167 && i < s.length() - 1) {
               ++i;
               c0 = s.charAt(i);
               if (c0 != 'l' && c0 != 'L') {
                  if (c0 == 'r' || c0 == 'R') {
                     flag = false;
                  }
               } else {
                  flag = true;
               }

               f2 = 0.0F;
            }

            float f = f1;
            f1 += f2;
            if (flag && f2 > 0.0F) {
               ++f1;
            }

            if ((float)p_214226_2_ >= f && (float)p_214226_2_ < f1) {
               return i;
            }
         }

         return (float)p_214226_2_ >= f1 ? s.length() - 1 : -1;
      }
   }

   private int func_214203_a(String p_214203_1_, EditBookScreen.Point p_214203_2_) {
      int i = 16 * 9;
      if (p_214203_2_.y > i) {
         return -1;
      } else {
         int j = Integer.MIN_VALUE;
         int k = 9;
         int l = 0;

         for(String s = p_214203_1_; !s.isEmpty() && j < i; k += 9) {
            int i1 = this.func_214216_b(s, 114);
            if (i1 < s.length()) {
               String s1 = s.substring(0, i1);
               if (p_214203_2_.y >= j && p_214203_2_.y < k) {
                  int k1 = this.func_214226_d(s1, p_214203_2_.x);
                  return k1 < 0 ? -1 : l + k1;
               }

               char c0 = s.charAt(i1);
               boolean flag = c0 == ' ' || c0 == '\n';
               s = TextFormatting.getFormatString(s1) + s.substring(i1 + (flag ? 1 : 0));
               l += s1.length() + (flag ? 1 : 0);
            } else if (p_214203_2_.y >= j && p_214203_2_.y < k) {
               int j1 = this.func_214226_d(s, p_214203_2_.x);
               return j1 < 0 ? -1 : l + j1;
            }

            j = k;
         }

         return p_214203_1_.length();
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         long i = Util.milliTime();
         String s = this.getCurrPageText();
         if (!s.isEmpty()) {
            EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point((int)p_mouseClicked_1_, (int)p_mouseClicked_3_);
            this.func_214210_b(editbookscreen$point);
            this.func_214227_a(editbookscreen$point);
            int j = this.func_214203_a(s, editbookscreen$point);
            if (j >= 0) {
               if (j == this.cachedPage && i - this.lastClickTime < 250L) {
                  if (this.selectionStart == this.selectionEnd) {
                     this.selectionStart = this.font.getWordPosition(s, -1, j, false);
                     this.selectionEnd = this.font.getWordPosition(s, 1, j, false);
                  } else {
                     this.selectionStart = 0;
                     this.selectionEnd = this.getCurrPageText().length();
                  }
               } else {
                  this.selectionEnd = j;
                  if (!Screen.hasShiftDown()) {
                     this.selectionStart = this.selectionEnd;
                  }
               }
            }

            this.cachedPage = j;
         }

         this.lastClickTime = i;
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ == 0 && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
         String s = this.bookPages.get(this.currPage);
         EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point((int)p_mouseDragged_1_, (int)p_mouseDragged_3_);
         this.func_214210_b(editbookscreen$point);
         this.func_214227_a(editbookscreen$point);
         int i = this.func_214203_a(s, editbookscreen$point);
         if (i >= 0) {
            this.selectionEnd = i;
         }
      }

      return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   @OnlyIn(Dist.CLIENT)
   class Point {
      private int x;
      private int y;

      Point() {
      }

      Point(int xIn, int yIn) {
         this.x = xIn;
         this.y = yIn;
      }
   }
}