package com.railwayteam.railways.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Style;

public class NumericalSignalTileEntityRenderer extends TileEntityRenderer<NumericalSignalTileEntity> {
    public NumericalSignalTileEntityRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(NumericalSignalTileEntity tile, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        p_225616_3_.push();
        float f = 0.6666667F;
        p_225616_3_.translate(0.5D, 0, 1.00001D /* .00001 to prevent z-fighting */);
//        p_225616_3_.translate(0.0D, -0.3125D, -0.4375D);

        p_225616_3_.push();
        p_225616_3_.scale(0.6666667F, -0.6666667F, -0.6666667F);
//        RenderMaterial rendermaterial = getModelTexture(blockstate.getBlock());
//        IVertexBuilder ivertexbuilder = rendermaterial.getVertexConsumer(p_225616_4_, this.model::getLayer);
//        this.model.field_78166_a.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
//        this.model.field_78165_b.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
        p_225616_3_.pop();
        FontRenderer fontrenderer = this.dispatcher.getFontRenderer();
        float f2 = 0.010416667F;
//        p_225616_3_.translate(0.0D, (double)0.33333334F, (double)0.046666667F);
        p_225616_3_.scale(0.03F, -0.03F, 0.03F);

        int color = NativeImage.getAbgrColor(0, 255, 255, 255);

        String str = String.valueOf(tile.getPower());
//        String str = "power: " + tile.getPower() + " also svelte > r**ct";

        IReorderingProcessor text = IReorderingProcessor.styledString(str, Style.EMPTY);
        float f3 = (float)(-fontrenderer.getWidth(text) / 2);
        fontrenderer.draw(text, f3, (float)(-20), color, false, p_225616_3_.peek().getModel(), p_225616_4_, false, 0, p_225616_5_);

        p_225616_3_.pop();
    }
}
