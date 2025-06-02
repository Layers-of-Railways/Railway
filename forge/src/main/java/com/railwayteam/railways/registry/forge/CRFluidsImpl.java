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
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

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

    private static class PaintFluidType extends AllFluids.TintedFluidType {
        public PaintFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return PaintFluid.getColor(stack.getTag())
                .map(PalettesColor::getDiffuseColor)
                .orElse(0) | 0xff000000;
        }

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return NO_TINT;
        }

        @Override
        public String getDescriptionId(FluidStack stack) {
            return PaintFluid.getColor(stack.getTag())
                .map(PalettesColor::getPaintNameId)
                .orElse("fluid.railways.paint");
        }
    }
}
