package net.minecraft.tileentity;

import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndPortalTileEntity extends TileEntity {
   public EndPortalTileEntity(TileEntityType<?> p_i48283_1_) {
      super(p_i48283_1_);
   }

   public EndPortalTileEntity() {
      this(TileEntityType.END_PORTAL);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderFace(Direction face) {
      return face == Direction.UP;
   }
}