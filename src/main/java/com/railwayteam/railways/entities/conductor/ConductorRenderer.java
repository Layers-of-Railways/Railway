package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ConductorRenderer extends MobRenderer<ConductorEntity, ConductorEntityModel> {
  public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");

  public ConductorRenderer(EntityRendererManager manager) {
    super(manager, new ConductorEntityModel(), 0.2f);

    this.shadowSize = 0.4F;

//    this.addLayer(new BipedArmorLayer<>(this, new ConductorEntityModel(), new ConductorEntityModel()));
    addLayer(new EngineersCapLayer(this));
  }

  @Override
  public ResourceLocation getEntityTexture(ConductorEntity p_110775_1_) {
    return TEXTURE;
  }
}

//public class EngineerGolemRenderer extends MobRenderer<EngineerGolemEntity, EngineerGolemEntityModel> {
//  private static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");
//
//  public EngineerGolemRenderer(EntityRendererManager manager) {
//    super(manager, new EngineerGolemEntityModel(), 0.2f);
//  }
//
//  @Nonnull
//  @Override
//  public ResourceLocation getEntityTexture(EngineerGolemEntity entity) {
//    return TEXTURE;
//  }
//}
