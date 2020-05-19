package net.minecraft.world.storage;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

public class MapBanner {
   private final BlockPos pos;
   private final DyeColor color;
   @Nullable
   private final ITextComponent name;

   public MapBanner(BlockPos p_i48876_1_, DyeColor p_i48876_2_, @Nullable ITextComponent p_i48876_3_) {
      this.pos = p_i48876_1_;
      this.color = p_i48876_2_;
      this.name = p_i48876_3_;
   }

   public static MapBanner read(CompoundNBT p_204300_0_) {
      BlockPos blockpos = NBTUtil.readBlockPos(p_204300_0_.getCompound("Pos"));
      DyeColor dyecolor = DyeColor.byTranslationKey(p_204300_0_.getString("Color"), DyeColor.WHITE);
      ITextComponent itextcomponent = p_204300_0_.contains("Name") ? ITextComponent.Serializer.fromJson(p_204300_0_.getString("Name")) : null;
      return new MapBanner(blockpos, dyecolor, itextcomponent);
   }

   @Nullable
   public static MapBanner fromWorld(IBlockReader p_204301_0_, BlockPos p_204301_1_) {
      TileEntity tileentity = p_204301_0_.getTileEntity(p_204301_1_);
      if (tileentity instanceof BannerTileEntity) {
         BannerTileEntity bannertileentity = (BannerTileEntity)tileentity;
         DyeColor dyecolor = bannertileentity.getBaseColor(() -> {
            return p_204301_0_.getBlockState(p_204301_1_);
         });
         ITextComponent itextcomponent = bannertileentity.hasCustomName() ? bannertileentity.getCustomName() : null;
         return new MapBanner(p_204301_1_, dyecolor, itextcomponent);
      } else {
         return null;
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public MapDecoration.Type getDecorationType() {
      switch(this.color) {
      case WHITE:
         return MapDecoration.Type.BANNER_WHITE;
      case ORANGE:
         return MapDecoration.Type.BANNER_ORANGE;
      case MAGENTA:
         return MapDecoration.Type.BANNER_MAGENTA;
      case LIGHT_BLUE:
         return MapDecoration.Type.BANNER_LIGHT_BLUE;
      case YELLOW:
         return MapDecoration.Type.BANNER_YELLOW;
      case LIME:
         return MapDecoration.Type.BANNER_LIME;
      case PINK:
         return MapDecoration.Type.BANNER_PINK;
      case GRAY:
         return MapDecoration.Type.BANNER_GRAY;
      case LIGHT_GRAY:
         return MapDecoration.Type.BANNER_LIGHT_GRAY;
      case CYAN:
         return MapDecoration.Type.BANNER_CYAN;
      case PURPLE:
         return MapDecoration.Type.BANNER_PURPLE;
      case BLUE:
         return MapDecoration.Type.BANNER_BLUE;
      case BROWN:
         return MapDecoration.Type.BANNER_BROWN;
      case GREEN:
         return MapDecoration.Type.BANNER_GREEN;
      case RED:
         return MapDecoration.Type.BANNER_RED;
      case BLACK:
      default:
         return MapDecoration.Type.BANNER_BLACK;
      }
   }

   @Nullable
   public ITextComponent getName() {
      return this.name;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         MapBanner mapbanner = (MapBanner)p_equals_1_;
         return Objects.equals(this.pos, mapbanner.pos) && this.color == mapbanner.color && Objects.equals(this.name, mapbanner.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.pos, this.color, this.name);
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("Pos", NBTUtil.writeBlockPos(this.pos));
      compoundnbt.putString("Color", this.color.getTranslationKey());
      if (this.name != null) {
         compoundnbt.putString("Name", ITextComponent.Serializer.toJson(this.name));
      }

      return compoundnbt;
   }

   public String getMapDecorationId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}