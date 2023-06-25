package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlockPartials;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConductorRenderer extends MobRenderer<ConductorEntity, ConductorEntityModel<ConductorEntity>> {
  public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");

  public ConductorRenderer (EntityRendererProvider.Context ctx) {
    super (ctx, new ConductorEntityModel<>(ctx.bakeLayer(ConductorEntityModel.LAYER_LOCATION)), 0.2f);
    this.addLayer(new HumanoidArmorLayer<>(this,
      new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
      new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR))
    ));
    this.addLayer(new ConductorToolboxLayer<>(this));
    this.addLayer(new ConductorFlagLayer<>(this));
    this.addLayer(new ConductorRemoteLayer<>(this));
  }

  private ResourceLocation ensurePng(ResourceLocation loc) {
    if (loc.getPath().endsWith(".png")) return loc;
    return new ResourceLocation(loc.getNamespace(), loc.getPath() + ".png");
  }

  @Override
  public @NotNull ResourceLocation getTextureLocation (@NotNull ConductorEntity conductor) {
    ItemStack headItem = conductor.getItemBySlot(EquipmentSlot.HEAD);
    String name = headItem.getHoverName().getString();
    if (!headItem.isEmpty() && headItem.getItem() instanceof ConductorCapItem && CRBlockPartials.CUSTOM_CONDUCTOR_SKINS.containsKey(name)) {
      return ensurePng(CRBlockPartials.CUSTOM_CONDUCTOR_SKINS.get(name));
    }
    return TEXTURE;
  }

  @Override
  public void render(ConductorEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i1) {
    super.render(entity, f1, f2, stack, source, i1);
  }
}
