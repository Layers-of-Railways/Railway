package net.minecraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   /**
    * Saves a screenshot in the game directory with a time-stamped filename.
    * Returns an ITextComponent indicating the success/failure of the saving.
    */
   public static void saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
      saveScreenshot(gameDirectory, (String)null, width, height, buffer, messageConsumer);
   }

   /**
    * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
    * Returns an ITextComponent indicating the success/failure of the saving.
    */
   public static void saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer);
         });
      } else {
         saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer);
      }

   }

   private static void saveScreenshotRaw(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
      NativeImage nativeimage = createScreenshot(width, height, buffer);
      File file1 = new File(gameDirectory, "screenshots");
      file1.mkdir();
      File file2;
      if (screenshotName == null) {
         file2 = getTimestampedPNGFileForDirectory(file1);
      } else {
         file2 = new File(file1, screenshotName);
      }


      net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeimage, file2);
      if (event.isCanceled()) {
         messageConsumer.accept(event.getCancelMessage());
         return;
      }

      final File target = event.getScreenshotFile();

      SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
         try {
            nativeimage.write(target);
            ITextComponent itextcomponent = (new StringTextComponent(target.getName())).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle((p_228050_1_) -> {
               p_228050_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, target.getAbsolutePath()));
            });

            if (event.getResultMessage() != null)
               messageConsumer.accept(event.getResultMessage());
            else
            messageConsumer.accept(new TranslationTextComponent("screenshot.success", itextcomponent));
         } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
            messageConsumer.accept(new TranslationTextComponent("screenshot.failure", exception.getMessage()));
         } finally {
            nativeimage.close();
         }

      });
   }

   public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
      width = framebufferIn.framebufferTextureWidth;
      height = framebufferIn.framebufferTextureHeight;
      NativeImage nativeimage = new NativeImage(width, height, false);
      RenderSystem.bindTexture(framebufferIn.framebufferTexture);
      nativeimage.downloadFromTexture(0, true);
      nativeimage.flip();
      return nativeimage;
   }

   /**
    * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone is
    * not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where the
    * filename was unique when this method was called, but another process or thread created a file at the same path
    * immediately after this method returned.
    */
   private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}