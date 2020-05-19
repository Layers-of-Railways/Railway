package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
   private static final Logger field_224843_a = LogManager.getLogger();
   private volatile boolean field_224844_b;
   private volatile boolean field_224845_c;
   private volatile boolean field_224846_d;
   private volatile boolean field_224847_e;
   private volatile File field_224848_f;
   private volatile File field_224849_g;
   private volatile HttpGet field_224850_h;
   private Thread field_224851_i;
   private final RequestConfig field_224852_j = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] field_224853_k = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long func_224827_a(String p_224827_1_) {
      CloseableHttpClient closeablehttpclient = null;
      HttpGet httpget = null;

      long i;
      try {
         httpget = new HttpGet(p_224827_1_);
         closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
         CloseableHttpResponse closeablehttpresponse = closeablehttpclient.execute(httpget);
         i = Long.parseLong(closeablehttpresponse.getFirstHeader("Content-Length").getValue());
         return i;
      } catch (Throwable var16) {
         field_224843_a.error("Unable to get content length for download");
         i = 0L;
      } finally {
         if (httpget != null) {
            httpget.releaseConnection();
         }

         if (closeablehttpclient != null) {
            try {
               closeablehttpclient.close();
            } catch (IOException ioexception) {
               field_224843_a.error("Could not close http client", (Throwable)ioexception);
            }
         }

      }

      return i;
   }

   public void func_224830_a(WorldDownload p_224830_1_, String p_224830_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_224830_3_, RealmsAnvilLevelStorageSource p_224830_4_) {
      if (this.field_224851_i == null) {
         this.field_224851_i = new Thread(() -> {
            CloseableHttpClient closeablehttpclient = null;

            try {
               this.field_224848_f = File.createTempFile("backup", ".tar.gz");
               this.field_224850_h = new HttpGet(p_224830_1_.downloadLink);
               closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
               HttpResponse httpresponse = closeablehttpclient.execute(this.field_224850_h);
               p_224830_3_.field_225140_b = Long.parseLong(httpresponse.getFirstHeader("Content-Length").getValue());
               if (httpresponse.getStatusLine().getStatusCode() == 200) {
                  OutputStream outputstream = new FileOutputStream(this.field_224848_f);
                  FileDownload.ProgressListener filedownload$progresslistener = new FileDownload.ProgressListener(p_224830_2_.trim(), this.field_224848_f, p_224830_4_, p_224830_3_, p_224830_1_);
                  FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream = new FileDownload.DownloadCountingOutputStream(outputstream);
                  filedownload$downloadcountingoutputstream.func_224804_a(filedownload$progresslistener);
                  IOUtils.copy(httpresponse.getEntity().getContent(), filedownload$downloadcountingoutputstream);
                  return;
               }

               this.field_224846_d = true;
               this.field_224850_h.abort();
            } catch (Exception exception1) {
               field_224843_a.error("Caught exception while downloading: " + exception1.getMessage());
               this.field_224846_d = true;
               return;
            } finally {
               this.field_224850_h.releaseConnection();
               if (this.field_224848_f != null) {
                  this.field_224848_f.delete();
               }

               if (!this.field_224846_d) {
                  if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                     try {
                        this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                        this.field_224850_h = new HttpGet(p_224830_1_.resourcePackUrl);
                        HttpResponse httpresponse1 = closeablehttpclient.execute(this.field_224850_h);
                        p_224830_3_.field_225140_b = Long.parseLong(httpresponse1.getFirstHeader("Content-Length").getValue());
                        if (httpresponse1.getStatusLine().getStatusCode() != 200) {
                           this.field_224846_d = true;
                           this.field_224850_h.abort();
                           return;
                        }

                        OutputStream outputstream1 = new FileOutputStream(this.field_224848_f);
                        FileDownload.ResourcePackProgressListener filedownload$resourcepackprogresslistener = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_224830_3_, p_224830_1_);
                        FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream1 = new FileDownload.DownloadCountingOutputStream(outputstream1);
                        filedownload$downloadcountingoutputstream1.func_224804_a(filedownload$resourcepackprogresslistener);
                        IOUtils.copy(httpresponse1.getEntity().getContent(), filedownload$downloadcountingoutputstream1);
                     } catch (Exception exception) {
                        field_224843_a.error("Caught exception while downloading: " + exception.getMessage());
                        this.field_224846_d = true;
                     } finally {
                        this.field_224850_h.releaseConnection();
                        if (this.field_224848_f != null) {
                           this.field_224848_f.delete();
                        }

                     }
                  } else {
                     this.field_224845_c = true;
                  }
               }

               if (closeablehttpclient != null) {
                  try {
                     closeablehttpclient.close();
                  } catch (IOException var90) {
                     field_224843_a.error("Failed to close Realms download client");
                  }
               }

            }

         });
         this.field_224851_i.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(field_224843_a));
         this.field_224851_i.start();
      }
   }

   public void func_224834_a() {
      if (this.field_224850_h != null) {
         this.field_224850_h.abort();
      }

      if (this.field_224848_f != null) {
         this.field_224848_f.delete();
      }

      this.field_224844_b = true;
   }

   public boolean func_224835_b() {
      return this.field_224845_c;
   }

   public boolean func_224836_c() {
      return this.field_224846_d;
   }

   public boolean func_224837_d() {
      return this.field_224847_e;
   }

   public static String func_224828_b(String p_224828_0_) {
      p_224828_0_ = p_224828_0_.replaceAll("[\\./\"]", "_");

      for(String s : field_224853_k) {
         if (p_224828_0_.equalsIgnoreCase(s)) {
            p_224828_0_ = "_" + p_224828_0_ + "_";
         }
      }

      return p_224828_0_;
   }

   private void func_224831_a(String p_224831_1_, File p_224831_2_, RealmsAnvilLevelStorageSource p_224831_3_) throws IOException {
      Pattern pattern = Pattern.compile(".*-([0-9]+)$");
      int i = 1;

      for(char c0 : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_224831_1_ = p_224831_1_.replace(c0, '_');
      }

      if (StringUtils.isEmpty(p_224831_1_)) {
         p_224831_1_ = "Realm";
      }

      p_224831_1_ = func_224828_b(p_224831_1_);

      try {
         for(RealmsLevelSummary realmslevelsummary : p_224831_3_.getLevelList()) {
            if (realmslevelsummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(p_224831_1_.toLowerCase(Locale.ROOT))) {
               Matcher matcher = pattern.matcher(realmslevelsummary.getLevelId());
               if (matcher.matches()) {
                  if (Integer.valueOf(matcher.group(1)) > i) {
                     i = Integer.valueOf(matcher.group(1));
                  }
               } else {
                  ++i;
               }
            }
         }
      } catch (Exception exception1) {
         field_224843_a.error("Error getting level list", (Throwable)exception1);
         this.field_224846_d = true;
         return;
      }

      String s;
      if (p_224831_3_.isNewLevelIdAcceptable(p_224831_1_) && i <= 1) {
         s = p_224831_1_;
      } else {
         s = p_224831_1_ + (i == 1 ? "" : "-" + i);
         if (!p_224831_3_.isNewLevelIdAcceptable(s)) {
            boolean flag = false;

            while(!flag) {
               ++i;
               s = p_224831_1_ + (i == 1 ? "" : "-" + i);
               if (p_224831_3_.isNewLevelIdAcceptable(s)) {
                  flag = true;
               }
            }
         }
      }

      TarArchiveInputStream tararchiveinputstream = null;
      File file2 = new File(Realms.getGameDirectoryPath(), "saves");

      try {
         file2.mkdir();
         tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_224831_2_))));

         for(TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry(); tararchiveentry != null; tararchiveentry = tararchiveinputstream.getNextTarEntry()) {
            File file3 = new File(file2, tararchiveentry.getName().replace("world", s));
            if (tararchiveentry.isDirectory()) {
               file3.mkdirs();
            } else {
               file3.createNewFile();
               byte[] abyte = new byte[1024];
               BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file3));
               int j = 0;

               while((j = tararchiveinputstream.read(abyte)) != -1) {
                  bufferedoutputstream.write(abyte, 0, j);
               }

               bufferedoutputstream.close();
               Object object = null;
            }
         }
      } catch (Exception exception) {
         field_224843_a.error("Error extracting world", (Throwable)exception);
         this.field_224846_d = true;
      } finally {
         if (tararchiveinputstream != null) {
            tararchiveinputstream.close();
         }

         if (p_224831_2_ != null) {
            p_224831_2_.delete();
         }

         p_224831_3_.renameLevel(s, s.trim());
         File file1 = new File(file2, s + File.separator + "level.dat");
         Realms.deletePlayerTag(file1);
         this.field_224849_g = new File(file2, s + File.separator + "resources.zip");
      }

   }

   @OnlyIn(Dist.CLIENT)
   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener field_224806_b;

      public DownloadCountingOutputStream(OutputStream p_i51649_2_) {
         super(p_i51649_2_);
      }

      public void func_224804_a(ActionListener p_224804_1_) {
         this.field_224806_b = p_224804_1_;
      }

      protected void afterWrite(int p_afterWrite_1_) throws IOException {
         super.afterWrite(p_afterWrite_1_);
         if (this.field_224806_b != null) {
            this.field_224806_b.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ProgressListener implements ActionListener {
      private final String field_224813_b;
      private final File field_224814_c;
      private final RealmsAnvilLevelStorageSource field_224815_d;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224816_e;
      private final WorldDownload field_224817_f;

      private ProgressListener(String p_i51647_2_, File p_i51647_3_, RealmsAnvilLevelStorageSource p_i51647_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51647_5_, WorldDownload p_i51647_6_) {
         this.field_224813_b = p_i51647_2_;
         this.field_224814_c = p_i51647_3_;
         this.field_224815_d = p_i51647_4_;
         this.field_224816_e = p_i51647_5_;
         this.field_224817_f = p_i51647_6_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.field_224816_e.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.field_224816_e.field_225139_a >= this.field_224816_e.field_225140_b && !FileDownload.this.field_224844_b && !FileDownload.this.field_224846_d) {
            try {
               FileDownload.this.field_224847_e = true;
               FileDownload.this.func_224831_a(this.field_224813_b, this.field_224814_c, this.field_224815_d);
            } catch (IOException ioexception) {
               FileDownload.field_224843_a.error("Error extracting archive", (Throwable)ioexception);
               FileDownload.this.field_224846_d = true;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ResourcePackProgressListener implements ActionListener {
      private final File field_224819_b;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224820_c;
      private final WorldDownload field_224821_d;

      private ResourcePackProgressListener(File p_i51645_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51645_3_, WorldDownload p_i51645_4_) {
         this.field_224819_b = p_i51645_2_;
         this.field_224820_c = p_i51645_3_;
         this.field_224821_d = p_i51645_4_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.field_224820_c.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.field_224820_c.field_225139_a >= this.field_224820_c.field_225140_b && !FileDownload.this.field_224844_b) {
            try {
               String s = Hashing.sha1().hashBytes(Files.toByteArray(this.field_224819_b)).toString();
               if (s.equals(this.field_224821_d.resourcePackHash)) {
                  FileUtils.copyFile(this.field_224819_b, FileDownload.this.field_224849_g);
                  FileDownload.this.field_224845_c = true;
               } else {
                  FileDownload.field_224843_a.error("Resourcepack had wrong hash (expected " + this.field_224821_d.resourcePackHash + ", found " + s + "). Deleting it.");
                  FileUtils.deleteQuietly(this.field_224819_b);
                  FileDownload.this.field_224846_d = true;
               }
            } catch (IOException ioexception) {
               FileDownload.field_224843_a.error("Error copying resourcepack file", (Object)ioexception.getMessage());
               FileDownload.this.field_224846_d = true;
            }
         }

      }
   }
}