package com.railwayteam.railways.content.conductor.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.conductor.ConductorCapItem;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.registry.CRItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ConductorCapItemRenderer implements ArmorRenderer {
	public static void register() {
		ArmorRenderer renderer = new ConductorCapItemRenderer();
		for (ItemEntry<ConductorCapItem> entry : CRItems.ITEM_CONDUCTOR_CAP.values()) {
			ConductorCapItem item = entry.get();
			ArmorRenderer.register(renderer, item);
		}
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity,
					   EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
		if (!(stack.getItem() instanceof ConductorCapItem cap))
			return;
		ConductorCapModel<?> model = ConductorCapModel.of(stack, contextModel, entity);
		ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, cap.textureId);
	}
}
