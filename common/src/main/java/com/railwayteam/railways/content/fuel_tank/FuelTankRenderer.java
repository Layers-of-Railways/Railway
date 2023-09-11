package com.railwayteam.railways.content.fuel_tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

public class FuelTankRenderer extends SafeBlockEntityRenderer<FuelTankBlockEntity> {
    public FuelTankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(FuelTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.isController())
            return;
        if (!be.window) {
            return;
        }

        LerpedFloat fuelLevel = be.getFuelLevel();
        if (fuelLevel == null)
            return;

        float capHeight = 1 / 4f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        float level = fuelLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        FluidTank tank = be.tankInventory;
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        boolean top = FluidVariantAttributes.isLighterThanAir(fluidStack.getType());

        float xMin = tankHullWidth;
        float xMax = xMin + be.width - 2 * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;

        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }

        float zMin = tankHullWidth;
        float zMax = zMin + be.width - 2 * tankHullWidth;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        FluidRenderer.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, buffer, ms, light, false);
        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FuelTankBlockEntity be) {
        return be.isController();
    }
}
