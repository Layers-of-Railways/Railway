package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends SettingsScreen {
   /** The ID of the button that has been pressed. */
   public KeyBinding buttonId;
   public long time;
   private KeyBindingList keyBindingList;
   private Button buttonReset;

   public ControlsScreen(Screen screen, GameSettings settings) {
      super(screen, settings, new TranslationTextComponent("controls.title"));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.format("options.mouse_settings"), (p_213126_1_) -> {
         this.minecraft.displayGuiScreen(new MouseSettingsScreen(this, this.gameSettings));
      }));
      this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.gameSettings, this.width / 2 - 155 + 160, 18, 150));
      this.keyBindingList = new KeyBindingList(this, this.minecraft);
      this.children.add(this.keyBindingList);
      this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll"), (p_213125_1_) -> {
         for(KeyBinding keybinding : this.gameSettings.keyBindings) {
            keybinding.setToDefault();
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done"), (p_213124_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.buttonId != null) {
         this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(p_mouseClicked_5_));
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.buttonId != null) {
         if (p_keyPressed_1_ == 256) {
            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
            this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
         } else {
            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
            this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
         }

         if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.buttonId.getKey()))
         this.buttonId = null;
         this.time = Util.milliTime();
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.keyBindingList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyBinding keybinding : this.gameSettings.keyBindings) {
         if (!keybinding.isDefault()) {
            flag = true;
            break;
         }
      }

      this.buttonReset.active = flag;
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}