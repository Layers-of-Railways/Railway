package com.railwayteam.railways.content.tiles.ters;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.railwayteam.railways.content.tiles.tiles.SpeedSignalTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Style;

public class SpeedSignalTileRenderer extends TileEntityRenderer<SpeedSignalTileEntity> {
    public SpeedSignalTileRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    public static int getDegreesFromDirection(Direction direction) {
        int d;
        switch (direction) {
            case EAST:
                d = 3;
                break;
            case SOUTH:
                d = 0;
                break;
            case WEST:
                d = 1;
                break;
            default:
                d = 2;
                break;
        }
        return d * 90;
    }

    @Override
    public void render(SpeedSignalTileEntity tile, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        BlockState blockstate = tile.getBlockState();

        matrixStack.push();
        float f = 0.6666667F;
//        p_225616_3_.translate(0.0D, -0.3125D, -0.4375D);
        Direction dir = blockstate.get(HorizontalBlock.HORIZONTAL_FACING);
        //why
        //matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-((float)(getDegreesFromDirection(dir)))));

//        switch (dir) {
//            case EAST:
//                matrixStack.translate();
//        }
        // i have no idea what im going lmao
        switch (dir) {
            case NORTH:
                matrixStack.translate(-.5D, 0, -.1D);
                break;
            case EAST:
                matrixStack.translate(-.5D, 0, .9D);
                break;
            case SOUTH:
                matrixStack.translate(.5D, 0, .9D);
                break;
            case WEST:
                matrixStack.translate(.5D, 0, -.1D);
        }
        matrixStack.push();
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
//        RenderMaterial rendermaterial = getModelTexture(blockstate.getBlock());
//        IVertexBuilder ivertexbuilder = rendermaterial.getVertexConsumer(p_225616_4_, this.model::getLayer);
//        this.model.field_78166_a.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
//        this.model.field_78165_b.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
        matrixStack.pop();
        FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
        float f2 = 0.010416667F;
//        p_225616_3_.translate(0.0D, (double)0.33333334F, (double)0.046666667F);
        matrixStack.scale(0.03F, -0.03F, 0.03F);

        int color = NativeImage.getCombined(0, 255, 255, 255);

        String str = String.valueOf(tile.getPower());
//        String str = "power: " + tile.getPower() + " also svelte > r**ct";

        IReorderingProcessor text = IReorderingProcessor.fromString(str, Style.EMPTY);
        float f3 = (float)(-fontrenderer.getStringWidth(String.valueOf(text)) / 2);
        //fontrenderer.drawString(text, f3, (float)(-20), color, false, matrixStack.peek().getModel(), p_225616_4_, false, 0, p_225616_5_);

        matrixStack.pop();
    }
}
