package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FilledMapItem extends AbstractMapItem {
   public FilledMapItem(Item.Properties builder) {
      super(builder);
   }

   public static ItemStack setupNewMap(World worldIn, int worldX, int worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      createMapData(itemstack, worldIn, worldX, worldZ, scale, trackingPosition, unlimitedTracking, worldIn.dimension.getType());
      return itemstack;
   }

   @Nullable
   public static MapData getData(ItemStack stack, World worldIn) {
      return worldIn.getMapData(getMapName(getMapId(stack)));
   }

   @Nullable
   public static MapData getMapData(ItemStack stack, World worldIn) {
      // FORGE: Add instance method for mods to override
      Item map = stack.getItem();
      if (map instanceof FilledMapItem) {
        return ((FilledMapItem)map).getCustomMapData(stack, worldIn);
      }
      return null;
   }

   @Nullable
   protected MapData getCustomMapData(ItemStack stack, World worldIn) {
      MapData mapdata = getData(stack, worldIn);
      if (mapdata == null && !worldIn.isRemote) {
         mapdata = createMapData(stack, worldIn, worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), 3, false, false, worldIn.dimension.getType());
      }

      return mapdata;
   }

   public static int getMapId(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getTag();
      return compoundnbt != null && compoundnbt.contains("map", 99) ? compoundnbt.getInt("map") : 0;
   }

   private static MapData createMapData(ItemStack stack, World worldIn, int x, int z, int scale, boolean trackingPosition, boolean unlimitedTracking, DimensionType dimensionTypeIn) {
      int i = worldIn.getNextMapId();
      MapData mapdata = new MapData(getMapName(i));
      mapdata.func_212440_a(x, z, scale, trackingPosition, unlimitedTracking, dimensionTypeIn);
      worldIn.registerMapData(mapdata);
      stack.getOrCreateTag().putInt("map", i);
      return mapdata;
   }

   public static String getMapName(int mapId) {
      return "map_" + mapId;
   }

   public void updateMapData(World worldIn, Entity viewer, MapData data) {
      if (worldIn.dimension.getType() == data.dimension && viewer instanceof PlayerEntity) {
         int i = 1 << data.scale;
         int j = data.xCenter;
         int k = data.zCenter;
         int l = MathHelper.floor(viewer.getPosX() - (double)j) / i + 64;
         int i1 = MathHelper.floor(viewer.getPosZ() - (double)k) / i + 64;
         int j1 = 128 / i;
         if (worldIn.dimension.isNether()) {
            j1 /= 2;
         }

         MapData.MapInfo mapdata$mapinfo = data.getMapInfo((PlayerEntity)viewer);
         ++mapdata$mapinfo.step;
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = k1 - l;
                     int j2 = l1 - i1;
                     boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                     int k2 = (j / i + k1 - 64) * i;
                     int l2 = (k / i + l1 - 64) * i;
                     Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
                     Chunk chunk = worldIn.getChunkAt(new BlockPos(k2, 0, l2));
                     if (!chunk.isEmpty()) {
                        ChunkPos chunkpos = chunk.getPos();
                        int i3 = k2 & 15;
                        int j3 = l2 & 15;
                        int k3 = 0;
                        double d1 = 0.0D;
                        if (worldIn.dimension.isNether()) {
                           int l3 = k2 + l2 * 231871;
                           l3 = l3 * l3 * 31287121 + l3 * 11;
                           if ((l3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.getDefaultState().getMaterialColor(worldIn, BlockPos.ZERO), 10);
                           } else {
                              multiset.add(Blocks.STONE.getDefaultState().getMaterialColor(worldIn, BlockPos.ZERO), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
                           BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j4 = 0; j4 < i; ++j4) {
                                 int k4 = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                 BlockState blockstate;
                                 if (k4 <= 1) {
                                    blockstate = Blocks.BEDROCK.getDefaultState();
                                 } else {
                                    while(true) {
                                       --k4;
                                       blockpos$mutable1.setPos(chunkpos.getXStart() + i4 + i3, k4, chunkpos.getZStart() + j4 + j3);
                                       blockstate = chunk.getBlockState(blockpos$mutable1);
                                       if (blockstate.getMaterialColor(worldIn, blockpos$mutable1) != MaterialColor.AIR || k4 <= 0) {
                                          break;
                                       }
                                    }

                                    if (k4 > 0 && !blockstate.getFluidState().isEmpty()) {
                                       int l4 = k4 - 1;
                                       blockpos$mutable.setPos(blockpos$mutable1);

                                       while(true) {
                                          blockpos$mutable.setY(l4--);
                                          BlockState blockstate1 = chunk.getBlockState(blockpos$mutable);
                                          ++k3;
                                          if (l4 <= 0 || blockstate1.getFluidState().isEmpty()) {
                                             break;
                                          }
                                       }

                                       blockstate = this.func_211698_a(worldIn, blockstate, blockpos$mutable1);
                                    }
                                 }

                                 data.removeStaleBanners(worldIn, chunkpos.getXStart() + i4 + i3, chunkpos.getZStart() + j4 + j3);
                                 d1 += (double)k4 / (double)(i * i);
                                 multiset.add(blockstate.getMaterialColor(worldIn, blockpos$mutable1));
                              }
                           }
                        }

                        k3 = k3 / (i * i);
                        double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                        int i5 = 1;
                        if (d2 > 0.6D) {
                           i5 = 2;
                        }

                        if (d2 < -0.6D) {
                           i5 = 0;
                        }

                        MaterialColor materialcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
                        if (materialcolor == MaterialColor.WATER) {
                           d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           i5 = 1;
                           if (d2 < 0.5D) {
                              i5 = 2;
                           }

                           if (d2 > 0.9D) {
                              i5 = 0;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           byte b0 = data.colors[k1 + l1 * 128];
                           byte b1 = (byte)(materialcolor.colorIndex * 4 + i5);
                           if (b0 != b1) {
                              data.colors[k1 + l1 * 128] = b1;
                              data.updateMapData(k1, l1);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private BlockState func_211698_a(World worldIn, BlockState state, BlockPos pos) {
      IFluidState ifluidstate = state.getFluidState();
      return !ifluidstate.isEmpty() && !state.isSolidSide(worldIn, pos, Direction.UP) ? ifluidstate.getBlockState() : state;
   }

   private static boolean func_195954_a(Biome[] p_195954_0_, int p_195954_1_, int p_195954_2_, int p_195954_3_) {
      return p_195954_0_[p_195954_2_ * p_195954_1_ + p_195954_3_ * p_195954_1_ * 128 * p_195954_1_].getDepth() >= 0.0F;
   }

   public static void func_226642_a_(ServerWorld p_226642_0_, ItemStack p_226642_1_) {
      MapData mapdata = getMapData(p_226642_1_, p_226642_0_);
      if (mapdata != null) {
         if (p_226642_0_.dimension.getType() == mapdata.dimension) {
            int i = 1 << mapdata.scale;
            int j = mapdata.xCenter;
            int k = mapdata.zCenter;
            Biome[] abiome = new Biome[128 * i * 128 * i];

            for(int l = 0; l < 128 * i; ++l) {
               for(int i1 = 0; i1 < 128 * i; ++i1) {
                  abiome[l * 128 * i + i1] = p_226642_0_.getBiome(new BlockPos((j / i - 64) * i + i1, 0, (k / i - 64) * i + l));
               }
            }

            for(int l1 = 0; l1 < 128; ++l1) {
               for(int i2 = 0; i2 < 128; ++i2) {
                  if (l1 > 0 && i2 > 0 && l1 < 127 && i2 < 127) {
                     Biome biome = abiome[l1 * i + i2 * i * 128 * i];
                     int j1 = 8;
                     if (func_195954_a(abiome, i, l1 - 1, i2 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1 - 1, i2 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1 - 1, i2)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1 + 1, i2 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1 + 1, i2 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1 + 1, i2)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1, i2 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l1, i2 + 1)) {
                        --j1;
                     }

                     int k1 = 3;
                     MaterialColor materialcolor = MaterialColor.AIR;
                     if (biome.getDepth() < 0.0F) {
                        materialcolor = MaterialColor.ADOBE;
                        if (j1 > 7 && i2 % 2 == 0) {
                           k1 = (l1 + (int)(MathHelper.sin((float)i2 + 0.0F) * 7.0F)) / 8 % 5;
                           if (k1 == 3) {
                              k1 = 1;
                           } else if (k1 == 4) {
                              k1 = 0;
                           }
                        } else if (j1 > 7) {
                           materialcolor = MaterialColor.AIR;
                        } else if (j1 > 5) {
                           k1 = 1;
                        } else if (j1 > 3) {
                           k1 = 0;
                        } else if (j1 > 1) {
                           k1 = 0;
                        }
                     } else if (j1 > 0) {
                        materialcolor = MaterialColor.BROWN;
                        if (j1 > 3) {
                           k1 = 1;
                        } else {
                           k1 = 3;
                        }
                     }

                     if (materialcolor != MaterialColor.AIR) {
                        mapdata.colors[l1 + i2 * 128] = (byte)(materialcolor.colorIndex * 4 + k1);
                        mapdata.updateMapData(l1, i2);
                     }
                  }
               }
            }

         }
      }
   }

   /**
    * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
    * update it's contents.
    */
   public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         MapData mapdata = getMapData(stack, worldIn);
         if (mapdata != null) {
            if (entityIn instanceof PlayerEntity) {
               PlayerEntity playerentity = (PlayerEntity)entityIn;
               mapdata.updateVisiblePlayers(playerentity, stack);
            }

            if (!mapdata.locked && (isSelected || entityIn instanceof PlayerEntity && ((PlayerEntity)entityIn).getHeldItemOffhand() == stack)) {
               this.updateMapData(worldIn, entityIn, mapdata);
            }

         }
      }
   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack stack, World worldIn, PlayerEntity player) {
      return getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
   }

   /**
    * Called when item is crafted/smelted. Used only by maps so far.
    */
   public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
      CompoundNBT compoundnbt = stack.getTag();
      if (compoundnbt != null && compoundnbt.contains("map_scale_direction", 99)) {
         scaleMap(stack, worldIn, compoundnbt.getInt("map_scale_direction"));
         compoundnbt.remove("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_) {
      MapData mapdata = getMapData(p_185063_0_, p_185063_1_);
      if (mapdata != null) {
         createMapData(p_185063_0_, p_185063_1_, mapdata.xCenter, mapdata.zCenter, MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4), mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
      }

   }

   @Nullable
   public static ItemStack func_219992_b(World worldIn, ItemStack stack) {
      MapData mapdata = getMapData(stack, worldIn);
      if (mapdata != null) {
         ItemStack itemstack = stack.copy();
         MapData mapdata1 = createMapData(itemstack, worldIn, 0, 0, mapdata.scale, mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
         mapdata1.copyFrom(mapdata);
         return itemstack;
      } else {
         return null;
      }
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      MapData mapdata = worldIn == null ? null : getMapData(stack, worldIn);
      if (mapdata != null && mapdata.locked) {
         tooltip.add((new TranslationTextComponent("filled_map.locked", getMapId(stack))).applyTextStyle(TextFormatting.GRAY));
      }

      if (flagIn.isAdvanced()) {
         if (mapdata != null) {
            tooltip.add((new TranslationTextComponent("filled_map.id", getMapId(stack))).applyTextStyle(TextFormatting.GRAY));
            tooltip.add((new TranslationTextComponent("filled_map.scale", 1 << mapdata.scale)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add((new TranslationTextComponent("filled_map.level", mapdata.scale, 4)).applyTextStyle(TextFormatting.GRAY));
         } else {
            tooltip.add((new TranslationTextComponent("filled_map.unknown")).applyTextStyle(TextFormatting.GRAY));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("display");
      if (compoundnbt != null && compoundnbt.contains("MapColor", 99)) {
         int i = compoundnbt.getInt("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      BlockState blockstate = context.getWorld().getBlockState(context.getPos());
      if (blockstate.isIn(BlockTags.BANNERS)) {
         if (!context.world.isRemote) {
            MapData mapdata = getMapData(context.getItem(), context.getWorld());
            mapdata.tryAddBanner(context.getWorld(), context.getPos());
         }

         return ActionResultType.SUCCESS;
      } else {
         return super.onItemUse(context);
      }
   }
}