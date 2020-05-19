package net.minecraft.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientResourcePackInfo extends ResourcePackInfo {
   @Nullable
   private NativeImage field_195809_a;
   @Nullable
   private ResourceLocation field_195810_b;

   @Deprecated
   public ClientResourcePackInfo(String nameIn, boolean isAlwaysEnabled, Supplier<IResourcePack> resourcePackSupplierIn, IResourcePack p_i48113_4_, PackMetadataSection p_i48113_5_, ResourcePackInfo.Priority priorityIn) {
	  this(nameIn, isAlwaysEnabled, resourcePackSupplierIn, p_i48113_4_, p_i48113_5_, priorityIn, false);
   }

   public ClientResourcePackInfo(String nameIn, boolean isAlwaysEnabled, Supplier<IResourcePack> resourcePackSupplierIn, IResourcePack p_i48113_4_, PackMetadataSection p_i48113_5_, ResourcePackInfo.Priority priorityIn, boolean hidden) {
      super(nameIn, isAlwaysEnabled, resourcePackSupplierIn, p_i48113_4_, p_i48113_5_, priorityIn, hidden);
      NativeImage nativeimage = null;

      try (InputStream inputstream = p_i48113_4_.getRootResourceStream("pack.png")) {
         nativeimage = NativeImage.read(inputstream);
      } catch (IllegalArgumentException | IOException var21) {
         ;
      }

      this.field_195809_a = nativeimage;
   }

   @Deprecated
   public ClientResourcePackInfo(String nameIn, boolean isAlwaysEnabled, Supplier<IResourcePack> resourcePackSupplierIn, ITextComponent titleIn, ITextComponent descriptionIn, PackCompatibility compatibilityIn, ResourcePackInfo.Priority priorityIn, boolean isOrderLocked, @Nullable NativeImage p_i48114_9_) {
      this(nameIn, isAlwaysEnabled, resourcePackSupplierIn, titleIn, descriptionIn, compatibilityIn, priorityIn, isOrderLocked, p_i48114_9_, false);
   }

   public ClientResourcePackInfo(String nameIn, boolean isAlwaysEnabled, Supplier<IResourcePack> resourcePackSupplierIn, ITextComponent titleIn, ITextComponent descriptionIn, PackCompatibility compatibilityIn, ResourcePackInfo.Priority priorityIn, boolean isOrderLocked, @Nullable NativeImage p_i48114_9_, boolean hidden) {
      super(nameIn, isAlwaysEnabled, resourcePackSupplierIn, titleIn, descriptionIn, compatibilityIn, priorityIn, isOrderLocked, hidden);
      this.field_195809_a = p_i48114_9_;
   }

   public void func_195808_a(TextureManager p_195808_1_) {
      if (this.field_195810_b == null) {
         if (this.field_195809_a == null) {
            this.field_195810_b = new ResourceLocation("textures/misc/unknown_pack.png");
         } else {
            this.field_195810_b = p_195808_1_.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.field_195809_a));
         }
      }

      p_195808_1_.bindTexture(this.field_195810_b);
   }

   public void close() {
      super.close();
      if (this.field_195809_a != null) {
         this.field_195809_a.close();
         this.field_195809_a = null;
      }

   }
}