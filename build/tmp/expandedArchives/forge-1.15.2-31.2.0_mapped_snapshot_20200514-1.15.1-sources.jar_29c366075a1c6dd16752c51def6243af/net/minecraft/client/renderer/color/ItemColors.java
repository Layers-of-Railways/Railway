package net.minecraft.client.renderer.color;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GrassColors;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemColors {
   // FORGE: Use RegistryDelegates as non-Vanilla item ids are not constant
   private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Item>, IItemColor> colors = new java.util.HashMap<>();

   public static ItemColors init(BlockColors colors) {
      ItemColors itemcolors = new ItemColors();
      itemcolors.register((p_210239_0_, p_210239_1_) -> {
         return p_210239_1_ > 0 ? -1 : ((IDyeableArmorItem)p_210239_0_.getItem()).getColor(p_210239_0_);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
      itemcolors.register((p_210236_0_, p_210236_1_) -> {
         return GrassColors.get(0.5D, 1.0D);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      itemcolors.register((p_210241_0_, p_210241_1_) -> {
         if (p_210241_1_ != 1) {
            return -1;
         } else {
            CompoundNBT compoundnbt = p_210241_0_.getChildTag("Explosion");
            int[] aint = compoundnbt != null && compoundnbt.contains("Colors", 11) ? compoundnbt.getIntArray("Colors") : null;
            if (aint == null) {
               return 9079434;
            } else if (aint.length == 1) {
               return aint[0];
            } else {
               int i = 0;
               int j = 0;
               int k = 0;

               for(int l : aint) {
                  i += (l & 16711680) >> 16;
                  j += (l & '\uff00') >> 8;
                  k += (l & 255) >> 0;
               }

               i = i / aint.length;
               j = j / aint.length;
               k = k / aint.length;
               return i << 16 | j << 8 | k;
            }
         }
      }, Items.FIREWORK_STAR);
      itemcolors.register((p_210238_0_, p_210238_1_) -> {
         return p_210238_1_ > 0 ? -1 : PotionUtils.getColor(p_210238_0_);
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

      for(SpawnEggItem spawneggitem : SpawnEggItem.getEggs()) {
         itemcolors.register((p_198141_1_, p_198141_2_) -> {
            return spawneggitem.getColor(p_198141_2_);
         }, spawneggitem);
      }

      itemcolors.register((p_210235_1_, p_210235_2_) -> {
         BlockState blockstate = ((BlockItem)p_210235_1_.getItem()).getBlock().getDefaultState();
         return colors.getColor(blockstate, (ILightReader)null, (BlockPos)null, p_210235_2_);
      }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      itemcolors.register((p_210242_0_, p_210242_1_) -> {
         return p_210242_1_ == 0 ? PotionUtils.getColor(p_210242_0_) : -1;
      }, Items.TIPPED_ARROW);
      itemcolors.register((p_210237_0_, p_210237_1_) -> {
         return p_210237_1_ == 0 ? -1 : FilledMapItem.getColor(p_210237_0_);
      }, Items.FILLED_MAP);
      net.minecraftforge.client.ForgeHooksClient.onItemColorsInit(itemcolors, colors);
      return itemcolors;
   }

   public int getColor(ItemStack stack, int tintIndex) {
      IItemColor iitemcolor = this.colors.get(stack.getItem().delegate);
      return iitemcolor == null ? -1 : iitemcolor.getColor(stack, tintIndex);
   }

   public void register(IItemColor itemColor, IItemProvider... itemsIn) {
      for(IItemProvider iitemprovider : itemsIn) {
         this.colors.put(iitemprovider.asItem().delegate, itemColor);
      }

   }
}