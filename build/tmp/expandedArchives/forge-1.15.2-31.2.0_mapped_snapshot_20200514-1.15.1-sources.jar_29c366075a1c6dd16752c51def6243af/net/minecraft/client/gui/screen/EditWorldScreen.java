package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private Button saveButton;
   private final BooleanConsumer field_214311_b;
   private TextFieldWidget nameEdit;
   private final String worldId;

   public EditWorldScreen(BooleanConsumer p_i51073_1_, String p_i51073_2_) {
      super(new TranslationTextComponent("selectWorld.edit.title"));
      this.field_214311_b = p_i51073_1_;
      this.worldId = p_i51073_2_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.format("selectWorld.edit.resetIcon"), (p_214309_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         FileUtils.deleteQuietly(saveformat1.getFile(this.worldId, "icon.png"));
         p_214309_1_.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.format("selectWorld.edit.openFolder"), (p_214303_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         Util.getOSType().openFile(saveformat1.getFile(this.worldId, "icon.png").getParentFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.format("selectWorld.edit.backup"), (p_214304_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         createBackup(saveformat1, this.worldId);
         this.field_214311_b.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.format("selectWorld.edit.backupFolder"), (p_214302_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         Path path = saveformat1.getBackupsFolder();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Util.getOSType().openFile(path.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.format("selectWorld.edit.optimize"), (p_214310_1_) -> {
         this.minecraft.displayGuiScreen(new ConfirmBackupScreen(this, (p_214305_1_, p_214305_2_) -> {
            if (p_214305_1_) {
               createBackup(this.minecraft.getSaveLoader(), this.worldId);
            }

            this.minecraft.displayGuiScreen(new OptimizeWorldScreen(this.field_214311_b, this.worldId, this.minecraft.getSaveLoader(), p_214305_2_));
         }, new TranslationTextComponent("optimizeWorld.confirm.title"), new TranslationTextComponent("optimizeWorld.confirm.description"), true));
      }));
      this.saveButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.format("selectWorld.edit.save"), (p_214308_1_) -> {
         this.saveChanges();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.format("gui.cancel"), (p_214306_1_) -> {
         this.field_214311_b.accept(false);
      }));
      button.active = this.minecraft.getSaveLoader().getFile(this.worldId, "icon.png").isFile();
      SaveFormat saveformat = this.minecraft.getSaveLoader();
      WorldInfo worldinfo = saveformat.getWorldInfo(this.worldId);
      String s = worldinfo == null ? "" : worldinfo.getWorldName();
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.format("selectWorld.enterName"));
      this.nameEdit.setText(s);
      this.nameEdit.setResponder((p_214301_1_) -> {
         this.saveButton.active = !p_214301_1_.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.setFocusedDefault(this.nameEdit);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.nameEdit.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameEdit.setText(s);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   /**
    * Saves changes to the world name and closes this GUI.
    */
   private void saveChanges() {
      SaveFormat saveformat = this.minecraft.getSaveLoader();
      saveformat.renameWorld(this.worldId, this.nameEdit.getText().trim());
      this.field_214311_b.accept(true);
   }

   /**
    * Creates a new backup of the given world, and creates a toast on completion.
    */
   public static void createBackup(SaveFormat saveFormat, String worldName) {
      ToastGui toastgui = Minecraft.getInstance().getToastGui();
      long i = 0L;
      IOException ioexception = null;

      try {
         i = saveFormat.createBackup(worldName);
      } catch (IOException ioexception1) {
         ioexception = ioexception1;
      }

      ITextComponent itextcomponent;
      ITextComponent itextcomponent1;
      if (ioexception != null) {
         itextcomponent = new TranslationTextComponent("selectWorld.edit.backupFailed");
         itextcomponent1 = new StringTextComponent(ioexception.getMessage());
      } else {
         itextcomponent = new TranslationTextComponent("selectWorld.edit.backupCreated", worldName);
         itextcomponent1 = new TranslationTextComponent("selectWorld.edit.backupSize", MathHelper.ceil((double)i / 1048576.0D));
      }

      toastgui.add(new SystemToast(SystemToast.Type.WORLD_BACKUP, itextcomponent, itextcomponent1));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}