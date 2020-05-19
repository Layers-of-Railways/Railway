package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends ArmorLayer<T, M, A> {
   public BipedArmorLayer(IEntityRenderer<T, M> p_i50936_1_, A p_i50936_2_, A p_i50936_3_) {
      super(p_i50936_1_, p_i50936_2_, p_i50936_3_);
   }

   protected void setModelSlotVisible(A modelIn, EquipmentSlotType slotIn) {
      this.setModelVisible(modelIn);
      switch(slotIn) {
      case HEAD:
         modelIn.bipedHead.showModel = true;
         modelIn.bipedHeadwear.showModel = true;
         break;
      case CHEST:
         modelIn.bipedBody.showModel = true;
         modelIn.bipedRightArm.showModel = true;
         modelIn.bipedLeftArm.showModel = true;
         break;
      case LEGS:
         modelIn.bipedBody.showModel = true;
         modelIn.bipedRightLeg.showModel = true;
         modelIn.bipedLeftLeg.showModel = true;
         break;
      case FEET:
         modelIn.bipedRightLeg.showModel = true;
         modelIn.bipedLeftLeg.showModel = true;
      }

   }

   protected void setModelVisible(A model) {
      model.setVisible(false);
   }
   
   @Override
   protected A getArmorModelHook(T entity, net.minecraft.item.ItemStack itemStack, EquipmentSlotType slot, A model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }
}