package net.minecraft.client.gui.advancements;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
enum AdvancementTabType {
   ABOVE(0, 0, 28, 32, 8),
   BELOW(84, 0, 28, 32, 8),
   LEFT(0, 64, 32, 28, 5),
   RIGHT(96, 64, 32, 28, 5);

   public static final int MAX_TABS = java.util.Arrays.stream(values()).mapToInt(e -> e.max).sum();
   private final int textureX;
   private final int textureY;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(int p_i47386_3_, int p_i47386_4_, int widthIn, int heightIn, int p_i47386_7_) {
      this.textureX = p_i47386_3_;
      this.textureY = p_i47386_4_;
      this.width = widthIn;
      this.height = heightIn;
      this.max = p_i47386_7_;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(AbstractGui guiIn, int x, int y, boolean p_192651_4_, int p_192651_5_) {
      int i = this.textureX;
      if (p_192651_5_ > 0) {
         i += this.width;
      }

      if (p_192651_5_ == this.max - 1) {
         i += this.width;
      }

      int j = p_192651_4_ ? this.textureY + this.height : this.textureY;
      guiIn.blit(x + this.getX(p_192651_5_), y + this.getY(p_192651_5_), i, j, this.width, this.height);
   }

   public void drawIcon(int p_192652_1_, int p_192652_2_, int p_192652_3_, ItemRenderer renderItemIn, ItemStack stack) {
      int i = p_192652_1_ + this.getX(p_192652_3_);
      int j = p_192652_2_ + this.getY(p_192652_3_);
      switch(this) {
      case ABOVE:
         i += 6;
         j += 9;
         break;
      case BELOW:
         i += 6;
         j += 6;
         break;
      case LEFT:
         i += 10;
         j += 5;
         break;
      case RIGHT:
         i += 6;
         j += 5;
      }

      renderItemIn.renderItemAndEffectIntoGUI((LivingEntity)null, stack, i, j);
   }

   public int getX(int p_192648_1_) {
      switch(this) {
      case ABOVE:
         return (this.width + 4) * p_192648_1_;
      case BELOW:
         return (this.width + 4) * p_192648_1_;
      case LEFT:
         return -this.width + 4;
      case RIGHT:
         return 248;
      default:
         throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public int getY(int p_192653_1_) {
      switch(this) {
      case ABOVE:
         return -this.height + 4;
      case BELOW:
         return 136;
      case LEFT:
         return this.height * p_192653_1_;
      case RIGHT:
         return this.height * p_192653_1_;
      default:
         throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public boolean func_198891_a(int p_198891_1_, int p_198891_2_, int p_198891_3_, double p_198891_4_, double p_198891_6_) {
      int i = p_198891_1_ + this.getX(p_198891_3_);
      int j = p_198891_2_ + this.getY(p_198891_3_);
      return p_198891_4_ > (double)i && p_198891_4_ < (double)(i + this.width) && p_198891_6_ > (double)j && p_198891_6_ < (double)(j + this.height);
   }
}