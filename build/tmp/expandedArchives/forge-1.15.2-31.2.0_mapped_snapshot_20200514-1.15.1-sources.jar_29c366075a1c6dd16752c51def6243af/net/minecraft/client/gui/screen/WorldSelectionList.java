package net.minecraft.client.gui.screen;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ExtendedList<WorldSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat field_214377_b = new SimpleDateFormat();
   private static final ResourceLocation field_214378_c = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation field_214379_d = new ResourceLocation("textures/gui/world_selection.png");
   private final WorldSelectionScreen worldSelection;
   @Nullable
   private List<WorldSummary> field_212331_y;

   public WorldSelectionList(WorldSelectionScreen p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_, int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_, @Nullable WorldSelectionList p_i49846_9_) {
      super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
      this.worldSelection = p_i49846_1_;
      if (p_i49846_9_ != null) {
         this.field_212331_y = p_i49846_9_.field_212331_y;
      }

      this.func_212330_a(p_i49846_8_, false);
   }

   public void func_212330_a(Supplier<String> p_212330_1_, boolean p_212330_2_) {
      this.clearEntries();
      SaveFormat saveformat = this.minecraft.getSaveLoader();
      if (this.field_212331_y == null || p_212330_2_) {
         try {
            this.field_212331_y = saveformat.getSaveList();
         } catch (AnvilConverterException anvilconverterexception) {
            LOGGER.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.minecraft.displayGuiScreen(new ErrorScreen(new TranslationTextComponent("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
            return;
         }

         Collections.sort(this.field_212331_y);
      }

      String s = p_212330_1_.get().toLowerCase(Locale.ROOT);

      for(WorldSummary worldsummary : this.field_212331_y) {
         if (worldsummary.getDisplayName().toLowerCase(Locale.ROOT).contains(s) || worldsummary.getFileName().toLowerCase(Locale.ROOT).contains(s)) {
            this.addEntry(new WorldSelectionList.Entry(this, worldsummary, this.minecraft.getSaveLoader()));
         }
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.worldSelection.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      if (p_setSelected_1_ != null) {
         WorldSummary worldsummary = p_setSelected_1_.field_214451_d;
         NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", new TranslationTextComponent("narrator.select.world", worldsummary.getDisplayName(), new Date(worldsummary.getLastTimePlayed()), worldsummary.isHardcoreModeEnabled() ? I18n.format("gameMode.hardcore") : I18n.format("gameMode." + worldsummary.getEnumGameType().getName()), worldsummary.getCheatsEnabled() ? I18n.format("selectWorld.cheats") : "", worldsummary.getVersionName()))).getString());
      }

   }

   protected void moveSelection(int p_moveSelection_1_) {
      super.moveSelection(p_moveSelection_1_);
      this.worldSelection.func_214324_a(true);
   }

   public Optional<WorldSelectionList.Entry> func_214376_a() {
      return Optional.ofNullable(this.getSelected());
   }

   public WorldSelectionScreen getGuiWorldSelection() {
      return this.worldSelection;
   }

   @OnlyIn(Dist.CLIENT)
   public final class Entry extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> implements AutoCloseable {
      private final Minecraft field_214449_b;
      private final WorldSelectionScreen field_214450_c;
      private final WorldSummary field_214451_d;
      private final ResourceLocation field_214452_e;
      private File field_214453_f;
      @Nullable
      private final DynamicTexture field_214454_g;
      private long field_214455_h;

      public Entry(WorldSelectionList p_i50631_2_, WorldSummary p_i50631_3_, SaveFormat p_i50631_4_) {
         this.field_214450_c = p_i50631_2_.getGuiWorldSelection();
         this.field_214451_d = p_i50631_3_;
         this.field_214449_b = Minecraft.getInstance();
         this.field_214452_e = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(p_i50631_3_.getFileName()) + "/icon");
         this.field_214453_f = p_i50631_4_.getFile(p_i50631_3_.getFileName(), "icon.png");
         if (!this.field_214453_f.isFile()) {
            this.field_214453_f = null;
         }

         this.field_214454_g = this.func_214446_f();
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         String s = this.field_214451_d.getDisplayName();
         String s1 = this.field_214451_d.getFileName() + " (" + WorldSelectionList.field_214377_b.format(new Date(this.field_214451_d.getLastTimePlayed())) + ")";
         if (StringUtils.isEmpty(s)) {
            s = I18n.format("selectWorld.world") + " " + (p_render_1_ + 1);
         }

         String s2 = "";
         if (this.field_214451_d.requiresConversion()) {
            s2 = I18n.format("selectWorld.conversion") + " " + s2;
         } else {
            s2 = I18n.format("gameMode." + this.field_214451_d.getEnumGameType().getName());
            if (this.field_214451_d.isHardcoreModeEnabled()) {
               s2 = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore") + TextFormatting.RESET;
            }

            if (this.field_214451_d.getCheatsEnabled()) {
               s2 = s2 + ", " + I18n.format("selectWorld.cheats");
            }

            String s3 = this.field_214451_d.getVersionName().getFormattedText();
            if (this.field_214451_d.markVersionInList()) {
               if (this.field_214451_d.askToOpenWorld()) {
                  s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.RED + s3 + TextFormatting.RESET;
               } else {
                  s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.ITALIC + s3 + TextFormatting.RESET;
               }
            } else {
               s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + s3;
            }
         }

         this.field_214449_b.fontRenderer.drawString(s, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         this.field_214449_b.fontRenderer.drawString(s1, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 3), 8421504);
         this.field_214449_b.fontRenderer.drawString(s2, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 9 + 3), 8421504);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_214449_b.getTextureManager().bindTexture(this.field_214454_g != null ? this.field_214452_e : WorldSelectionList.field_214378_c);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         if (this.field_214449_b.gameSettings.touchscreen || p_render_8_) {
            this.field_214449_b.getTextureManager().bindTexture(WorldSelectionList.field_214379_d);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = p_render_6_ - p_render_3_;
            int i = j < 32 ? 32 : 0;
            if (this.field_214451_d.markVersionInList()) {
               AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, (float)i, 32, 32, 256, 256);
               if (this.field_214451_d.func_202842_n()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     ITextComponent itextcomponent = (new TranslationTextComponent("selectWorld.tooltip.unsupported", this.field_214451_d.getVersionName())).applyTextStyle(TextFormatting.RED);
                     this.field_214450_c.setVersionTooltip(this.field_214449_b.fontRenderer.wrapFormattedStringToWidth(itextcomponent.getFormattedText(), 175));
                  }
               } else if (this.field_214451_d.askToOpenWorld()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     this.field_214450_c.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2"));
                  }
               } else if (!SharedConstants.getVersion().isStable()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     this.field_214450_c.setVersionTooltip(TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot1") + "\n" + TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot2"));
                  }
               }
            } else {
               AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, (float)i, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         WorldSelectionList.this.setSelected(this);
         this.field_214450_c.func_214324_a(WorldSelectionList.this.func_214376_a().isPresent());
         if (p_mouseClicked_1_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
            this.func_214438_a();
            return true;
         } else if (Util.milliTime() - this.field_214455_h < 250L) {
            this.func_214438_a();
            return true;
         } else {
            this.field_214455_h = Util.milliTime();
            return false;
         }
      }

      public void func_214438_a() {
         if (!this.field_214451_d.func_197731_n() && !this.field_214451_d.func_202842_n()) {
            if (this.field_214451_d.askToOpenWorld()) {
               this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214434_1_) -> {
                  if (p_214434_1_) {
                     try {
                        this.func_214443_e();
                     } catch (Exception exception) {
                        WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                        this.field_214449_b.displayGuiScreen(new AlertScreen(() -> {
                           this.field_214449_b.displayGuiScreen(this.field_214450_c);
                        }, new TranslationTextComponent("selectWorld.futureworld.error.title"), new TranslationTextComponent("selectWorld.futureworld.error.text")));
                     }
                  } else {
                     this.field_214449_b.displayGuiScreen(this.field_214450_c);
                  }

               }, new TranslationTextComponent("selectWorld.versionQuestion"), new TranslationTextComponent("selectWorld.versionWarning", this.field_214451_d.getVersionName().getFormattedText()), I18n.format("selectWorld.versionJoinButton"), I18n.format("gui.cancel")));
            } else {
               this.func_214443_e();
            }
         } else {
            ITextComponent itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion");
            ITextComponent itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning", this.field_214451_d.getVersionName().getFormattedText(), SharedConstants.getVersion().getName());
            if (this.field_214451_d.func_202842_n()) {
               itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.customized");
               itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.customized");
            }

            this.field_214449_b.displayGuiScreen(new ConfirmBackupScreen(this.field_214450_c, (p_214436_1_, p_214436_2_) -> {
               if (p_214436_1_) {
                  String s = this.field_214451_d.getFileName();
                  EditWorldScreen.createBackup(this.field_214449_b.getSaveLoader(), s);
               }

               this.func_214443_e();
            }, itextcomponent, itextcomponent1, false));
         }

      }

      public void func_214442_b() {
         this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214440_1_) -> {
            if (p_214440_1_) {
               this.field_214449_b.displayGuiScreen(new WorkingScreen());
               SaveFormat saveformat = this.field_214449_b.getSaveLoader();
               saveformat.deleteWorldDirectory(this.field_214451_d.getFileName());
               WorldSelectionList.this.func_212330_a(() -> {
                  return this.field_214450_c.field_212352_g.getText();
               }, true);
            }

            this.field_214449_b.displayGuiScreen(this.field_214450_c);
         }, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", this.field_214451_d.getDisplayName()), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel")));
      }

      public void func_214444_c() {
         this.field_214449_b.displayGuiScreen(new EditWorldScreen((p_214435_1_) -> {
            if (p_214435_1_) {
               WorldSelectionList.this.func_212330_a(() -> {
                  return this.field_214450_c.field_212352_g.getText();
               }, true);
            }

            this.field_214449_b.displayGuiScreen(this.field_214450_c);
         }, this.field_214451_d.getFileName()));
      }

      public void func_214445_d() {
         try {
            this.field_214449_b.displayGuiScreen(new WorkingScreen());
            CreateWorldScreen createworldscreen = new CreateWorldScreen(this.field_214450_c);
            SaveHandler savehandler = this.field_214449_b.getSaveLoader().getSaveLoader(this.field_214451_d.getFileName(), (MinecraftServer)null);
            WorldInfo worldinfo = savehandler.loadWorldInfo();
            if (worldinfo != null) {
               createworldscreen.recreateFromExistingWorld(worldinfo);
               if (this.field_214451_d.func_202842_n()) {
                  this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214439_2_) -> {
                     this.field_214449_b.displayGuiScreen((Screen)(p_214439_2_ ? createworldscreen : this.field_214450_c));
                  }, new TranslationTextComponent("selectWorld.recreate.customized.title"), new TranslationTextComponent("selectWorld.recreate.customized.text"), I18n.format("gui.proceed"), I18n.format("gui.cancel")));
               } else {
                  this.field_214449_b.displayGuiScreen(createworldscreen);
               }
            }
         } catch (Exception exception) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception);
            this.field_214449_b.displayGuiScreen(new AlertScreen(() -> {
               this.field_214449_b.displayGuiScreen(this.field_214450_c);
            }, new TranslationTextComponent("selectWorld.recreate.error.title"), new TranslationTextComponent("selectWorld.recreate.error.text")));
         }

      }

      private void func_214443_e() {
         this.field_214449_b.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.field_214449_b.getSaveLoader().canLoadWorld(this.field_214451_d.getFileName())) {
            this.field_214449_b.launchIntegratedServer(this.field_214451_d.getFileName(), this.field_214451_d.getDisplayName(), (WorldSettings)null);
         }

      }

      @Nullable
      private DynamicTexture func_214446_f() {
         boolean flag = this.field_214453_f != null && this.field_214453_f.isFile();
         if (flag) {
            try (InputStream inputstream = new FileInputStream(this.field_214453_f)) {
               NativeImage nativeimage = NativeImage.read(inputstream);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
               this.field_214449_b.getTextureManager().loadTexture(this.field_214452_e, dynamictexture);
               return dynamictexture;
            } catch (Throwable throwable) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.field_214451_d.getFileName(), throwable);
               this.field_214453_f = null;
               return null;
            }
         } else {
            this.field_214449_b.getTextureManager().deleteTexture(this.field_214452_e);
            return null;
         }
      }

      public void close() {
         if (this.field_214454_g != null) {
            this.field_214454_g.close();
         }

      }
   }
}