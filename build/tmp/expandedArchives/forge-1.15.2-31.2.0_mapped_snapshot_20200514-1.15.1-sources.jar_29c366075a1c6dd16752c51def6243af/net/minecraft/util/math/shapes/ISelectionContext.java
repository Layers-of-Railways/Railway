package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public interface ISelectionContext extends net.minecraftforge.common.extensions.IForgeSelectionContext {
   static ISelectionContext dummy() {
      return EntitySelectionContext.DUMMY;
   }

   static ISelectionContext forEntity(Entity entityIn) {
      return new EntitySelectionContext(entityIn);
   }

   boolean func_225581_b_();

   boolean func_216378_a(VoxelShape shape, BlockPos pos, boolean p_216378_3_);

   boolean hasItem(Item itemIn);
}