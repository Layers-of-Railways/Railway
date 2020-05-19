package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger field_224696_a = LogManager.getLogger();
   private final RealmsResetWorldScreen field_224697_b;
   private final RealmsLevelSummary field_224698_c;
   private final long field_224699_d;
   private final int field_224700_e;
   private final UploadStatus field_224701_f;
   private final RateLimiter field_224702_g;
   private volatile String field_224703_h;
   private volatile String field_224704_i;
   private volatile String field_224705_j;
   private volatile boolean field_224706_k;
   private volatile boolean field_224707_l;
   private volatile boolean field_224708_m = true;
   private volatile boolean field_224709_n;
   private RealmsButton field_224710_o;
   private RealmsButton field_224711_p;
   private int field_224712_q;
   private static final String[] field_224713_r = new String[]{"", ".", ". .", ". . ."};
   private int field_224714_s;
   private Long field_224715_t;
   private Long field_224716_u;
   private long field_224717_v;
   private static final ReentrantLock field_224718_w = new ReentrantLock();

   public RealmsUploadScreen(long p_i51747_1_, int p_i51747_3_, RealmsResetWorldScreen p_i51747_4_, RealmsLevelSummary p_i51747_5_) {
      this.field_224699_d = p_i51747_1_;
      this.field_224700_e = p_i51747_3_;
      this.field_224697_b = p_i51747_4_;
      this.field_224698_c = p_i51747_5_;
      this.field_224701_f = new UploadStatus();
      this.field_224702_g = RateLimiter.create((double)0.1F);
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224710_o = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsUploadScreen.this.func_224679_c();
         }
      };
      this.buttonsAdd(this.field_224711_p = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsUploadScreen.this.func_224695_d();
         }
      });
      if (!this.field_224709_n) {
         if (this.field_224697_b.field_224455_a == -1) {
            this.func_224682_h();
         } else {
            this.field_224697_b.func_224446_a(this);
         }
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && !this.field_224709_n) {
         this.field_224709_n = true;
         Realms.setScreen(this);
         this.func_224682_h();
      }

   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void func_224679_c() {
      this.field_224697_b.confirmResult(true, 0);
   }

   private void func_224695_d() {
      this.field_224706_k = true;
      Realms.setScreen(this.field_224697_b);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         if (this.field_224708_m) {
            this.func_224695_d();
         } else {
            this.func_224679_c();
         }

         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (!this.field_224707_l && this.field_224701_f.field_224978_a != 0L && this.field_224701_f.field_224978_a == this.field_224701_f.field_224979_b) {
         this.field_224704_i = getLocalizedString("mco.upload.verifying");
         this.field_224711_p.active(false);
      }

      this.drawCenteredString(this.field_224704_i, this.width() / 2, 50, 16777215);
      if (this.field_224708_m) {
         this.func_224678_e();
      }

      if (this.field_224701_f.field_224978_a != 0L && !this.field_224706_k) {
         this.func_224681_f();
         this.func_224664_g();
      }

      if (this.field_224703_h != null) {
         String[] astring = this.field_224703_h.split("\\\\n");

         for(int i = 0; i < astring.length; ++i) {
            this.drawCenteredString(astring[i], this.width() / 2, 110 + 12 * i, 16711680);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private void func_224678_e() {
      int i = this.fontWidth(this.field_224704_i);
      if (this.field_224712_q % 10 == 0) {
         ++this.field_224714_s;
      }

      this.drawString(field_224713_r[this.field_224714_s % field_224713_r.length], this.width() / 2 + i / 2 + 5, 50, 16777215);
   }

   private void func_224681_f() {
      double d0 = this.field_224701_f.field_224978_a.doubleValue() / this.field_224701_f.field_224979_b.doubleValue() * 100.0D;
      if (d0 > 100.0D) {
         d0 = 100.0D;
      }

      this.field_224705_j = String.format(Locale.ROOT, "%.1f", d0);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double d1 = (double)(this.width() / 2 - 100);
      double d2 = 0.5D;
      Tezzelator tezzelator = Tezzelator.instance;
      tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      tezzelator.vertex(d1 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.end();
      RenderSystem.enableTexture();
      this.drawCenteredString(this.field_224705_j + " %", this.width() / 2, 84, 16777215);
   }

   private void func_224664_g() {
      if (this.field_224712_q % 20 == 0) {
         if (this.field_224715_t != null) {
            long i = System.currentTimeMillis() - this.field_224716_u;
            if (i == 0L) {
               i = 1L;
            }

            this.field_224717_v = 1000L * (this.field_224701_f.field_224978_a - this.field_224715_t) / i;
            this.func_224673_c(this.field_224717_v);
         }

         this.field_224715_t = this.field_224701_f.field_224978_a;
         this.field_224716_u = System.currentTimeMillis();
      } else {
         this.func_224673_c(this.field_224717_v);
      }

   }

   private void func_224673_c(long p_224673_1_) {
      if (p_224673_1_ > 0L) {
         int i = this.fontWidth(this.field_224705_j);
         String s = "(" + func_224671_a(p_224673_1_) + ")";
         this.drawString(s, this.width() / 2 + i / 2 + 15, 84, 16777215);
      }

   }

   public static String func_224671_a(long p_224671_0_) {
      int i = 1024;
      if (p_224671_0_ < 1024L) {
         return p_224671_0_ + " B";
      } else {
         int j = (int)(Math.log((double)p_224671_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(j - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)p_224671_0_ / Math.pow(1024.0D, (double)j), s);
      }
   }

   public void tick() {
      super.tick();
      ++this.field_224712_q;
      if (this.field_224704_i != null && this.field_224702_g.tryAcquire(1)) {
         List<String> list = Lists.newArrayList();
         list.add(this.field_224704_i);
         if (this.field_224705_j != null) {
            list.add(this.field_224705_j + "%");
         }

         if (this.field_224703_h != null) {
            list.add(this.field_224703_h);
         }

         Realms.narrateNow(String.join(System.lineSeparator(), list));
      }

   }

   public static RealmsUploadScreen.Unit func_224665_b(long p_224665_0_) {
      if (p_224665_0_ < 1024L) {
         return RealmsUploadScreen.Unit.B;
      } else {
         int i = (int)(Math.log((double)p_224665_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(i - 1) + "";

         try {
            return RealmsUploadScreen.Unit.valueOf(s + "B");
         } catch (Exception var5) {
            return RealmsUploadScreen.Unit.GB;
         }
      }
   }

   public static double func_224691_a(long p_224691_0_, RealmsUploadScreen.Unit p_224691_2_) {
      return p_224691_2_.equals(RealmsUploadScreen.Unit.B) ? (double)p_224691_0_ : (double)p_224691_0_ / Math.pow(1024.0D, (double)p_224691_2_.ordinal());
   }

   public static String func_224667_b(long p_224667_0_, RealmsUploadScreen.Unit p_224667_2_) {
      return String.format("%." + (p_224667_2_.equals(RealmsUploadScreen.Unit.GB) ? "1" : "0") + "f %s", func_224691_a(p_224667_0_, p_224667_2_), p_224667_2_.name());
   }

   private void func_224682_h() {
      this.field_224709_n = true;
      (new Thread(() -> {
         File file1 = null;
         RealmsClient realmsclient = RealmsClient.func_224911_a();
         long i = this.field_224699_d;

         try {
            if (field_224718_w.tryLock(1L, TimeUnit.SECONDS)) {
               this.field_224704_i = getLocalizedString("mco.upload.preparing");
               UploadInfo uploadinfo = null;

               for(int j = 0; j < 20; ++j) {
                  try {
                     if (this.field_224706_k) {
                        this.func_224676_i();
                        return;
                     }

                     uploadinfo = realmsclient.func_224934_h(i, UploadTokenCache.func_225235_a(i));
                     break;
                  } catch (RetryCallException retrycallexception) {
                     Thread.sleep((long)(retrycallexception.field_224985_e * 1000));
                  }
               }

               if (uploadinfo == null) {
                  this.field_224704_i = getLocalizedString("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.func_225234_a(i, uploadinfo.getToken());
               if (!uploadinfo.isWorldClosed()) {
                  this.field_224704_i = getLocalizedString("mco.upload.close.failure");
                  return;
               }

               if (this.field_224706_k) {
                  this.func_224676_i();
                  return;
               }

               File file2 = new File(Realms.getGameDirectoryPath(), "saves");
               file1 = this.func_224675_b(new File(file2, this.field_224698_c.getLevelId()));
               if (this.field_224706_k) {
                  this.func_224676_i();
                  return;
               }

               if (this.func_224692_a(file1)) {
                  this.field_224704_i = getLocalizedString("mco.upload.uploading", new Object[]{this.field_224698_c.getLevelName()});
                  FileUpload fileupload = new FileUpload(file1, this.field_224699_d, this.field_224700_e, uploadinfo, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), this.field_224701_f);
                  fileupload.func_224874_a((p_227992_3_) -> {
                     if (p_227992_3_.field_225179_a >= 200 && p_227992_3_.field_225179_a < 300) {
                        this.field_224707_l = true;
                        this.field_224704_i = getLocalizedString("mco.upload.done");
                        this.field_224710_o.setMessage(getLocalizedString("gui.done"));
                        UploadTokenCache.func_225233_b(i);
                     } else if (p_227992_3_.field_225179_a == 400 && p_227992_3_.field_225180_b != null) {
                        this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.field_225180_b});
                     } else {
                        this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.field_225179_a});
                     }

                  });

                  while(!fileupload.func_224881_b()) {
                     if (this.field_224706_k) {
                        fileupload.func_224878_a();
                        this.func_224676_i();
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException var19) {
                        field_224696_a.error("Failed to check Realms file upload status");
                     }
                  }

                  return;
               }

               long k = file1.length();
               RealmsUploadScreen.Unit realmsuploadscreen$unit = func_224665_b(k);
               RealmsUploadScreen.Unit realmsuploadscreen$unit1 = func_224665_b(5368709120L);
               if (func_224667_b(k, realmsuploadscreen$unit).equals(func_224667_b(5368709120L, realmsuploadscreen$unit1)) && realmsuploadscreen$unit != RealmsUploadScreen.Unit.B) {
                  RealmsUploadScreen.Unit realmsuploadscreen$unit2 = RealmsUploadScreen.Unit.values()[realmsuploadscreen$unit.ordinal() - 1];
                  this.field_224703_h = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.field_224698_c.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(k, realmsuploadscreen$unit2), func_224667_b(5368709120L, realmsuploadscreen$unit2)});
                  return;
               }

               this.field_224703_h = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.field_224698_c.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(k, realmsuploadscreen$unit), func_224667_b(5368709120L, realmsuploadscreen$unit1)});
               return;
            }
         } catch (IOException ioexception) {
            this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{ioexception.getMessage()});
            return;
         } catch (RealmsServiceException realmsserviceexception) {
            this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{realmsserviceexception.toString()});
            return;
         } catch (InterruptedException var23) {
            field_224696_a.error("Could not acquire upload lock");
            return;
         } finally {
            this.field_224707_l = true;
            if (field_224718_w.isHeldByCurrentThread()) {
               field_224718_w.unlock();
               this.field_224708_m = false;
               this.childrenClear();
               this.buttonsAdd(this.field_224710_o);
               if (file1 != null) {
                  field_224696_a.debug("Deleting file " + file1.getAbsolutePath());
                  file1.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void func_224676_i() {
      this.field_224704_i = getLocalizedString("mco.upload.cancelled");
      field_224696_a.debug("Upload was cancelled");
   }

   private boolean func_224692_a(File p_224692_1_) {
      return p_224692_1_.length() < 5368709120L;
   }

   private File func_224675_b(File p_224675_1_) throws IOException {
      TarArchiveOutputStream tararchiveoutputstream = null;

      File file2;
      try {
         File file1 = File.createTempFile("realms-upload-file", ".tar.gz");
         tararchiveoutputstream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file1)));
         tararchiveoutputstream.setLongFileMode(3);
         this.func_224669_a(tararchiveoutputstream, p_224675_1_.getAbsolutePath(), "world", true);
         tararchiveoutputstream.finish();
         file2 = file1;
      } finally {
         if (tararchiveoutputstream != null) {
            tararchiveoutputstream.close();
         }

      }

      return file2;
   }

   private void func_224669_a(TarArchiveOutputStream p_224669_1_, String p_224669_2_, String p_224669_3_, boolean p_224669_4_) throws IOException {
      if (!this.field_224706_k) {
         File file1 = new File(p_224669_2_);
         String s = p_224669_4_ ? p_224669_3_ : p_224669_3_ + file1.getName();
         TarArchiveEntry tararchiveentry = new TarArchiveEntry(file1, s);
         p_224669_1_.putArchiveEntry(tararchiveentry);
         if (file1.isFile()) {
            IOUtils.copy(new FileInputStream(file1), p_224669_1_);
            p_224669_1_.closeArchiveEntry();
         } else {
            p_224669_1_.closeArchiveEntry();
            File[] afile = file1.listFiles();
            if (afile != null) {
               for(File file2 : afile) {
                  this.func_224669_a(p_224669_1_, file2.getAbsolutePath(), s + "/", false);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum Unit {
      B,
      KB,
      MB,
      GB;
   }
}