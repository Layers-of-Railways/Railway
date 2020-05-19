package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IEntityRenderer<T extends Entity, M extends EntityModel<T>> {
   M getEntityModel();

   /**
    * Returns the location of an entity's texture.
    */
   ResourceLocation getEntityTexture(T entity);
}