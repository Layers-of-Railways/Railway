package net.minecraft.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FilePack extends ResourcePack {
   public static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile zipFile;

   public FilePack(File fileIn) {
      super(fileIn);
   }

   private ZipFile getResourcePackZipFile() throws IOException {
      if (this.zipFile == null) {
         this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
   }

   protected InputStream getInputStream(String resourcePath) throws IOException {
      ZipFile zipfile = this.getResourcePackZipFile();
      ZipEntry zipentry = zipfile.getEntry(resourcePath);
      if (zipentry == null) {
         throw new ResourcePackFileNotFoundException(this.file, resourcePath);
      } else {
         return zipfile.getInputStream(zipentry);
      }
   }

   public boolean resourceExists(String resourcePath) {
      try {
         return this.getResourcePackZipFile().getEntry(resourcePath) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      ZipFile zipfile;
      try {
         zipfile = this.getResourcePackZipFile();
      } catch (IOException var9) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      Set<String> set = Sets.newHashSet();

      while(enumeration.hasMoreElements()) {
         ZipEntry zipentry = enumeration.nextElement();
         String s = zipentry.getName();
         if (s.startsWith(type.getDirectoryName() + "/")) {
            List<String> list = Lists.newArrayList(PATH_SPLITTER.split(s));
            if (list.size() > 1) {
               String s1 = list.get(1);
               if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                  set.add(s1);
               } else {
                  this.onIgnoreNonLowercaseNamespace(s1);
               }
            }
         }
      }

      return set;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly((Closeable)this.zipFile);
         this.zipFile = null;
      }

   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
      ZipFile zipfile;
      try {
         zipfile = this.getResourcePackZipFile();
      } catch (IOException var15) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      List<ResourceLocation> list = Lists.newArrayList();
      String s = type.getDirectoryName() + "/" + namespaceIn + "/";
      String s1 = s + pathIn + "/";

      while(enumeration.hasMoreElements()) {
         ZipEntry zipentry = enumeration.nextElement();
         if (!zipentry.isDirectory()) {
            String s2 = zipentry.getName();
            if (!s2.endsWith(".mcmeta") && s2.startsWith(s1)) {
               String s3 = s2.substring(s.length());
               String[] astring = s3.split("/");
               if (astring.length >= maxDepthIn + 1 && filterIn.test(astring[astring.length - 1])) {
                  list.add(new ResourceLocation(namespaceIn, s3));
               }
            }
         }
      }

      return list;
   }
}