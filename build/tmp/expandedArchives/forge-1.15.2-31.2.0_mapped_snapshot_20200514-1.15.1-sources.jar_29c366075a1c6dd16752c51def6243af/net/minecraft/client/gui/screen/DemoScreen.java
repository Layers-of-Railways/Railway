package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemoScreen extends Screen {
   private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

   public DemoScreen() {
      super(new TranslationTextComponent("demo.help.title"));
   }

   protected void init() {
      int i = -16;
      this.addButton(new Button(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.buy"), (p_213019_0_) -> {
         p_213019_0_.active = false;
         Util.getOSType().openURI("http://www.minecraft.net/store?source=demo");
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.later"), (p_213018_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         this.minecraft.mouseHelper.grabMouse();
      }));
   }

   public void renderBackground() {
      super.renderBackground();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(DEMO_BACKGROUND_LOCATION);
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      this.blit(i, j, 0, 0, 248, 166);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      int i = (this.width - 248) / 2 + 10;
      int j = (this.height - 166) / 2 + 8;
      this.font.drawString(this.title.getFormattedText(), (float)i, (float)j, 2039583);
      j = j + 12;
      GameSettings gamesettings = this.minecraft.gameSettings;
      this.font.drawString(I18n.format("demo.help.movementShort", gamesettings.keyBindForward.getLocalizedName(), gamesettings.keyBindLeft.getLocalizedName(), gamesettings.keyBindBack.getLocalizedName(), gamesettings.keyBindRight.getLocalizedName()), (float)i, (float)j, 5197647);
      this.font.drawString(I18n.format("demo.help.movementMouse"), (float)i, (float)(j + 12), 5197647);
      this.font.drawString(I18n.format("demo.help.jump", gamesettings.keyBindJump.getLocalizedName()), (float)i, (float)(j + 24), 5197647);
      this.font.drawString(I18n.format("demo.help.inventory", gamesettings.keyBindInventory.getLocalizedName()), (float)i, (float)(j + 36), 5197647);
      this.font.drawSplitString(I18n.format("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}