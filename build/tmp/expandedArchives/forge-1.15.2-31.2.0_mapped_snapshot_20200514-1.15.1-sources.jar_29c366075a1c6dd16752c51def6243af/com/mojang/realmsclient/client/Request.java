package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Request<T extends Request<T>> {
   protected HttpURLConnection field_224968_a;
   private boolean field_224970_c;
   protected String field_224969_b;

   public Request(String p_i51788_1_, int p_i51788_2_, int p_i51788_3_) {
      try {
         this.field_224969_b = p_i51788_1_;
         Proxy proxy = RealmsClientConfig.func_224895_a();
         if (proxy != null) {
            this.field_224968_a = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection(proxy);
         } else {
            this.field_224968_a = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection();
         }

         this.field_224968_a.setConnectTimeout(p_i51788_2_);
         this.field_224968_a.setReadTimeout(p_i51788_3_);
      } catch (MalformedURLException malformedurlexception) {
         throw new RealmsHttpException(malformedurlexception.getMessage(), malformedurlexception);
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   public void func_224962_a(String p_224962_1_, String p_224962_2_) {
      func_224967_a(this.field_224968_a, p_224962_1_, p_224962_2_);
   }

   public static void func_224967_a(HttpURLConnection p_224967_0_, String p_224967_1_, String p_224967_2_) {
      String s = p_224967_0_.getRequestProperty("Cookie");
      if (s == null) {
         p_224967_0_.setRequestProperty("Cookie", p_224967_1_ + "=" + p_224967_2_);
      } else {
         p_224967_0_.setRequestProperty("Cookie", s + ";" + p_224967_1_ + "=" + p_224967_2_);
      }

   }

   public int func_224957_a() {
      return func_224964_a(this.field_224968_a);
   }

   public static int func_224964_a(HttpURLConnection p_224964_0_) {
      String s = p_224964_0_.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(s);
      } catch (Exception var3) {
         return 5;
      }
   }

   public int func_224958_b() {
      try {
         this.func_224955_d();
         return this.field_224968_a.getResponseCode();
      } catch (Exception exception) {
         throw new RealmsHttpException(exception.getMessage(), exception);
      }
   }

   public String func_224963_c() {
      try {
         this.func_224955_d();
         String s = null;
         if (this.func_224958_b() >= 400) {
            s = this.func_224954_a(this.field_224968_a.getErrorStream());
         } else {
            s = this.func_224954_a(this.field_224968_a.getInputStream());
         }

         this.func_224950_f();
         return s;
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   private String func_224954_a(InputStream p_224954_1_) throws IOException {
      if (p_224954_1_ == null) {
         return "";
      } else {
         InputStreamReader inputstreamreader = new InputStreamReader(p_224954_1_, "UTF-8");
         StringBuilder stringbuilder = new StringBuilder();

         for(int i = inputstreamreader.read(); i != -1; i = inputstreamreader.read()) {
            stringbuilder.append((char)i);
         }

         return stringbuilder.toString();
      }
   }

   private void func_224950_f() {
      byte[] abyte = new byte[1024];

      try {
         int i = 0;
         InputStream inputstream1 = this.field_224968_a.getInputStream();

         while(inputstream1.read(abyte) > 0) {
            ;
         }

         inputstream1.close();
         return;
      } catch (Exception var10) {
         try {
            InputStream inputstream = this.field_224968_a.getErrorStream();
            int j = 0;
            if (inputstream != null) {
               while(inputstream.read(abyte) > 0) {
                  ;
               }

               inputstream.close();
               return;
            }
         } catch (IOException var9) {
            return;
         }
      } finally {
         if (this.field_224968_a != null) {
            this.field_224968_a.disconnect();
         }

      }

   }

   protected T func_224955_d() {
      if (this.field_224970_c) {
         return (T)this;
      } else {
         T t = this.func_223626_e_();
         this.field_224970_c = true;
         return t;
      }
   }

   protected abstract T func_223626_e_();

   public static Request<?> func_224953_a(String p_224953_0_) {
      return new Request.Get(p_224953_0_, 5000, 60000);
   }

   public static Request<?> func_224960_a(String p_224960_0_, int p_224960_1_, int p_224960_2_) {
      return new Request.Get(p_224960_0_, p_224960_1_, p_224960_2_);
   }

   public static Request<?> func_224951_b(String p_224951_0_, String p_224951_1_) {
      return new Request.Post(p_224951_0_, p_224951_1_, 5000, 60000);
   }

   public static Request<?> func_224959_a(String p_224959_0_, String p_224959_1_, int p_224959_2_, int p_224959_3_) {
      return new Request.Post(p_224959_0_, p_224959_1_, p_224959_2_, p_224959_3_);
   }

   public static Request<?> func_224952_b(String p_224952_0_) {
      return new Request.Delete(p_224952_0_, 5000, 60000);
   }

   public static Request<?> func_224965_c(String p_224965_0_, String p_224965_1_) {
      return new Request.Put(p_224965_0_, p_224965_1_, 5000, 60000);
   }

   public static Request<?> func_224966_b(String p_224966_0_, String p_224966_1_, int p_224966_2_, int p_224966_3_) {
      return new Request.Put(p_224966_0_, p_224966_1_, p_224966_2_, p_224966_3_);
   }

   public String func_224956_c(String p_224956_1_) {
      return func_224961_a(this.field_224968_a, p_224956_1_);
   }

   public static String func_224961_a(HttpURLConnection p_224961_0_, String p_224961_1_) {
      try {
         return p_224961_0_.getHeaderField(p_224961_1_);
      } catch (Exception var3) {
         return "";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Delete extends Request<Request.Delete> {
      public Delete(String p_i51800_1_, int p_i51800_2_, int p_i51800_3_) {
         super(p_i51800_1_, p_i51800_2_, p_i51800_3_);
      }

      public Request.Delete func_223626_e_() {
         try {
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setRequestMethod("DELETE");
            this.field_224968_a.connect();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Get extends Request<Request.Get> {
      public Get(String p_i51799_1_, int p_i51799_2_, int p_i51799_3_) {
         super(p_i51799_1_, p_i51799_2_, p_i51799_3_);
      }

      public Request.Get func_223626_e_() {
         try {
            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setUseCaches(false);
            this.field_224968_a.setRequestMethod("GET");
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Post extends Request<Request.Post> {
      private final String field_224971_c;

      public Post(String p_i51798_1_, String p_i51798_2_, int p_i51798_3_, int p_i51798_4_) {
         super(p_i51798_1_, p_i51798_3_, p_i51798_4_);
         this.field_224971_c = p_i51798_2_;
      }

      public Request.Post func_223626_e_() {
         try {
            if (this.field_224971_c != null) {
               this.field_224968_a.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setUseCaches(false);
            this.field_224968_a.setRequestMethod("POST");
            OutputStream outputstream = this.field_224968_a.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.field_224971_c);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Put extends Request<Request.Put> {
      private final String field_224972_c;

      public Put(String p_i51797_1_, String p_i51797_2_, int p_i51797_3_, int p_i51797_4_) {
         super(p_i51797_1_, p_i51797_3_, p_i51797_4_);
         this.field_224972_c = p_i51797_2_;
      }

      public Request.Put func_223626_e_() {
         try {
            if (this.field_224972_c != null) {
               this.field_224968_a.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setRequestMethod("PUT");
            OutputStream outputstream = this.field_224968_a.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.field_224972_c);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }
}