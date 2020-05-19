package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
   protected TextFieldWidget commandTextField;
   protected TextFieldWidget resultTextField;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button trackOutputButton;
   protected boolean field_195238_s;
   private CommandSuggestionHelper field_228184_g_;

   public AbstractCommandBlockScreen() {
      super(NarratorChatListener.EMPTY);
   }

   public void tick() {
      this.commandTextField.tick();
   }

   abstract CommandBlockLogic getLogic();

   abstract int func_195236_i();

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done"), (p_214187_1_) -> {
         this.func_195234_k();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel"), (p_214186_1_) -> {
         this.onClose();
      }));
      this.trackOutputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.func_195236_i(), 20, 20, "O", (p_214184_1_) -> {
         CommandBlockLogic commandblocklogic = this.getLogic();
         commandblocklogic.setTrackOutput(!commandblocklogic.shouldTrackOutput());
         this.updateTrackOutput();
      }));
      this.commandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.format("advMode.command")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + AbstractCommandBlockScreen.this.field_228184_g_.func_228129_c_();
         }
      };
      this.commandTextField.setMaxStringLength(32500);
      this.commandTextField.setResponder(this::func_214185_b);
      this.children.add(this.commandTextField);
      this.resultTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.func_195236_i(), 276, 20, I18n.format("advMode.previousOutput"));
      this.resultTextField.setMaxStringLength(32500);
      this.resultTextField.setEnabled(false);
      this.resultTextField.setText("-");
      this.children.add(this.resultTextField);
      this.setFocusedDefault(this.commandTextField);
      this.commandTextField.setFocused2(true);
      this.field_228184_g_ = new CommandSuggestionHelper(this.minecraft, this, this.commandTextField, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.field_228184_g_.func_228124_a_(true);
      this.field_228184_g_.init();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.commandTextField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.commandTextField.setText(s);
      this.field_228184_g_.init();
   }

   protected void updateTrackOutput() {
      if (this.getLogic().shouldTrackOutput()) {
         this.trackOutputButton.setMessage("O");
         this.resultTextField.setText(this.getLogic().getLastOutput().getString());
      } else {
         this.trackOutputButton.setMessage("X");
         this.resultTextField.setText("-");
      }

   }

   protected void func_195234_k() {
      CommandBlockLogic commandblocklogic = this.getLogic();
      this.func_195235_a(commandblocklogic);
      if (!commandblocklogic.shouldTrackOutput()) {
         commandblocklogic.setLastOutput((ITextComponent)null);
      }

      this.minecraft.displayGuiScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   protected abstract void func_195235_a(CommandBlockLogic commandBlockLogicIn);

   public void onClose() {
      this.getLogic().setTrackOutput(this.field_195238_s);
      this.minecraft.displayGuiScreen((Screen)null);
   }

   private void func_214185_b(String p_214185_1_) {
      this.field_228184_g_.init();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.field_228184_g_.onKeyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.func_195234_k();
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.field_228184_g_.onScroll(p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.field_228184_g_.onClick(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.format("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandTextField.render(p_render_1_, p_render_2_, p_render_3_);
      int i = 75;
      if (!this.resultTextField.getText().isEmpty()) {
         i = i + (5 * 9 + 1 + this.func_195236_i() - 135);
         this.drawString(this.font, I18n.format("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
         this.resultTextField.render(p_render_1_, p_render_2_, p_render_3_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_228184_g_.render(p_render_1_, p_render_2_);
   }
}