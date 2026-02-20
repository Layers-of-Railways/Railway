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

package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.base.reload.ClientResourceReloadCallback;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.railwayteam.railways.registry.CRFluids;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

@ImplClass
@SuppressWarnings("UnstableApiUsage")
public class CRFluidsImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static FluidEntry<VirtualFluid> registerPaint() {
        return REGISTRATE.virtualFluid("paint", VirtualFluid::new)
            .lang("Paint")
            .fluidAttributes(PaintFluidVariantAttributeHandler::new)
            .register();
    }

    @Environment(EnvType.CLIENT)
    public static void initRendering() {
        PaintFluidVariantRenderHandler handler = new PaintFluidVariantRenderHandler();
        VirtualFluid paintFluid = CRFluids.PAINT.get();
        FluidVariantRendering.register(paintFluid.getFlowing(), handler);
        FluidVariantRendering.register(paintFluid.getSource(), handler);
        RailwaysClient.registerReloadCallback(handler);
    }

    public static class PaintFluidVariantAttributeHandler implements FluidVariantAttributeHandler {
        @Override
        public Component getName(FluidVariant fluidVariant) {
            return Components.translatable(PaintFluid.getColor(fluidVariant.getNbt())
                .map(PalettesColor::getPaintNameId)
                .orElse("fluid.railways.paint"));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class PaintFluidVariantRenderHandler implements FluidVariantRenderHandler, ClientResourceReloadCallback {
        private final TextureAtlasSprite[][] sprites = new TextureAtlasSprite[PalettesColor.values().length][2];

        @Override
        public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
            return -1;
        }

        @Override
        public @Nullable TextureAtlasSprite[] getSprites(FluidVariant fluidVariant) {
            int index = PaintFluid.getColor(fluidVariant.getNbt())
                .orElse(PalettesColor.NETHERITE)
                .ordinal();
            return sprites[index];
        }

        @Override
        public void onResourceManagerReload() {
            TextureAtlas texture = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(InventoryMenu.BLOCK_ATLAS);
            for (PalettesColor color : PalettesColor.values()) {
                TextureAtlasSprite[] sp = sprites[color.ordinal()];

                sp[0] = texture.getSprite(Railways.asResource("fluid/paint_still/" + color.getSerializedName()));
                sp[1] = texture.getSprite(Railways.asResource("fluid/paint_flow/" + color.getSerializedName()));
            }
        }
    }
}
