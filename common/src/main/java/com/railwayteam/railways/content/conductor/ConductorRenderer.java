/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.client.AccessorLivingEntityRenderer;
import com.railwayteam.railways.registry.CRBlockPartials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConductorRenderer extends MobRenderer<ConductorEntity, ConductorEntityModel<ConductorEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/conductor.png");

    public ConductorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ConductorEntityModel<>(ctx.bakeLayer(ConductorEntityModel.LAYER_LOCATION)), 0.2f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
                new ConductorEntityModel<>(ctx.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR))
        ));
        this.addLayer(new ConductorSecondaryHeadLayer<>(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
        this.addLayer(new ConductorToolboxLayer<>(this));
        this.addLayer(new ConductorFlagLayer<>(this));
        this.addLayer(new ConductorRemoteLayer<>(this));
        this.addLayer(new ConductorElytraLayer<>(this, ctx.getModelSet()));
    }

    private ResourceLocation ensurePng(ResourceLocation loc) {
        if (loc.getPath().endsWith(".png")) return loc;
        return new ResourceLocation(loc.getNamespace(), loc.getPath() + ".png");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ConductorEntity conductor) {
        ItemStack headItem = conductor.getItemBySlot(EquipmentSlot.HEAD);
        String name = headItem.getHoverName().getString();
        if (name.startsWith("[sus]"))
            name = name.substring(5);
        if (!headItem.isEmpty() && headItem.getItem() instanceof ConductorCapItem && CRBlockPartials.CUSTOM_CONDUCTOR_SKINS.containsKey(name)) {
            return ensurePng(CRBlockPartials.CUSTOM_CONDUCTOR_SKINS.get(name));
        }
        if (conductor.getCustomName() != null && CRBlockPartials.CUSTOM_CONDUCTOR_SKINS_FOR_NAME.containsKey(conductor.getCustomName().getString())) {
            return ensurePng(CRBlockPartials.CUSTOM_CONDUCTOR_SKINS_FOR_NAME.get(conductor.getCustomName().getString()));
        }
        return TEXTURE;
    }

    @Override
    public void render(ConductorEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i1) {
        super.render(entity, f1, f2, stack, source, i1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void setupRotations(ConductorEntity entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entityLiving.visualBaseEntity != null) {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityLiving.visualBaseEntity);
            if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
                ((AccessorLivingEntityRenderer) livingRenderer).callSetupRotations(entityLiving.visualBaseEntity, matrixStack, ageInTicks, rotationYaw, partialTicks);
                return;
            }
        }
        super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
    }
}
