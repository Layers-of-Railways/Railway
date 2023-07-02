package com.railwayteam.railways.content.conductor;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConductorSecondaryHeadLayer<T extends ConductorEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final ItemInHandRenderer itemInHandRenderer;
    public ConductorSecondaryHeadLayer(RenderLayerParent<T, M> renderer, EntityModelSet skullModels, ItemInHandRenderer itemInHandRenderer) {
        this(renderer, skullModels, 1.0f, 1.0f, 1.0f, itemInHandRenderer);
    }

    public ConductorSecondaryHeadLayer(RenderLayerParent<T, M> renderer, EntityModelSet skullModels, float scaleX, float scaleY, float scaleZ, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.skullModels = SkullBlockRenderer.createSkullRenderers(skullModels);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, int packedLight,
                       @NotNull T conductor, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemStack = conductor.getSecondaryHeadStack();
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        matrixStack.pushPose();
        matrixStack.scale(this.scaleX, this.scaleY, this.scaleZ);
        if (conductor.isBaby()) {
            matrixStack.translate(0.0, 0.03125, 0.0);
            matrixStack.scale(0.7f, 0.7f, 0.7f);
            matrixStack.translate(0.0, 1.0, 0.0);
        }
        ((HeadedModel)this.getParentModel()).getHead().translateAndRotate(matrixStack);
        if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            CompoundTag compoundTag;
            matrixStack.scale(1.1875f, -1.1875f, -1.1875f);
            GameProfile gameProfile = null;
            if (itemStack.hasTag() && (compoundTag = itemStack.getTag()).contains("SkullOwner", 10)) {
                gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
            }
            matrixStack.translate(-0.5, 0.0, -0.5);
            SkullBlock.Type type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
            SkullModelBase skullModelBase = this.skullModels.get(type);
            RenderType renderType = SkullBlockRenderer.getRenderType(type, gameProfile);
            SkullBlockRenderer.renderSkull(null, 180.0f, limbSwing, matrixStack, buffer, packedLight, skullModelBase, renderType);
        } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlot() != EquipmentSlot.HEAD) {
            CustomHeadLayer.translateToHead(matrixStack, false);
            this.itemInHandRenderer.renderItem(conductor, itemStack, ItemTransforms.TransformType.HEAD, false, matrixStack, buffer, packedLight);
        }
        matrixStack.popPose();
    }
}
