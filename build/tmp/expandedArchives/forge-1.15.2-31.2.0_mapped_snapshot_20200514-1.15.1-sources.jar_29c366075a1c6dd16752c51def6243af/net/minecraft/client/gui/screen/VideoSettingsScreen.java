package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.FullscreenResolutionOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VideoSettingsScreen extends SettingsScreen {
   private OptionsRowList optionsRowList;
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AO, AbstractOption.FRAMERATE_LIMIT, AbstractOption.VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS};
   private int mipmapLevels;

   public VideoSettingsScreen(Screen parentScreenIn, GameSettings gameSettingsIn) {
      super(parentScreenIn, gameSettingsIn, new TranslationTextComponent("options.videoTitle"));
   }

   protected void init() {
      this.mipmapLevels = this.gameSettings.mipmapLevels;
      this.optionsRowList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.optionsRowList.addOption(new FullscreenResolutionOption(this.minecraft.getMainWindow()));
      this.optionsRowList.addOption(AbstractOption.BIOME_BLEND_RADIUS);
      this.optionsRowList.addOptions(OPTIONS);
      this.children.add(this.optionsRowList);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.format("gui.done"), (p_213106_1_) -> {
         this.minecraft.gameSettings.saveOptions();
         this.minecraft.getMainWindow().update();
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
   }

   public void removed() {
      if (this.gameSettings.mipmapLevels != this.mipmapLevels) {
         this.minecraft.setMipmapLevels(this.gameSettings.mipmapLevels);
         this.minecraft.scheduleResourcesRefresh();
      }

      super.removed();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      int i = this.gameSettings.guiScale;
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         if (this.gameSettings.guiScale != i) {
            this.minecraft.updateWindowSize();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      int i = this.gameSettings.guiScale;
      if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         return true;
      } else if (this.optionsRowList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         if (this.gameSettings.guiScale != i) {
            this.minecraft.updateWindowSize();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.optionsRowList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}