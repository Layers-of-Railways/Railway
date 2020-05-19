package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextFieldWidget extends Widget implements IRenderable, IGuiEventListener {
   private final FontRenderer fontRenderer;
   /** Has the current text being edited on the textbox. */
   private String text = "";
   private int maxStringLength = 32;
   private int cursorCounter;
   private boolean enableBackgroundDrawing = true;
   /** if true the textbox can lose focus by clicking elsewhere on the screen */
   private boolean canLoseFocus = true;
   /** If this value is true along with isFocused, keyTyped will process the keys. */
   private boolean isEnabled = true;
   private boolean field_212956_h;
   /** The current character index that should be used as start of the rendered text. */
   private int lineScrollOffset;
   private int cursorPosition;
   /** other selection position, maybe the same as the cursor */
   private int selectionEnd;
   private int enabledColor = 14737632;
   private int disabledColor = 7368816;
   private String suggestion;
   private Consumer<String> guiResponder;
   /** Called to check if the text is valid */
   private Predicate<String> validator = Predicates.alwaysTrue();
   private BiFunction<String, Integer, String> textFormatter = (p_195610_0_, p_195610_1_) -> {
      return p_195610_0_;
   };

   public TextFieldWidget(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, String msg) {
      this(fontIn, xIn, yIn, widthIn, heightIn, (TextFieldWidget)null, msg);
   }

   public TextFieldWidget(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget p_i51138_6_, String msg) {
      super(xIn, yIn, widthIn, heightIn, msg);
      this.fontRenderer = fontIn;
      if (p_i51138_6_ != null) {
         this.setText(p_i51138_6_.getText());
      }

   }

   public void setResponder(Consumer<String> rssponderIn) {
      this.guiResponder = rssponderIn;
   }

   public void setTextFormatter(BiFunction<String, Integer, String> textFormatterIn) {
      this.textFormatter = textFormatterIn;
   }

   /**
    * Increments the cursor counter
    */
   public void tick() {
      ++this.cursorCounter;
   }

   protected String getNarrationMessage() {
      String s = this.getMessage();
      return s.isEmpty() ? "" : I18n.format("gui.narrate.editBox", s, this.text);
   }

   /**
    * Sets the text of the textbox, and moves the cursor to the end.
    */
   public void setText(String textIn) {
      if (this.validator.test(textIn)) {
         if (textIn.length() > this.maxStringLength) {
            this.text = textIn.substring(0, this.maxStringLength);
         } else {
            this.text = textIn;
         }

         this.setCursorPositionEnd();
         this.setSelectionPos(this.cursorPosition);
         this.onTextChanged(textIn);
      }
   }

   /**
    * Returns the contents of the textbox
    */
   public String getText() {
      return this.text;
   }

   /**
    * returns the text between the cursor and selectionEnd
    */
   public String getSelectedText() {
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(i, j);
   }

   public void setValidator(Predicate<String> validatorIn) {
      this.validator = validatorIn;
   }

   /**
    * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
    */
   public void writeText(String textToWrite) {
      String s = "";
      String s1 = SharedConstants.filterAllowedCharacters(textToWrite);
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int k = this.maxStringLength - this.text.length() - (i - j);
      if (!this.text.isEmpty()) {
         s = s + this.text.substring(0, i);
      }

      int l;
      if (k < s1.length()) {
         s = s + s1.substring(0, k);
         l = k;
      } else {
         s = s + s1;
         l = s1.length();
      }

      if (!this.text.isEmpty() && j < this.text.length()) {
         s = s + this.text.substring(j);
      }

      if (this.validator.test(s)) {
         this.text = s;
         this.clampCursorPosition(i + l);
         this.setSelectionPos(this.cursorPosition);
         this.onTextChanged(this.text);
      }
   }

   private void onTextChanged(String newText) {
      if (this.guiResponder != null) {
         this.guiResponder.accept(newText);
      }

      this.nextNarration = Util.milliTime() + 500L;
   }

   private void delete(int p_212950_1_) {
      if (Screen.hasControlDown()) {
         this.deleteWords(p_212950_1_);
      } else {
         this.deleteFromCursor(p_212950_1_);
      }

   }

   /**
    * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
    * which case the selection is deleted instead.
    */
   public void deleteWords(int num) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
         }
      }
   }

   /**
    * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
    * in which case the selection is deleted instead.
    */
   public void deleteFromCursor(int num) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean flag = num < 0;
            int i = flag ? this.cursorPosition + num : this.cursorPosition;
            int j = flag ? this.cursorPosition : this.cursorPosition + num;
            String s = "";
            if (i >= 0) {
               s = this.text.substring(0, i);
            }

            if (j < this.text.length()) {
               s = s + this.text.substring(j);
            }

            if (this.validator.test(s)) {
               this.text = s;
               if (flag) {
                  this.moveCursorBy(num);
               }

               this.onTextChanged(this.text);
            }
         }
      }
   }

   /**
    * Gets the starting index of the word at the specified number of words away from the cursor position.
    */
   public int getNthWordFromCursor(int numWords) {
      return this.getNthWordFromPos(numWords, this.getCursorPosition());
   }

   /**
    * Gets the starting index of the word at a distance of the specified number of words away from the given position.
    */
   private int getNthWordFromPos(int n, int pos) {
      return this.getNthWordFromPosWS(n, pos, true);
   }

   /**
    * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
    */
   private int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
      int i = pos;
      boolean flag = n < 0;
      int j = Math.abs(n);

      for(int k = 0; k < j; ++k) {
         if (!flag) {
            int l = this.text.length();
            i = this.text.indexOf(32, i);
            if (i == -1) {
               i = l;
            } else {
               while(skipWs && i < l && this.text.charAt(i) == ' ') {
                  ++i;
               }
            }
         } else {
            while(skipWs && i > 0 && this.text.charAt(i - 1) == ' ') {
               --i;
            }

            while(i > 0 && this.text.charAt(i - 1) != ' ') {
               --i;
            }
         }
      }

      return i;
   }

   /**
    * Moves the text cursor by a specified number of characters and clears the selection
    */
   public void moveCursorBy(int num) {
      this.setCursorPosition(this.cursorPosition + num);
   }

   /**
    * Sets the current position of the cursor.
    */
   public void setCursorPosition(int pos) {
      this.clampCursorPosition(pos);
      if (!this.field_212956_h) {
         this.setSelectionPos(this.cursorPosition);
      }

      this.onTextChanged(this.text);
   }

   public void clampCursorPosition(int pos) {
      this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
   }

   /**
    * Moves the cursor to the very start of this text box.
    */
   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   /**
    * Moves the cursor to the very end of this text box.
    */
   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (!this.canWrite()) {
         return false;
      } else {
         this.field_212956_h = Screen.hasShiftDown();
         if (Screen.isSelectAll(p_keyPressed_1_)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
         } else if (Screen.isCopy(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
            return true;
         } else if (Screen.isPaste(p_keyPressed_1_)) {
            if (this.isEnabled) {
               this.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
            }

            return true;
         } else if (Screen.isCut(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
               this.writeText("");
            }

            return true;
         } else {
            switch(p_keyPressed_1_) {
            case 259:
               if (this.isEnabled) {
                  this.field_212956_h = false;
                  this.delete(-1);
                  this.field_212956_h = Screen.hasShiftDown();
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return false;
            case 261:
               if (this.isEnabled) {
                  this.field_212956_h = false;
                  this.delete(1);
                  this.field_212956_h = Screen.hasShiftDown();
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(1));
               } else {
                  this.moveCursorBy(1);
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(-1));
               } else {
                  this.moveCursorBy(-1);
               }

               return true;
            case 268:
               this.setCursorPositionZero();
               return true;
            case 269:
               this.setCursorPositionEnd();
               return true;
            }
         }
      }
   }

   public boolean canWrite() {
      return this.getVisible() && this.isFocused() && this.isEnabled();
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (!this.canWrite()) {
         return false;
      } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
         if (this.isEnabled) {
            this.writeText(Character.toString(p_charTyped_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (!this.getVisible()) {
         return false;
      } else {
         boolean flag = p_mouseClicked_1_ >= (double)this.x && p_mouseClicked_1_ < (double)(this.x + this.width) && p_mouseClicked_3_ >= (double)this.y && p_mouseClicked_3_ < (double)(this.y + this.height);
         if (this.canLoseFocus) {
            this.setFocused2(flag);
         }

         if (this.isFocused() && flag && p_mouseClicked_5_ == 0) {
            int i = MathHelper.floor(p_mouseClicked_1_) - this.x;
            if (this.enableBackgroundDrawing) {
               i -= 4;
            }

            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(s, i).length() + this.lineScrollOffset);
            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * Sets focus to this gui element
    */
   public void setFocused2(boolean isFocusedIn) {
      super.setFocused(isFocusedIn);
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.getVisible()) {
         if (this.getEnableBackgroundDrawing()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int i = this.isEnabled ? this.enabledColor : this.disabledColor;
         int j = this.cursorPosition - this.lineScrollOffset;
         int k = this.selectionEnd - this.lineScrollOffset;
         String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
         boolean flag = j >= 0 && j <= s.length();
         boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && flag;
         int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
         int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
         int j1 = l;
         if (k > s.length()) {
            k = s.length();
         }

         if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = this.fontRenderer.drawStringWithShadow(this.textFormatter.apply(s1, this.lineScrollOffset), (float)l, (float)i1, i);
         }

         boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
         int k1 = j1;
         if (!flag) {
            k1 = j > 0 ? l + this.width : l;
         } else if (flag2) {
            k1 = j1 - 1;
            --j1;
         }

         if (!s.isEmpty() && flag && j < s.length()) {
            this.fontRenderer.drawStringWithShadow(this.textFormatter.apply(s.substring(j), this.cursorPosition), (float)j1, (float)i1, i);
         }

         if (!flag2 && this.suggestion != null) {
            this.fontRenderer.drawStringWithShadow(this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
         }

         if (flag1) {
            if (flag2) {
               AbstractGui.fill(k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            } else {
               this.fontRenderer.drawStringWithShadow("_", (float)k1, (float)i1, i);
            }
         }

         if (k != j) {
            int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
            this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
         }

      }
   }

   /**
    * Draws the blue selection box.
    */
   private void drawSelectionBox(int startX, int startY, int endX, int endY) {
      if (startX < endX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      if (startY < endY) {
         int j = startY;
         startY = endY;
         endY = j;
      }

      if (endX > this.x + this.width) {
         endX = this.x + this.width;
      }

      if (startX > this.x + this.width) {
         startX = this.x + this.width;
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
      bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
      bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
      bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
      tessellator.draw();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   /**
    * Sets the maximum length for the text in this text box. If the current text is longer than this length, the current
    * text will be trimmed.
    */
   public void setMaxStringLength(int length) {
      this.maxStringLength = length;
      if (this.text.length() > length) {
         this.text = this.text.substring(0, length);
         this.onTextChanged(this.text);
      }

   }

   /**
    * returns the maximum number of character that can be contained in this textbox
    */
   private int getMaxStringLength() {
      return this.maxStringLength;
   }

   /**
    * returns the current position of the cursor
    */
   public int getCursorPosition() {
      return this.cursorPosition;
   }

   /**
    * Gets whether the background and outline of this text box should be drawn (true if so).
    */
   private boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   /**
    * Sets whether or not the background and outline of this text box should be drawn.
    */
   public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
      this.enableBackgroundDrawing = enableBackgroundDrawingIn;
   }

   /**
    * Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled.
    */
   public void setTextColor(int color) {
      this.enabledColor = color;
   }

   /**
    * Sets the color to use for text in this text box when this text box is disabled.
    */
   public void setDisabledTextColour(int color) {
      this.disabledColor = color;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return this.visible && this.isEnabled ? super.changeFocus(p_changeFocus_1_) : false;
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return this.visible && p_isMouseOver_1_ >= (double)this.x && p_isMouseOver_1_ < (double)(this.x + this.width) && p_isMouseOver_3_ >= (double)this.y && p_isMouseOver_3_ < (double)(this.y + this.height);
   }

   protected void onFocusedChanged(boolean p_onFocusedChanged_1_) {
      if (p_onFocusedChanged_1_) {
         this.cursorCounter = 0;
      }

   }

   private boolean isEnabled() {
      return this.isEnabled;
   }

   /**
    * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
    */
   public void setEnabled(boolean enabled) {
      this.isEnabled = enabled;
   }

   /**
    * returns the width of the textbox depending on if background drawing is enabled
    */
   public int getAdjustedWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
   }

   /**
    * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
    * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
    */
   public void setSelectionPos(int position) {
      int i = this.text.length();
      this.selectionEnd = MathHelper.clamp(position, 0, i);
      if (this.fontRenderer != null) {
         if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i;
         }

         int j = this.getAdjustedWidth();
         String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
         int k = s.length() + this.lineScrollOffset;
         if (this.selectionEnd == this.lineScrollOffset) {
            this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, j, true).length();
         }

         if (this.selectionEnd > k) {
            this.lineScrollOffset += this.selectionEnd - k;
         } else if (this.selectionEnd <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
         }

         this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
      }

   }

   /**
    * Sets whether this text box loses focus when something other than it is clicked.
    */
   public void setCanLoseFocus(boolean canLoseFocusIn) {
      this.canLoseFocus = canLoseFocusIn;
   }

   /**
    * returns true if this textbox is visible
    */
   public boolean getVisible() {
      return this.visible;
   }

   /**
    * Sets whether or not this textbox is visible
    */
   public void setVisible(boolean isVisible) {
      this.visible = isVisible;
   }

   public void setSuggestion(@Nullable String p_195612_1_) {
      this.suggestion = p_195612_1_;
   }

   public int func_195611_j(int p_195611_1_) {
      return p_195611_1_ > this.text.length() ? this.x : this.x + this.fontRenderer.getStringWidth(this.text.substring(0, p_195611_1_));
   }

   public void setX(int xIn) {
      this.x = xIn;
   }
}