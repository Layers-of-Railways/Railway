package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public class AbstractMapItem extends Item {
   public AbstractMapItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns {@code true} if this is a complex item.
    */
   public boolean isComplex() {
      return true;
   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack stack, World worldIn, PlayerEntity player) {
      return null;
   }
}