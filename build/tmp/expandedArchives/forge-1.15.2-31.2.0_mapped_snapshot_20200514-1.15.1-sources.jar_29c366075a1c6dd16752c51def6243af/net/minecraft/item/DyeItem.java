package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
   private static final Map<DyeColor, DyeItem> COLOR_DYE_ITEM_MAP = Maps.newEnumMap(DyeColor.class);
   private final DyeColor dyeColor;

   public DyeItem(DyeColor dyeColorIn, Item.Properties builder) {
      super(builder);
      this.dyeColor = dyeColorIn;
      COLOR_DYE_ITEM_MAP.put(dyeColorIn, this);
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
      if (target instanceof SheepEntity) {
         SheepEntity sheepentity = (SheepEntity)target;
         if (sheepentity.isAlive() && !sheepentity.getSheared() && sheepentity.getFleeceColor() != this.dyeColor) {
            sheepentity.setFleeceColor(this.dyeColor);
            stack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static DyeItem getItem(DyeColor color) {
      return COLOR_DYE_ITEM_MAP.get(color);
   }
}