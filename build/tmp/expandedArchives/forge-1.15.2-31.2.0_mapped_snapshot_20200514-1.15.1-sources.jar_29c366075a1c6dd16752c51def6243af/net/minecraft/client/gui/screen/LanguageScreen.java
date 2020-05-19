package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageScreen extends SettingsScreen {
   /** The List GuiSlot object reference. */
   private LanguageScreen.List list;
   /** Reference to the LanguageManager object. */
   private final LanguageManager languageManager;
   private OptionButton field_211832_i;
   /** The button to confirm the current settings. */
   private Button confirmSettingsBtn;

   public LanguageScreen(Screen screen, GameSettings gameSettingsObj, LanguageManager manager) {
      super(screen, gameSettingsObj, new TranslationTextComponent("options.language"));
      this.languageManager = manager;
   }

   protected void init() {
      this.list = new LanguageScreen.List(this.minecraft);
      this.children.add(this.list);
      this.field_211832_i = this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, AbstractOption.FORCE_UNICODE_FONT, AbstractOption.FORCE_UNICODE_FONT.getText(this.gameSettings), (p_213037_1_) -> {
         AbstractOption.FORCE_UNICODE_FONT.nextValue(this.gameSettings);
         this.gameSettings.saveOptions();
         p_213037_1_.setMessage(AbstractOption.FORCE_UNICODE_FONT.getText(this.gameSettings));
         this.minecraft.updateWindowSize();
      }));
      this.confirmSettingsBtn = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.format("gui.done"), (p_213036_1_) -> {
         LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = this.list.getSelected();
         if (languagescreen$list$languageentry != null && !languagescreen$list$languageentry.field_214398_b.getCode().equals(this.languageManager.getCurrentLanguage().getCode())) {
            this.languageManager.setCurrentLanguage(languagescreen$list$languageentry.field_214398_b);
            this.gameSettings.language = languagescreen$list$languageentry.field_214398_b.getCode();
            net.minecraftforge.client.ForgeHooksClient.refreshResources(this.minecraft, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
            this.font.setBidiFlag(this.languageManager.isCurrentLanguageBidirectional());
            this.confirmSettingsBtn.setMessage(I18n.format("gui.done"));
            this.field_211832_i.setMessage(AbstractOption.FORCE_UNICODE_FONT.getText(this.gameSettings));
            this.gameSettings.saveOptions();
         }

         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      super.init();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.list.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends ExtendedList<LanguageScreen.List.LanguageEntry> {
      public List(Minecraft mcIn) {
         super(mcIn, LanguageScreen.this.width, LanguageScreen.this.height, 32, LanguageScreen.this.height - 65 + 4, 18);

         for(Language language : LanguageScreen.this.languageManager.getLanguages()) {
            LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = new LanguageScreen.List.LanguageEntry(language);
            this.addEntry(languagescreen$list$languageentry);
            if (LanguageScreen.this.languageManager.getCurrentLanguage().getCode().equals(language.getCode())) {
               this.setSelected(languagescreen$list$languageentry);
            }
         }

         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }

      }

      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public void setSelected(@Nullable LanguageScreen.List.LanguageEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", p_setSelected_1_.field_214398_b)).getString());
         }

      }

      protected void renderBackground() {
         LanguageScreen.this.renderBackground();
      }

      protected boolean isFocused() {
         return LanguageScreen.this.getFocused() == this;
      }

      @OnlyIn(Dist.CLIENT)
      public class LanguageEntry extends ExtendedList.AbstractListEntry<LanguageScreen.List.LanguageEntry> {
         private final Language field_214398_b;

         public LanguageEntry(Language p_i50494_2_) {
            this.field_214398_b = p_i50494_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            LanguageScreen.this.font.setBidiFlag(true);
            List.this.drawCenteredString(LanguageScreen.this.font, this.field_214398_b.toString(), List.this.width / 2, p_render_2_ + 1, 16777215);
            LanguageScreen.this.font.setBidiFlag(LanguageScreen.this.languageManager.getCurrentLanguage().isBidirectional());
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               this.func_214395_a();
               return true;
            } else {
               return false;
            }
         }

         private void func_214395_a() {
            List.this.setSelected(this);
         }
      }
   }
}