package com.railwayteam.railways.content.entities.handcar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class HandcarRenderer extends EntityRenderer<HandcarEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Railways.MODID, "textures/entity/handcar.png");
    protected final HandcarModel model = new HandcarModel();

    public HandcarRenderer(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getEntityTexture(HandcarEntity p_110775_1_) {
        return TEXTURE;
    }

    @Override
    public void render(HandcarEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight) {
        matrixStack.push();
        matrixStack.translate(0.0D, 0.375D, 0.0D);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - entityYaw));
//        float f = (float)entity.getTimeSinceHit() - partialTicks;
//        float f1 = entity.getDamageTaken() - partialTicks;
//        if (f1 < 0.0F) {
//            f1 = 0.0F;
//        }

//        if (f > 0.0F) {
//            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(f) * f * f1 / 10.0F * (float)entity.getForwardDirection()));
//        }

//        float f2 = entity.getRockingAngle(partialTicks);
//        if (!MathHelper.epsilonEquals(f2, 0.0F)) {
//            matrixStack.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), entity.getRockingAngle(partialTicks), true));
//        }

        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
        this.model.setAngles(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(this.model.getLayer(this.getEntityTexture(entity)));
        this.model.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.pop();
        super.render(entity, entityYaw, partialTicks, matrixStack, renderTypeBuffer, packedLight);
    }
}
