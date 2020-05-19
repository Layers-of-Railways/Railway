package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern implements net.minecraftforge.common.IExtensibleEnum {
   BASE("base", "b"),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
   SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
   SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
   STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
   STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
   STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
   STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
   STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
   STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
   STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
   STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
   CROSS("cross", "cr", "# #", " # ", "# #"),
   STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
   TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
   TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
   TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
   TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
   DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
   DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
   CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
   RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
   HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
   HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
   BORDER("border", "bo", "###", "# #", "###"),
   CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
   GRADIENT("gradient", "gra", "# #", " # ", " # "),
   GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
   BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
   GLOBE("globe", "glb"),
   CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
   SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
   FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
   MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

   public static final int field_222480_O = values().length;
   public static final int field_222481_P = field_222480_O - 5 - 1;
   private final String fileName;
   private final String hashname;
   private final String[] patterns = new String[3];
   private ItemStack patternItem = ItemStack.EMPTY;

   private BannerPattern(String fileNameIn, String hashNameIn) {
      this.fileName = fileNameIn;
      this.hashname = hashNameIn;
   }

   private BannerPattern(String fileNameIn, String hashNameIn, ItemStack craftingStack) {
      this(fileNameIn, hashNameIn);
      this.patternItem = craftingStack;
   }

   private BannerPattern(String fileNameIn, String hashNameIn, String p_i47247_5_, String p_i47247_6_, String p_i47247_7_) {
      this(fileNameIn, hashNameIn);
      this.patterns[0] = p_i47247_5_;
      this.patterns[1] = p_i47247_6_;
      this.patterns[2] = p_i47247_7_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_226957_a_(boolean p_226957_1_) {
      String s = p_226957_1_ ? "banner" : "shield";
      return new ResourceLocation("entity/" + s + "/" + this.getFileName());
   }

   @OnlyIn(Dist.CLIENT)
   public String getFileName() {
      return this.fileName;
   }

   public String getHashname() {
      return this.hashname;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static BannerPattern byHash(String hash) {
      for(BannerPattern bannerpattern : values()) {
         if (bannerpattern.hashname.equals(hash)) {
            return bannerpattern;
         }
      }

      return null;
   }

   public static BannerPattern create(String enumName, String fileNameIn, String hashNameIn, ItemStack craftingStack) {
      throw new IllegalStateException("Enum not extended");
   }

   public static BannerPattern create(String enumName, String fileNameIn, String hashNameIn, String p_i47247_5_, String p_i47247_6_, String p_i47247_7_) {
      throw new IllegalStateException("Enum not extended");
   }

   public static class Builder {
      private final List<Pair<BannerPattern, DyeColor>> field_222478_a = Lists.newArrayList();

      public BannerPattern.Builder setPatternWithColor(BannerPattern p_222477_1_, DyeColor p_222477_2_) {
         this.field_222478_a.add(Pair.of(p_222477_1_, p_222477_2_));
         return this;
      }

      public ListNBT func_222476_a() {
         ListNBT listnbt = new ListNBT();

         for(Pair<BannerPattern, DyeColor> pair : this.field_222478_a) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Pattern", (pair.getLeft()).hashname);
            compoundnbt.putInt("Color", pair.getRight().getId());
            listnbt.add(compoundnbt);
         }

         return listnbt;
      }
   }
}