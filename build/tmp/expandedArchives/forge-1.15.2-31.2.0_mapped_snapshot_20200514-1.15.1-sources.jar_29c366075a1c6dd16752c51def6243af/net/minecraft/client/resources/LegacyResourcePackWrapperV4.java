package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LegacyResourcePackWrapperV4 implements IResourcePack {
   private static final Map<String, Pair<ChestType, ResourceLocation>> CHESTS = Util.make(Maps.newHashMap(), (p_229288_0_) -> {
      p_229288_0_.put("textures/entity/chest/normal_left.png", new Pair<>(ChestType.LEFT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      p_229288_0_.put("textures/entity/chest/normal_right.png", new Pair<>(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      p_229288_0_.put("textures/entity/chest/normal.png", new Pair<>(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/normal.png")));
      p_229288_0_.put("textures/entity/chest/trapped_left.png", new Pair<>(ChestType.LEFT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      p_229288_0_.put("textures/entity/chest/trapped_right.png", new Pair<>(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      p_229288_0_.put("textures/entity/chest/trapped.png", new Pair<>(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/trapped.png")));
      p_229288_0_.put("textures/entity/chest/christmas_left.png", new Pair<>(ChestType.LEFT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      p_229288_0_.put("textures/entity/chest/christmas_right.png", new Pair<>(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      p_229288_0_.put("textures/entity/chest/christmas.png", new Pair<>(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/christmas.png")));
      p_229288_0_.put("textures/entity/chest/ender.png", new Pair<>(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/ender.png")));
   });
   private static final List<String> PATTERNS = Lists.newArrayList("base", "border", "bricks", "circle", "creeper", "cross", "curly_border", "diagonal_left", "diagonal_right", "diagonal_up_left", "diagonal_up_right", "flower", "globe", "gradient", "gradient_up", "half_horizontal", "half_horizontal_bottom", "half_vertical", "half_vertical_right", "mojang", "rhombus", "skull", "small_stripes", "square_bottom_left", "square_bottom_right", "square_top_left", "square_top_right", "straight_cross", "stripe_bottom", "stripe_center", "stripe_downleft", "stripe_downright", "stripe_left", "stripe_middle", "stripe_right", "stripe_top", "triangle_bottom", "triangle_top", "triangles_bottom", "triangles_top");
   private static final Set<String> SHIELDS = PATTERNS.stream().map((p_229291_0_) -> {
      return "textures/entity/shield/" + p_229291_0_ + ".png";
   }).collect(Collectors.toSet());
   private static final Set<String> BANNERS = PATTERNS.stream().map((p_229287_0_) -> {
      return "textures/entity/banner/" + p_229287_0_ + ".png";
   }).collect(Collectors.toSet());
   public static final ResourceLocation SHIELD_BASE = new ResourceLocation("textures/entity/shield_base.png");
   public static final ResourceLocation BANNER_BASE = new ResourceLocation("textures/entity/banner_base.png");
   public static final ResourceLocation OLD_IRON_GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem.png");
   private final IResourcePack resourcePack;

   public LegacyResourcePackWrapperV4(IResourcePack p_i226053_1_) {
      this.resourcePack = p_i226053_1_;
   }

   public InputStream getRootResourceStream(String fileName) throws IOException {
      return this.resourcePack.getRootResourceStream(fileName);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      if (!"minecraft".equals(location.getNamespace())) {
         return this.resourcePack.resourceExists(type, location);
      } else {
         String s = location.getPath();
         if ("textures/misc/enchanted_item_glint.png".equals(s)) {
            return false;
         } else if ("textures/entity/iron_golem/iron_golem.png".equals(s)) {
            return this.resourcePack.resourceExists(type, OLD_IRON_GOLEM_LOCATION);
         } else if (!"textures/entity/conduit/wind.png".equals(s) && !"textures/entity/conduit/wind_vertical.png".equals(s)) {
            if (SHIELDS.contains(s)) {
               return this.resourcePack.resourceExists(type, SHIELD_BASE) && this.resourcePack.resourceExists(type, location);
            } else if (!BANNERS.contains(s)) {
               Pair<ChestType, ResourceLocation> pair = CHESTS.get(s);
               return pair != null && this.resourcePack.resourceExists(type, pair.getSecond()) ? true : this.resourcePack.resourceExists(type, location);
            } else {
               return this.resourcePack.resourceExists(type, BANNER_BASE) && this.resourcePack.resourceExists(type, location);
            }
         } else {
            return false;
         }
      }
   }

   public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
      if (!"minecraft".equals(location.getNamespace())) {
         return this.resourcePack.getResourceStream(type, location);
      } else {
         String s = location.getPath();
         if ("textures/entity/iron_golem/iron_golem.png".equals(s)) {
            return this.resourcePack.getResourceStream(type, OLD_IRON_GOLEM_LOCATION);
         } else {
            if (SHIELDS.contains(s)) {
               InputStream inputstream2 = func_229286_a_(this.resourcePack.getResourceStream(type, SHIELD_BASE), this.resourcePack.getResourceStream(type, location), 64, 2, 2, 12, 22);
               if (inputstream2 != null) {
                  return inputstream2;
               }
            } else if (BANNERS.contains(s)) {
               InputStream inputstream1 = func_229286_a_(this.resourcePack.getResourceStream(type, BANNER_BASE), this.resourcePack.getResourceStream(type, location), 64, 0, 0, 42, 41);
               if (inputstream1 != null) {
                  return inputstream1;
               }
            } else {
               if ("textures/entity/enderdragon/dragon.png".equals(s) || "textures/entity/enderdragon/dragon_exploding.png".equals(s)) {
                  ByteArrayInputStream bytearrayinputstream;
                  try (NativeImage nativeimage = NativeImage.read(this.resourcePack.getResourceStream(type, location))) {
                     int k = nativeimage.getWidth() / 256;

                     for(int i = 88 * k; i < 200 * k; ++i) {
                        for(int j = 56 * k; j < 112 * k; ++j) {
                           nativeimage.setPixelRGBA(j, i, 0);
                        }
                     }

                     bytearrayinputstream = new ByteArrayInputStream(nativeimage.getBytes());
                  }

                  return bytearrayinputstream;
               }

               if ("textures/entity/conduit/closed_eye.png".equals(s) || "textures/entity/conduit/open_eye.png".equals(s)) {
                  return func_229285_a_(this.resourcePack.getResourceStream(type, location));
               }

               Pair<ChestType, ResourceLocation> pair = CHESTS.get(s);
               if (pair != null) {
                  ChestType chesttype = pair.getFirst();
                  InputStream inputstream = this.resourcePack.getResourceStream(type, pair.getSecond());
                  if (chesttype == ChestType.SINGLE) {
                     return func_229292_d_(inputstream);
                  }

                  if (chesttype == ChestType.LEFT) {
                     return func_229289_b_(inputstream);
                  }

                  if (chesttype == ChestType.RIGHT) {
                     return func_229290_c_(inputstream);
                  }
               }
            }

            return this.resourcePack.getResourceStream(type, location);
         }
      }
   }

   @Nullable
   public static InputStream func_229286_a_(InputStream p_229286_0_, InputStream p_229286_1_, int p_229286_2_, int p_229286_3_, int p_229286_4_, int p_229286_5_, int p_229286_6_) throws IOException {
      ByteArrayInputStream bytearrayinputstream;
      try (
         NativeImage nativeimage1 = NativeImage.read(p_229286_1_);
         NativeImage nativeimage = NativeImage.read(p_229286_0_);
      ) {
         int i = nativeimage.getWidth();
         int j = nativeimage.getHeight();
         if (i != nativeimage1.getWidth() || j != nativeimage1.getHeight()) {
            return null;
         }

         try (NativeImage nativeimage2 = new NativeImage(i, j, true)) {
            int k = i / p_229286_2_;

            for(int l = p_229286_4_ * k; l < p_229286_6_ * k; ++l) {
               for(int i1 = p_229286_3_ * k; i1 < p_229286_5_ * k; ++i1) {
                  int j1 = NativeImage.getRed(nativeimage1.getPixelRGBA(i1, l));
                  int k1 = nativeimage.getPixelRGBA(i1, l);
                  nativeimage2.setPixelRGBA(i1, l, NativeImage.getCombined(j1, NativeImage.getBlue(k1), NativeImage.getGreen(k1), NativeImage.getRed(k1)));
               }
            }

            bytearrayinputstream = new ByteArrayInputStream(nativeimage2.getBytes());
         }
      }

      return bytearrayinputstream;
   }

   public static InputStream func_229285_a_(InputStream p_229285_0_) throws IOException {
      ByteArrayInputStream bytearrayinputstream;
      try (NativeImage nativeimage = NativeImage.read(p_229285_0_)) {
         int i = nativeimage.getWidth();
         int j = nativeimage.getHeight();

         try (NativeImage nativeimage1 = new NativeImage(2 * i, 2 * j, true)) {
            func_229284_a_(nativeimage, nativeimage1, 0, 0, 0, 0, i, j, 1, false, false);
            bytearrayinputstream = new ByteArrayInputStream(nativeimage1.getBytes());
         }
      }

      return bytearrayinputstream;
   }

   public static InputStream func_229289_b_(InputStream p_229289_0_) throws IOException {
      ByteArrayInputStream bytearrayinputstream;
      try (NativeImage nativeimage = NativeImage.read(p_229289_0_)) {
         int i = nativeimage.getWidth();
         int j = nativeimage.getHeight();

         try (NativeImage nativeimage1 = new NativeImage(i / 2, j, true)) {
            int k = j / 64;
            func_229284_a_(nativeimage, nativeimage1, 29, 0, 29, 0, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 59, 0, 14, 0, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 29, 14, 43, 14, 15, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 44, 14, 29, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 58, 14, 14, 14, 15, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 29, 19, 29, 19, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 59, 19, 14, 19, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 29, 33, 43, 33, 15, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 44, 33, 29, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 58, 33, 14, 33, 15, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 2, 0, 2, 0, 1, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 4, 0, 1, 0, 1, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 2, 1, 3, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 3, 1, 2, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 4, 1, 1, 1, 1, 4, k, true, true);
            bytearrayinputstream = new ByteArrayInputStream(nativeimage1.getBytes());
         }
      }

      return bytearrayinputstream;
   }

   public static InputStream func_229290_c_(InputStream p_229290_0_) throws IOException {
      ByteArrayInputStream bytearrayinputstream;
      try (NativeImage nativeimage = NativeImage.read(p_229290_0_)) {
         int i = nativeimage.getWidth();
         int j = nativeimage.getHeight();

         try (NativeImage nativeimage1 = new NativeImage(i / 2, j, true)) {
            int k = j / 64;
            func_229284_a_(nativeimage, nativeimage1, 14, 0, 29, 0, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 44, 0, 14, 0, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 14, 0, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 14, 43, 14, 15, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 73, 14, 14, 14, 15, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 19, 29, 19, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 44, 19, 14, 19, 15, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 33, 0, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 33, 43, 33, 15, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 73, 33, 14, 33, 15, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 1, 0, 2, 0, 1, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 3, 0, 1, 0, 1, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 1, 0, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 1, 1, 3, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 5, 1, 1, 1, 1, 4, k, true, true);
            bytearrayinputstream = new ByteArrayInputStream(nativeimage1.getBytes());
         }
      }

      return bytearrayinputstream;
   }

   public static InputStream func_229292_d_(InputStream p_229292_0_) throws IOException {
      ByteArrayInputStream bytearrayinputstream;
      try (NativeImage nativeimage = NativeImage.read(p_229292_0_)) {
         int i = nativeimage.getWidth();
         int j = nativeimage.getHeight();

         try (NativeImage nativeimage1 = new NativeImage(i, j, true)) {
            int k = j / 64;
            func_229284_a_(nativeimage, nativeimage1, 14, 0, 28, 0, 14, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 28, 0, 14, 0, 14, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 14, 0, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 14, 42, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 28, 14, 28, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 42, 14, 14, 14, 14, 5, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 19, 28, 19, 14, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 28, 19, 14, 19, 14, 14, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 33, 0, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 14, 33, 42, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 28, 33, 28, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 42, 33, 14, 33, 14, 10, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 1, 0, 3, 0, 2, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 3, 0, 1, 0, 2, 1, k, false, true);
            func_229284_a_(nativeimage, nativeimage1, 0, 1, 0, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 1, 1, 4, 1, 2, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 3, 1, 3, 1, 1, 4, k, true, true);
            func_229284_a_(nativeimage, nativeimage1, 4, 1, 1, 1, 2, 4, k, true, true);
            bytearrayinputstream = new ByteArrayInputStream(nativeimage1.getBytes());
         }
      }

      return bytearrayinputstream;
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
      return this.resourcePack.getAllResourceLocations(type, namespaceIn, pathIn, maxDepthIn, filterIn);
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      return this.resourcePack.getResourceNamespaces(type);
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
      return this.resourcePack.getMetadata(deserializer);
   }

   public String getName() {
      return this.resourcePack.getName();
   }

   public void close() throws IOException {
      this.resourcePack.close();
   }

   private static void func_229284_a_(NativeImage p_229284_0_, NativeImage p_229284_1_, int p_229284_2_, int p_229284_3_, int p_229284_4_, int p_229284_5_, int p_229284_6_, int p_229284_7_, int p_229284_8_, boolean p_229284_9_, boolean p_229284_10_) {
      p_229284_7_ = p_229284_7_ * p_229284_8_;
      p_229284_6_ = p_229284_6_ * p_229284_8_;
      p_229284_4_ = p_229284_4_ * p_229284_8_;
      p_229284_5_ = p_229284_5_ * p_229284_8_;
      p_229284_2_ = p_229284_2_ * p_229284_8_;
      p_229284_3_ = p_229284_3_ * p_229284_8_;

      for(int i = 0; i < p_229284_7_; ++i) {
         for(int j = 0; j < p_229284_6_; ++j) {
            p_229284_1_.setPixelRGBA(p_229284_4_ + j, p_229284_5_ + i, p_229284_0_.getPixelRGBA(p_229284_2_ + (p_229284_9_ ? p_229284_6_ - 1 - j : j), p_229284_3_ + (p_229284_10_ ? p_229284_7_ - 1 - i : i)));
         }
      }

   }
}