package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallOrFloorItem {
   public BannerItem(Block p_i48529_1_, Block p_i48529_2_, Item.Properties builder) {
      super(p_i48529_1_, p_i48529_2_, builder);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_1_);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static void appendHoverTextFromTileEntityTag(ItemStack stack, List<ITextComponent> p_185054_1_) {
      CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns")) {
         ListNBT listnbt = compoundnbt.getList("Patterns", 10);

         for(int i = 0; i < listnbt.size() && i < 6; ++i) {
            CompoundNBT compoundnbt1 = listnbt.getCompound(i);
            DyeColor dyecolor = DyeColor.byId(compoundnbt1.getInt("Color"));
            BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt1.getString("Pattern"));
            if (bannerpattern != null) {
               p_185054_1_.add((new TranslationTextComponent("block.minecraft.banner." + bannerpattern.getFileName() + '.' + dyecolor.getTranslationKey())).applyTextStyle(TextFormatting.GRAY));
            }
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      appendHoverTextFromTileEntityTag(stack, tooltip);
   }
}