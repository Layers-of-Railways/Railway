package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex field_195785_b;

   public VirtualAssetsPack(ResourceIndex p_i48115_1_) {
      super("minecraft", "realms");
      this.field_195785_b = p_i48115_1_;
   }

   @Nullable
   protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
      if (type == ResourcePackType.CLIENT_RESOURCES) {
         File file1 = this.field_195785_b.getFile(location);
         if (file1 != null && file1.exists()) {
            try {
               return new FileInputStream(file1);
            } catch (FileNotFoundException var5) {
               ;
            }
         }
      }

      return super.getInputStreamVanilla(type, location);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      if (type == ResourcePackType.CLIENT_RESOURCES) {
         File file1 = this.field_195785_b.getFile(location);
         if (file1 != null && file1.exists()) {
            return true;
         }
      }

      return super.resourceExists(type, location);
   }

   @Nullable
   protected InputStream getInputStreamVanilla(String pathIn) {
      File file1 = this.field_195785_b.getFile(pathIn);
      if (file1 != null && file1.exists()) {
         try {
            return new FileInputStream(file1);
         } catch (FileNotFoundException var4) {
            ;
         }
      }

      return super.getInputStreamVanilla(pathIn);
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
      Collection<ResourceLocation> collection = super.getAllResourceLocations(type, namespaceIn, pathIn, maxDepthIn, filterIn);
      collection.addAll(this.field_195785_b.getFiles(pathIn, namespaceIn, maxDepthIn, filterIn));
      return collection;
   }
}