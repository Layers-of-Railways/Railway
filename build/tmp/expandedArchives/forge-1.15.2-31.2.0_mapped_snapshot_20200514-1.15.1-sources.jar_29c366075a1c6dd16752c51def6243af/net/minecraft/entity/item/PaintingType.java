package net.minecraft.entity.item;

import net.minecraft.util.registry.Registry;

public class PaintingType extends net.minecraftforge.registries.ForgeRegistryEntry<PaintingType> {
   public static final PaintingType KEBAB = register("kebab", 16, 16);
   public static final PaintingType AZTEC = register("aztec", 16, 16);
   public static final PaintingType ALBAN = register("alban", 16, 16);
   public static final PaintingType AZTEC2 = register("aztec2", 16, 16);
   public static final PaintingType BOMB = register("bomb", 16, 16);
   public static final PaintingType PLANT = register("plant", 16, 16);
   public static final PaintingType WASTELAND = register("wasteland", 16, 16);
   public static final PaintingType POOL = register("pool", 32, 16);
   public static final PaintingType COURBET = register("courbet", 32, 16);
   public static final PaintingType SEA = register("sea", 32, 16);
   public static final PaintingType SUNSET = register("sunset", 32, 16);
   public static final PaintingType CREEBET = register("creebet", 32, 16);
   public static final PaintingType WANDERER = register("wanderer", 16, 32);
   public static final PaintingType GRAHAM = register("graham", 16, 32);
   public static final PaintingType MATCH = register("match", 32, 32);
   public static final PaintingType BUST = register("bust", 32, 32);
   public static final PaintingType STAGE = register("stage", 32, 32);
   public static final PaintingType VOID = register("void", 32, 32);
   public static final PaintingType SKULL_AND_ROSES = register("skull_and_roses", 32, 32);
   public static final PaintingType WITHER = register("wither", 32, 32);
   public static final PaintingType FIGHTERS = register("fighters", 64, 32);
   public static final PaintingType POINTER = register("pointer", 64, 64);
   public static final PaintingType PIGSCENE = register("pigscene", 64, 64);
   public static final PaintingType BURNING_SKULL = register("burning_skull", 64, 64);
   public static final PaintingType SKELETON = register("skeleton", 64, 48);
   public static final PaintingType DONKEY_KONG = register("donkey_kong", 64, 48);
   private final int width;
   private final int height;

   private static PaintingType register(String key, int p_221119_1_, int p_221119_2_) {
      return Registry.register(Registry.MOTIVE, key, new PaintingType(p_221119_1_, p_221119_2_));
   }

   public PaintingType(int p_i50222_1_, int p_i50222_2_) {
      this.width = p_i50222_1_;
      this.height = p_i50222_2_;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}