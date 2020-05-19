package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPartRotation {
   public final Vector3f origin;
   public final Direction.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockPartRotation(Vector3f p_i47623_1_, Direction.Axis p_i47623_2_, float p_i47623_3_, boolean p_i47623_4_) {
      this.origin = p_i47623_1_;
      this.axis = p_i47623_2_;
      this.angle = p_i47623_3_;
      this.rescale = p_i47623_4_;
   }
}