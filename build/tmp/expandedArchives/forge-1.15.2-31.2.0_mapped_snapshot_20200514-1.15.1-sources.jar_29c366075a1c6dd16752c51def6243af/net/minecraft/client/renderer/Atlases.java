package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.WoodType;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Atlases {
   public static final ResourceLocation SHULKER_BOX_ATLAS = new ResourceLocation("textures/atlas/shulker_boxes.png");
   public static final ResourceLocation BED_ATLAS = new ResourceLocation("textures/atlas/beds.png");
   public static final ResourceLocation BANNER_ATLAS = new ResourceLocation("textures/atlas/banner_patterns.png");
   public static final ResourceLocation SHIELD_ATLAS = new ResourceLocation("textures/atlas/shield_patterns.png");
   public static final ResourceLocation SIGN_ATLAS = new ResourceLocation("textures/atlas/signs.png");
   public static final ResourceLocation CHEST_ATLAS = new ResourceLocation("textures/atlas/chest.png");
   private static final RenderType SHULKER_BOX_TYPE = RenderType.getEntityCutoutNoCull(SHULKER_BOX_ATLAS);
   private static final RenderType BED_TYPE = RenderType.getEntitySolid(BED_ATLAS);
   private static final RenderType BANNER_TYPE = RenderType.getEntityNoOutline(BANNER_ATLAS);
   private static final RenderType SHIELD_TYPE = RenderType.getEntityNoOutline(SHIELD_ATLAS);
   private static final RenderType SIGN_TYPE = RenderType.getEntityCutoutNoCull(SIGN_ATLAS);
   private static final RenderType CHEST_TYPE = RenderType.getEntityCutout(CHEST_ATLAS);
   private static final RenderType SOLID_BLOCK_TYPE = RenderType.getEntitySolid(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
   private static final RenderType CUTOUT_BLOCK_TYPE = RenderType.getEntityCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
   private static final RenderType TRANSLUCENT_BLOCK_TYPE = RenderType.getEntityTranslucent(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
   private static final RenderType TRANSLUCENT_CULL_BLOCK_TYPE = RenderType.getEntityTranslucentCull(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
   public static final Material DEFAULT_SHULKER_TEXTURE = new Material(SHULKER_BOX_ATLAS, new ResourceLocation("entity/shulker/shulker"));
   public static final List<Material> SHULKER_TEXTURES = Stream.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").map((p_228777_0_) -> {
      return new Material(SHULKER_BOX_ATLAS, new ResourceLocation("entity/shulker/shulker_" + p_228777_0_));
   }).collect(ImmutableList.toImmutableList());
   public static final Map<WoodType, Material> SIGN_MATERIALS = WoodType.getValues().collect(Collectors.toMap(Function.identity(), Atlases::getSignMaterial));
   public static final Material[] BED_TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((p_228770_0_) -> {
      return new Material(BED_ATLAS, new ResourceLocation("entity/bed/" + p_228770_0_.getTranslationKey()));
   }).toArray((p_228769_0_) -> {
      return new Material[p_228769_0_];
   });
   public static final Material CHEST_TRAPPED_MATERIAL = getChestMaterial("trapped");
   public static final Material CHEST_TRAPPED_LEFT_MATERIAL = getChestMaterial("trapped_left");
   public static final Material CHEST_TRAPPED_RIGHT_MATERIAL = getChestMaterial("trapped_right");
   public static final Material CHEST_XMAS_MATERIAL = getChestMaterial("christmas");
   public static final Material CHEST_XMAS_LEFT_MATERIAL = getChestMaterial("christmas_left");
   public static final Material CHEST_XMAS_RIGHT_MATERIAL = getChestMaterial("christmas_right");
   public static final Material CHEST_MATERIAL = getChestMaterial("normal");
   public static final Material CHEST_LEFT_MATERIAL = getChestMaterial("normal_left");
   public static final Material CHEST_RIGHT_MATERIAL = getChestMaterial("normal_right");
   public static final Material ENDER_CHEST_MATERIAL = getChestMaterial("ender");

   public static RenderType getBannerType() {
      return BANNER_TYPE;
   }

   public static RenderType getShieldType() {
      return SHIELD_TYPE;
   }

   public static RenderType getBedType() {
      return BED_TYPE;
   }

   public static RenderType getShulkerBoxType() {
      return SHULKER_BOX_TYPE;
   }

   public static RenderType getSignType() {
      return SIGN_TYPE;
   }

   public static RenderType getChestType() {
      return CHEST_TYPE;
   }

   public static RenderType getSolidBlockType() {
      return SOLID_BLOCK_TYPE;
   }

   public static RenderType getCutoutBlockType() {
      return CUTOUT_BLOCK_TYPE;
   }

   public static RenderType getTranslucentBlockType() {
      return TRANSLUCENT_BLOCK_TYPE;
   }

   public static RenderType getTranslucentCullBlockType() {
      return TRANSLUCENT_CULL_BLOCK_TYPE;
   }

   public static void collectAllMaterials(Consumer<Material> p_228775_0_) {
      p_228775_0_.accept(DEFAULT_SHULKER_TEXTURE);
      SHULKER_TEXTURES.forEach(p_228775_0_);

      for(BannerPattern bannerpattern : BannerPattern.values()) {
         p_228775_0_.accept(new Material(BANNER_ATLAS, bannerpattern.func_226957_a_(true)));
         p_228775_0_.accept(new Material(SHIELD_ATLAS, bannerpattern.func_226957_a_(false)));
      }

      SIGN_MATERIALS.values().forEach(p_228775_0_);

      for(Material material : BED_TEXTURES) {
         p_228775_0_.accept(material);
      }

      p_228775_0_.accept(CHEST_TRAPPED_MATERIAL);
      p_228775_0_.accept(CHEST_TRAPPED_LEFT_MATERIAL);
      p_228775_0_.accept(CHEST_TRAPPED_RIGHT_MATERIAL);
      p_228775_0_.accept(CHEST_XMAS_MATERIAL);
      p_228775_0_.accept(CHEST_XMAS_LEFT_MATERIAL);
      p_228775_0_.accept(CHEST_XMAS_RIGHT_MATERIAL);
      p_228775_0_.accept(CHEST_MATERIAL);
      p_228775_0_.accept(CHEST_LEFT_MATERIAL);
      p_228775_0_.accept(CHEST_RIGHT_MATERIAL);
      p_228775_0_.accept(ENDER_CHEST_MATERIAL);
   }

   public static Material getSignMaterial(WoodType p_228773_0_) {
      return new Material(SIGN_ATLAS, new ResourceLocation("entity/signs/" + p_228773_0_.getName()));
   }

   private static Material getChestMaterial(String p_228774_0_) {
      return new Material(CHEST_ATLAS, new ResourceLocation("entity/chest/" + p_228774_0_));
   }

   public static Material getChestMaterial(TileEntity p_228771_0_, ChestType p_228771_1_, boolean p_228771_2_) {
      if (p_228771_2_) {
         return getChestMaterial(p_228771_1_, CHEST_XMAS_MATERIAL, CHEST_XMAS_LEFT_MATERIAL, CHEST_XMAS_RIGHT_MATERIAL);
      } else if (p_228771_0_ instanceof TrappedChestTileEntity) {
         return getChestMaterial(p_228771_1_, CHEST_TRAPPED_MATERIAL, CHEST_TRAPPED_LEFT_MATERIAL, CHEST_TRAPPED_RIGHT_MATERIAL);
      } else {
         return p_228771_0_ instanceof EnderChestTileEntity ? ENDER_CHEST_MATERIAL : getChestMaterial(p_228771_1_, CHEST_MATERIAL, CHEST_LEFT_MATERIAL, CHEST_RIGHT_MATERIAL);
      }
   }

   private static Material getChestMaterial(ChestType p_228772_0_, Material p_228772_1_, Material p_228772_2_, Material p_228772_3_) {
      switch(p_228772_0_) {
      case LEFT:
         return p_228772_2_;
      case RIGHT:
         return p_228772_3_;
      case SINGLE:
      default:
         return p_228772_1_;
      }
   }
}