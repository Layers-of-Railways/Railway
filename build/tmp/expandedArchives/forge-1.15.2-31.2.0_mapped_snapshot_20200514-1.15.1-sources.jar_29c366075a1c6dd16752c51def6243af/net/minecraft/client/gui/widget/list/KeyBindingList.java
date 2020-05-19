package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public class KeyBindingList extends AbstractOptionList<KeyBindingList.Entry> {
   private final ControlsScreen controlsScreen;
   private int maxListLabelWidth;

   public KeyBindingList(ControlsScreen controls, Minecraft mcIn) {
      super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
      this.controlsScreen = controls;
      KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      Arrays.sort((Object[])akeybinding);
      String s = null;

      for(KeyBinding keybinding : akeybinding) {
         String s1 = keybinding.getKeyCategory();
         if (!s1.equals(s)) {
            s = s1;
            this.addEntry(new KeyBindingList.CategoryEntry(s1));
         }

         int i = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
         if (i > this.maxListLabelWidth) {
            this.maxListLabelWidth = i;
         }

         this.addEntry(new KeyBindingList.KeyEntry(keybinding));
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15 + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryEntry extends KeyBindingList.Entry {
      private final String labelText;
      private final int labelWidth;

      public CategoryEntry(String name) {
         this.labelText = I18n.format(name);
         this.labelWidth = KeyBindingList.this.minecraft.fontRenderer.getStringWidth(this.labelText);
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         KeyBindingList.this.minecraft.fontRenderer.drawString(this.labelText, (float)(KeyBindingList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float)(p_render_2_ + p_render_5_ - 9 - 1), 16777215);
      }

      public boolean changeFocus(boolean p_changeFocus_1_) {
         return false;
      }

      public List<? extends IGuiEventListener> children() {
         return Collections.emptyList();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends AbstractOptionList.Entry<KeyBindingList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public class KeyEntry extends KeyBindingList.Entry {
      /** The keybinding specified for this KeyEntry */
      private final KeyBinding keybinding;
      /** The localized key description for this KeyEntry */
      private final String keyDesc;
      private final Button btnChangeKeyBinding;
      private final Button btnReset;

      private KeyEntry(final KeyBinding name) {
         this.keybinding = name;
         this.keyDesc = I18n.format(name.getKeyDescription());
         this.btnChangeKeyBinding = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, this.keyDesc, (p_214386_2_) -> {
            KeyBindingList.this.controlsScreen.buttonId = name;
         }) {
            protected String getNarrationMessage() {
               return name.isInvalid() ? I18n.format("narrator.controls.unbound", KeyEntry.this.keyDesc) : I18n.format("narrator.controls.bound", KeyEntry.this.keyDesc, super.getNarrationMessage());
            }
         };
         this.btnReset = new Button(0, 0, 50, 20, I18n.format("controls.reset"), (p_214387_2_) -> {
            keybinding.setToDefault();
            KeyBindingList.this.minecraft.gameSettings.setKeyBindingCode(name, name.getDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
         }) {
            protected String getNarrationMessage() {
               return I18n.format("narrator.controls.reset", KeyEntry.this.keyDesc);
            }
         };
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         boolean flag = KeyBindingList.this.controlsScreen.buttonId == this.keybinding;
         KeyBindingList.this.minecraft.fontRenderer.drawString(this.keyDesc, (float)(p_render_3_ + 90 - KeyBindingList.this.maxListLabelWidth), (float)(p_render_2_ + p_render_5_ / 2 - 9 / 2), 16777215);
         this.btnReset.x = p_render_3_ + 190 + 20;
         this.btnReset.y = p_render_2_;
         this.btnReset.active = !this.keybinding.isDefault();
         this.btnReset.render(p_render_6_, p_render_7_, p_render_9_);
         this.btnChangeKeyBinding.x = p_render_3_ + 105;
         this.btnChangeKeyBinding.y = p_render_2_;
         this.btnChangeKeyBinding.setMessage(this.keybinding.getLocalizedName());
         boolean flag1 = false;
         boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
         if (!this.keybinding.isInvalid()) {
            for(KeyBinding keybinding : KeyBindingList.this.minecraft.gameSettings.keyBindings) {
               if (keybinding != this.keybinding && this.keybinding.conflicts(keybinding)) {
                  flag1 = true;
                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.setMessage(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.getMessage() + TextFormatting.WHITE + " <");
         } else if (flag1) {
            this.btnChangeKeyBinding.setMessage((keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.getMessage());
         }

         this.btnChangeKeyBinding.render(p_render_6_, p_render_7_, p_render_9_);
      }

      public List<? extends IGuiEventListener> children() {
         return ImmutableList.of(this.btnChangeKeyBinding, this.btnReset);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            return this.btnReset.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
         return this.btnChangeKeyBinding.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_) || this.btnReset.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }
}