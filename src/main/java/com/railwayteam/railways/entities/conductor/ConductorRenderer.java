package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.railwayteam.railways.entities.conductor.engineers_cap.EngineersCapModel;
import com.railwayteam.railways.entities.conductor.engineers_cap.EngineersCapRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.client.model.armor.PotatoArmorModel;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

public class ConductorRenderer extends GeoEntityRenderer<ConductorEntity> {
  public ConductorRenderer(EntityRendererManager renderManager)
  {
    super(renderManager, new ConductorEntityModel());
    this.shadowSize = 0.4F;
  }

  @Override
  public ResourceLocation getEntityTexture(ConductorEntity entity) {
    return new ConductorEntityModel().getTextureLocation(entity);
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
