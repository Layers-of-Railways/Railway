package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List<IResourcePack> resourcePacks = Lists.newArrayList();
   private final ResourcePackType type;
   private final String namespace;

   public FallbackResourceManager(ResourcePackType p_i226096_1_, String p_i226096_2_) {
      this.type = p_i226096_1_;
      this.namespace = p_i226096_2_;
   }

   public void addResourcePack(IResourcePack resourcePack) {
      this.resourcePacks.add(resourcePack);
   }

   public Set<String> getResourceNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   public IResource getResource(ResourceLocation resourceLocationIn) throws IOException {
      this.checkResourcePath(resourceLocationIn);
      IResourcePack iresourcepack = null;
      ResourceLocation resourcelocation = getLocationMcmeta(resourceLocationIn);

      for(int i = this.resourcePacks.size() - 1; i >= 0; --i) {
         IResourcePack iresourcepack1 = this.resourcePacks.get(i);
         if (iresourcepack == null && iresourcepack1.resourceExists(this.type, resourcelocation)) {
            iresourcepack = iresourcepack1;
         }

         if (iresourcepack1.resourceExists(this.type, resourceLocationIn)) {
            InputStream inputstream = null;
            if (iresourcepack != null) {
               inputstream = this.getInputStream(resourcelocation, iresourcepack);
            }

            return new SimpleResource(iresourcepack1.getName(), resourceLocationIn, this.getInputStream(resourceLocationIn, iresourcepack1), inputstream);
         }
      }

      throw new FileNotFoundException(resourceLocationIn.toString());
   }

   public boolean hasResource(ResourceLocation p_219533_1_) {
      if (!this.func_219541_f(p_219533_1_)) {
         return false;
      } else {
         for(int i = this.resourcePacks.size() - 1; i >= 0; --i) {
            IResourcePack iresourcepack = this.resourcePacks.get(i);
            if (iresourcepack.resourceExists(this.type, p_219533_1_)) {
               return true;
            }
         }

         return false;
      }
   }

   protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
      InputStream inputstream = resourcePack.getResourceStream(this.type, location);
      return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakComplainerInputStream(inputstream, location, resourcePack.getName()) : inputstream);
   }

   private void checkResourcePath(ResourceLocation location) throws IOException {
      if (!this.func_219541_f(location)) {
         throw new IOException("Invalid relative path to resource: " + location);
      }
   }

   private boolean func_219541_f(ResourceLocation p_219541_1_) {
      return !p_219541_1_.getPath().contains("..");
   }

   public List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException {
      this.checkResourcePath(resourceLocationIn);
      List<IResource> list = Lists.newArrayList();
      ResourceLocation resourcelocation = getLocationMcmeta(resourceLocationIn);

      for(IResourcePack iresourcepack : this.resourcePacks) {
         if (iresourcepack.resourceExists(this.type, resourceLocationIn)) {
            InputStream inputstream = iresourcepack.resourceExists(this.type, resourcelocation) ? this.getInputStream(resourcelocation, iresourcepack) : null;
            list.add(new SimpleResource(iresourcepack.getName(), resourceLocationIn, this.getInputStream(resourceLocationIn, iresourcepack), inputstream));
         }
      }

      if (list.isEmpty()) {
         throw new FileNotFoundException(resourceLocationIn.toString());
      } else {
         return list;
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(IResourcePack iresourcepack : this.resourcePacks) {
         list.addAll(iresourcepack.getAllResourceLocations(this.type, this.namespace, pathIn, Integer.MAX_VALUE, filter));
      }

      Collections.sort(list);
      return list;
   }

   static ResourceLocation getLocationMcmeta(ResourceLocation location) {
      return new ResourceLocation(location.getNamespace(), location.getPath() + ".mcmeta");
   }

   static class LeakComplainerInputStream extends FilterInputStream {
      private final String message;
      private boolean isClosed;

      public LeakComplainerInputStream(InputStream inputStreamIn, ResourceLocation location, String resourcePack) {
         super(inputStreamIn);
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
         this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + bytearrayoutputstream;
      }

      public void close() throws IOException {
         super.close();
         this.isClosed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.isClosed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }
   }
}