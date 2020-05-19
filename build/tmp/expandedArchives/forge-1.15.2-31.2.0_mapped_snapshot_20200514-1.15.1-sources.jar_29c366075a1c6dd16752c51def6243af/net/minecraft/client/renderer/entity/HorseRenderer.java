package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class HorseRenderer extends AbstractHorseRenderer<HorseEntity, HorseModel<HorseEntity>> {
   private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();

   public HorseRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new HorseModel<>(0.0F), 1.1F);
      this.addLayer(new LeatherHorseArmorLayer(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(HorseEntity entity) {
      String s = entity.getHorseTexture();
      ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s);
         Minecraft.getInstance().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(entity.getVariantTexturePaths()));
         LAYERED_LOCATION_CACHE.put(s, resourcelocation);
      }

      return resourcelocation;
   }
}