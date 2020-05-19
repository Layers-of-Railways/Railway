package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsScreen extends Screen {
   private static final AbstractOption[] SCREEN_OPTIONS = new AbstractOption[]{AbstractOption.FOV};
   private final Screen lastScreen;
   /** Reference to the GameSettings object. */
   private final GameSettings settings;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty field_213062_f;

   public OptionsScreen(Screen p_i1046_1_, GameSettings p_i1046_2_) {
      super(new TranslationTextComponent("options.title"));
      this.lastScreen = p_i1046_1_;
      this.settings = p_i1046_2_;
   }

   protected void init() {
      int i = 0;

      for(AbstractOption abstractoption : SCREEN_OPTIONS) {
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 - 12 + 24 * (i >> 1);
         this.addButton(abstractoption.createWidget(this.minecraft.gameSettings, j, k, 150));
         ++i;
      }

      if (this.minecraft.world != null) {
         this.field_213062_f = this.minecraft.world.getDifficulty();
         this.difficultyButton = this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(this.field_213062_f), (p_213051_1_) -> {
            this.field_213062_f = Difficulty.byId(this.field_213062_f.getId() + 1);
            this.minecraft.getConnection().sendPacket(new CSetDifficultyPacket(this.field_213062_f));
            this.difficultyButton.setMessage(this.getDifficultyText(this.field_213062_f));
         }));
         if (this.minecraft.isSingleplayer() && !this.minecraft.world.getWorldInfo().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (p_213054_1_) -> {
               this.minecraft.displayGuiScreen(new ConfirmScreen(this::func_213050_a, new TranslationTextComponent("difficulty.lock.title"), new TranslationTextComponent("difficulty.lock.question", new TranslationTextComponent("options.difficulty." + this.minecraft.world.getWorldInfo().getDifficulty().getTranslationKey()))));
            }));
            this.lockButton.setLocked(this.minecraft.world.getWorldInfo().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {
         this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, AbstractOption.REALMS_NOTIFICATIONS, AbstractOption.REALMS_NOTIFICATIONS.getText(this.settings), (p_213057_1_) -> {
            AbstractOption.REALMS_NOTIFICATIONS.nextValue(this.settings);
            this.settings.saveOptions();
            p_213057_1_.setMessage(AbstractOption.REALMS_NOTIFICATIONS.getText(this.settings));
         }));
      }

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation"), (p_213055_1_) -> {
         this.minecraft.displayGuiScreen(new CustomizeSkinScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.sounds"), (p_213061_1_) -> {
         this.minecraft.displayGuiScreen(new OptionsSoundsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.video"), (p_213059_1_) -> {
         this.minecraft.displayGuiScreen(new VideoSettingsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.controls"), (p_213052_1_) -> {
         this.minecraft.displayGuiScreen(new ControlsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.language"), (p_213053_1_) -> {
         this.minecraft.displayGuiScreen(new LanguageScreen(this, this.settings, this.minecraft.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.chat.title"), (p_213049_1_) -> {
         this.minecraft.displayGuiScreen(new ChatOptionsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack"), (p_213060_1_) -> {
         this.minecraft.displayGuiScreen(new ResourcePacksScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.accessibility.title"), (p_213058_1_) -> {
         this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
         this.minecraft.displayGuiScreen(this.lastScreen);
      }));
   }

   public String getDifficultyText(Difficulty p_175355_1_) {
      return (new TranslationTextComponent("options.difficulty")).appendText(": ").appendSibling(p_175355_1_.getDisplayName()).getFormattedText();
   }

   private void func_213050_a(boolean p_213050_1_) {
      this.minecraft.displayGuiScreen(this);
      if (p_213050_1_ && this.minecraft.world != null) {
         this.minecraft.getConnection().sendPacket(new CLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.settings.saveOptions();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @Override
   public void onClose() {
      // We need to consider 2 potential parent screens here:
      // 1. From the main menu, in which case display the main menu
      // 2. From the pause menu, in which case exit back to game
      this.minecraft.displayGuiScreen(this.lastScreen instanceof IngameMenuScreen ? null : this.lastScreen);
   }
}