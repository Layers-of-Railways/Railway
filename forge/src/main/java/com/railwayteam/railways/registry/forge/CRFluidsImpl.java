/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@ImplClass
public class CRFluidsImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static FluidEntry<VirtualFluid> registerPaint() {
        return REGISTRATE.virtualFluid("paint", PaintFluidType::new, VirtualFluid::new)
            .lang("Paint")
            .register();
    }

    @OnlyIn(Dist.CLIENT)
    public static void initRendering() {}

    private static class PaintFluidType extends FluidType {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;

        public PaintFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private ResourceLocation $getStillTexture(@Nullable PalettesColor color) {
                    if (color == null) color = PalettesColor.NETHERITE;
                    return stillTexture.withSuffix("/" + color.getSerializedName());
                }

                private ResourceLocation $getFlowingTexture(@Nullable PalettesColor color) {
                    if (color == null) color = PalettesColor.NETHERITE;
                    return flowingTexture.withSuffix("/" + color.getSerializedName());
                }

                @Override
                public ResourceLocation getStillTexture() {
                    return $getStillTexture(null);
                }

                @Override
                public ResourceLocation getStillTexture(FluidStack stack) {
                    return $getStillTexture(PaintFluid.getColor(stack.getTag()).orElse(null));
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return $getFlowingTexture(null);
                }

                @Override
                public ResourceLocation getFlowingTexture(FluidStack stack) {
                    return $getFlowingTexture(PaintFluid.getColor(stack.getTag()).orElse(null));
                }
            });
        }

        @Override
        public String getDescriptionId(FluidStack stack) {
            return PaintFluid.getColor(stack.getTag())
                .map(PalettesColor::getPaintNameId)
                .orElse("fluid.railways.paint");
        }
    }
}
