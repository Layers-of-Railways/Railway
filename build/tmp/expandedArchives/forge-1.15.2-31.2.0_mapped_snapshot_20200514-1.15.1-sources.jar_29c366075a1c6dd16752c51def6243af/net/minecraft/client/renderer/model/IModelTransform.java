package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IModelTransform extends net.minecraftforge.client.extensions.IForgeModelTransform {
   default TransformationMatrix getRotation() {
      return TransformationMatrix.identity();
   }

   default boolean isUvLock() {
      return false;
   }
}