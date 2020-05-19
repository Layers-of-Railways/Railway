package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerPatternItem extends Item {
   private final BannerPattern field_219982_a;

   public BannerPatternItem(BannerPattern p_i50057_1_, Item.Properties p_i50057_2_) {
      super(p_i50057_2_);
      this.field_219982_a = p_i50057_1_;
   }

   public BannerPattern func_219980_b() {
      return this.field_219982_a;
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(this.func_219981_d().applyTextStyle(TextFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_219981_d() {
      return new TranslationTextComponent(this.getTranslationKey() + ".desc");
   }
}