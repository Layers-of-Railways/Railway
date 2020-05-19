package net.minecraft.client.renderer.color;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShearableDoublePlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockColors {
   // FORGE: Use RegistryDelegates as non-Vanilla block ids are not constant
   private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Block>, IBlockColor> colors = new java.util.HashMap<>();
   private final Map<Block, Set<IProperty<?>>> colorStates = Maps.newHashMap();

   public static BlockColors init() {
      BlockColors blockcolors = new BlockColors();
      blockcolors.register((p_228065_0_, p_228065_1_, p_228065_2_, p_228065_3_) -> {
         return p_228065_1_ != null && p_228065_2_ != null ? BiomeColors.getGrassColor(p_228065_1_, p_228065_0_.get(ShearableDoublePlantBlock.PLANT_HALF) == DoubleBlockHalf.UPPER ? p_228065_2_.down() : p_228065_2_) : -1;
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.addColorState(ShearableDoublePlantBlock.PLANT_HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.register((p_228064_0_, p_228064_1_, p_228064_2_, p_228064_3_) -> {
         return p_228064_1_ != null && p_228064_2_ != null ? BiomeColors.getGrassColor(p_228064_1_, p_228064_2_) : GrassColors.get(0.5D, 1.0D);
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
      blockcolors.register((p_228063_0_, p_228063_1_, p_228063_2_, p_228063_3_) -> {
         return FoliageColors.getSpruce();
      }, Blocks.SPRUCE_LEAVES);
      blockcolors.register((p_228062_0_, p_228062_1_, p_228062_2_, p_228062_3_) -> {
         return FoliageColors.getBirch();
      }, Blocks.BIRCH_LEAVES);
      blockcolors.register((p_228061_0_, p_228061_1_, p_228061_2_, p_228061_3_) -> {
         return p_228061_1_ != null && p_228061_2_ != null ? BiomeColors.getFoliageColor(p_228061_1_, p_228061_2_) : FoliageColors.getDefault();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
      blockcolors.register((p_228060_0_, p_228060_1_, p_228060_2_, p_228060_3_) -> {
         return p_228060_1_ != null && p_228060_2_ != null ? BiomeColors.getWaterColor(p_228060_1_, p_228060_2_) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
      blockcolors.register((p_228059_0_, p_228059_1_, p_228059_2_, p_228059_3_) -> {
         return RedstoneWireBlock.colorMultiplier(p_228059_0_.get(RedstoneWireBlock.POWER));
      }, Blocks.REDSTONE_WIRE);
      blockcolors.addColorState(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      blockcolors.register((p_228058_0_, p_228058_1_, p_228058_2_, p_228058_3_) -> {
         return p_228058_1_ != null && p_228058_2_ != null ? BiomeColors.getGrassColor(p_228058_1_, p_228058_2_) : -1;
      }, Blocks.SUGAR_CANE);
      blockcolors.register((p_228057_0_, p_228057_1_, p_228057_2_, p_228057_3_) -> {
         return 14731036;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      blockcolors.register((p_228056_0_, p_228056_1_, p_228056_2_, p_228056_3_) -> {
         int i = p_228056_0_.get(StemBlock.AGE);
         int j = i * 32;
         int k = 255 - i * 8;
         int l = i * 4;
         return j << 16 | k << 8 | l;
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.addColorState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.register((p_228055_0_, p_228055_1_, p_228055_2_, p_228055_3_) -> {
         return p_228055_1_ != null && p_228055_2_ != null ? 2129968 : 7455580;
      }, Blocks.LILY_PAD);
      net.minecraftforge.client.ForgeHooksClient.onBlockColorsInit(blockcolors);
      return blockcolors;
   }

   public int getColorOrMaterialColor(BlockState state, World worldIn, BlockPos blockPosIn) {
      IBlockColor iblockcolor = this.colors.get(state.getBlock().delegate);
      if (iblockcolor != null) {
         return iblockcolor.getColor(state, (ILightReader)null, (BlockPos)null, 0);
      } else {
         MaterialColor materialcolor = state.getMaterialColor(worldIn, blockPosIn);
         return materialcolor != null ? materialcolor.colorValue : -1;
      }
   }

   public int getColor(BlockState blockStateIn, @Nullable ILightReader lightReaderIn, @Nullable BlockPos blockPosIn, int tintIndexIn) {
      IBlockColor iblockcolor = this.colors.get(blockStateIn.getBlock().delegate);
      return iblockcolor == null ? -1 : iblockcolor.getColor(blockStateIn, lightReaderIn, blockPosIn, tintIndexIn);
   }

   public void register(IBlockColor blockColor, Block... blocksIn) {
      for(Block block : blocksIn) {
         this.colors.put(block.delegate, blockColor);
      }

   }

   private void addColorStates(Set<IProperty<?>> propertiesIn, Block... blocksIn) {
      for(Block block : blocksIn) {
         this.colorStates.put(block, propertiesIn);
      }

   }

   private void addColorState(IProperty<?> propertyIn, Block... blocksIn) {
      this.addColorStates(ImmutableSet.of(propertyIn), blocksIn);
   }

   public Set<IProperty<?>> getColorProperties(Block blockIn) {
      return this.colorStates.getOrDefault(blockIn, ImmutableSet.of());
   }
}