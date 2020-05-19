package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperChargeLayer extends EnergyLayer<CreeperEntity, CreeperModel<CreeperEntity>> {
   private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel<CreeperEntity> creeperModel = new CreeperModel<>(2.0F);

   public CreeperChargeLayer(IEntityRenderer<CreeperEntity, CreeperModel<CreeperEntity>> p_i50947_1_) {
      super(p_i50947_1_);
   }

   protected float func_225634_a_(float p_225634_1_) {
      return p_225634_1_ * 0.01F;
   }

   protected ResourceLocation func_225633_a_() {
      return LIGHTNING_TEXTURE;
   }

   protected EntityModel<CreeperEntity> func_225635_b_() {
      return this.creeperModel;
   }
}