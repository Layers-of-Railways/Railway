package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShareToLanScreen extends Screen {
   private final Screen lastScreen;
   private Button allowCheatsButton;
   private Button gameModeButton;
   private String gameMode = "survival";
   private boolean allowCheats;

   public ShareToLanScreen(Screen lastScreenIn) {
      super(new TranslationTextComponent("lanServer.title"));
      this.lastScreen = lastScreenIn;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start"), (p_213082_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         int i = HTTPUtil.getSuitableLanPort();
         ITextComponent itextcomponent;
         if (this.minecraft.getIntegratedServer().shareToLAN(GameType.getByName(this.gameMode), this.allowCheats, i)) {
            itextcomponent = new TranslationTextComponent("commands.publish.started", i);
         } else {
            itextcomponent = new TranslationTextComponent("commands.publish.failed");
         }

         this.minecraft.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
         this.minecraft.func_230150_b_();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213085_1_) -> {
         this.minecraft.displayGuiScreen(this.lastScreen);
      }));
      this.gameModeButton = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode"), (p_213084_1_) -> {
         if ("spectator".equals(this.gameMode)) {
            this.gameMode = "creative";
         } else if ("creative".equals(this.gameMode)) {
            this.gameMode = "adventure";
         } else if ("adventure".equals(this.gameMode)) {
            this.gameMode = "survival";
         } else {
            this.gameMode = "spectator";
         }

         this.updateDisplayNames();
      }));
      this.allowCheatsButton = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands"), (p_213083_1_) -> {
         this.allowCheats = !this.allowCheats;
         this.updateDisplayNames();
      }));
      this.updateDisplayNames();
   }

   private void updateDisplayNames() {
      this.gameModeButton.setMessage(I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode));
      this.allowCheatsButton.setMessage(I18n.format("selectWorld.allowCommands") + ' ' + I18n.format(this.allowCheats ? "options.on" : "options.off"));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.font, I18n.format("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}