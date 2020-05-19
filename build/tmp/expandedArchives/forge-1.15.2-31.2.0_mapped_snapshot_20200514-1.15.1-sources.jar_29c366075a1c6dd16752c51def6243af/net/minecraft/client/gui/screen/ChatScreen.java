package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatScreen extends Screen {
   private String historyBuffer = "";
   /**
    * keeps position of which chat message you will select when you press up, (does not increase for duplicated messages
    * sent immediately after each other)
    */
   private int sentHistoryCursor = -1;
   /** Chat entry field */
   protected TextFieldWidget inputField;
   /** is the text that appears when you press the chat key and the input box appears pre-filled */
   private String defaultInputFieldText = "";
   private CommandSuggestionHelper commandSuggestionHelper;

   public ChatScreen(String defaultText) {
      super(NarratorChatListener.EMPTY);
      this.defaultInputFieldText = defaultText;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.format("chat.editBox")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ChatScreen.this.commandSuggestionHelper.func_228129_c_();
         }
      };
      this.inputField.setMaxStringLength(256);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setResponder(this::func_212997_a);
      this.children.add(this.inputField);
      this.commandSuggestionHelper = new CommandSuggestionHelper(this.minecraft, this, this.inputField, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestionHelper.init();
      this.setFocusedDefault(this.inputField);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.inputField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.setChatLine(s);
      this.commandSuggestionHelper.init();
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
      this.minecraft.ingameGUI.getChatGUI().resetScroll();
   }

   public void tick() {
      this.inputField.tick();
   }

   private void func_212997_a(String p_212997_1_) {
      String s = this.inputField.getText();
      this.commandSuggestionHelper.func_228124_a_(!s.equals(this.defaultInputFieldText));
      this.commandSuggestionHelper.init();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.commandSuggestionHelper.onKeyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ == 256) {
         this.minecraft.displayGuiScreen((Screen)null);
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (p_keyPressed_1_ == 265) {
            this.getSentHistory(-1);
            return true;
         } else if (p_keyPressed_1_ == 264) {
            this.getSentHistory(1);
            return true;
         } else if (p_keyPressed_1_ == 266) {
            this.minecraft.ingameGUI.getChatGUI().addScrollPos((double)(this.minecraft.ingameGUI.getChatGUI().getLineCount() - 1));
            return true;
         } else if (p_keyPressed_1_ == 267) {
            this.minecraft.ingameGUI.getChatGUI().addScrollPos((double)(-this.minecraft.ingameGUI.getChatGUI().getLineCount() + 1));
            return true;
         } else {
            return false;
         }
      } else {
         String s = this.inputField.getText().trim();
         if (!s.isEmpty()) {
            this.sendMessage(s);
         }

         this.minecraft.displayGuiScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (p_mouseScrolled_5_ > 1.0D) {
         p_mouseScrolled_5_ = 1.0D;
      }

      if (p_mouseScrolled_5_ < -1.0D) {
         p_mouseScrolled_5_ = -1.0D;
      }

      if (this.commandSuggestionHelper.onScroll(p_mouseScrolled_5_)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            p_mouseScrolled_5_ *= 7.0D;
         }

         this.minecraft.ingameGUI.getChatGUI().addScrollPos(p_mouseScrolled_5_);
         return true;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.commandSuggestionHelper.onClick((double)((int)p_mouseClicked_1_), (double)((int)p_mouseClicked_3_), p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_5_ == 0) {
            ITextComponent itextcomponent = this.minecraft.ingameGUI.getChatGUI().getTextComponent(p_mouseClicked_1_, p_mouseClicked_3_);
            if (itextcomponent != null && this.handleComponentClicked(itextcomponent)) {
               return true;
            }
         }

         return this.inputField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
      if (p_insertText_2_) {
         this.inputField.setText(p_insertText_1_);
      } else {
         this.inputField.writeText(p_insertText_1_);
      }

   }

   /**
    * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
    * message from the current cursor position
    */
   public void getSentHistory(int msgPos) {
      int i = this.sentHistoryCursor + msgPos;
      int j = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
      i = MathHelper.clamp(i, 0, j);
      if (i != this.sentHistoryCursor) {
         if (i == j) {
            this.sentHistoryCursor = j;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == j) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.minecraft.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.commandSuggestionHelper.func_228124_a_(false);
            this.sentHistoryCursor = i;
         }
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.setFocused(this.inputField);
      this.inputField.setFocused2(true);
      fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.gameSettings.getChatBackgroundColor(Integer.MIN_VALUE));
      this.inputField.render(p_render_1_, p_render_2_, p_render_3_);
      this.commandSuggestionHelper.render(p_render_1_, p_render_2_);
      ITextComponent itextcomponent = this.minecraft.ingameGUI.getChatGUI().getTextComponent((double)p_render_1_, (double)p_render_2_);
      if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
         this.renderComponentHoverEffect(itextcomponent, p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String p_208604_1_) {
      this.inputField.setText(p_208604_1_);
   }
}