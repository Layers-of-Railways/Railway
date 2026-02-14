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

package com.railwayteam.railways.content.palettes.painting.fabric;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.content.palettes.painting.PitcherColor;
import com.railwayteam.railways.registry.CRFluids;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import static com.railwayteam.railways.content.palettes.painting.PaintPitcherItem.FLUID_PER_LEVEL;
import static com.railwayteam.railways.content.palettes.painting.PaintPitcherItem.MAX_LEVELS;

@SuppressWarnings("UnstableApiUsage")
class PaintPitcherFluidStorage implements SingleSlotStorage<FluidVariant> {
    private final ContainerItemContext context;

    public PaintPitcherFluidStorage(ContainerItemContext context) {
        this.context = context;
    }

    private @Nullable PitcherColor getColor() {
        if (context.getItemVariant().getItem() instanceof PaintPitcherItem item) {
            return new PitcherColor(item.getColor());
        }
        return null;
    }

    private int getLevels() {
        ItemStack stack = context.getItemVariant().toStack();
        if (!(stack.getItem() instanceof PaintPitcherItem item)) return 0;
        return item.getLevels(stack);
    }

    private static @Nullable PitcherColor getVariantColor(FluidVariant resource) {
        if (Fluids.WATER.isSame(resource.getFluid()))
            return PitcherColor.SANDY_WATER;

        if (!CRFluids.PAINT.get().isSame(resource.getFluid()))
            return null;

        PalettesColor fluidColor = PaintFluid.getColor(resource.getNbt()).orElse(null);
        if (fluidColor == null)
            return null;

        return new PitcherColor(fluidColor);
    }

    @Nullable
    private PitcherColor getColorIfValid(FluidVariant resource) {
        PitcherColor color = getColor();
        PitcherColor fluidColor = getVariantColor(resource);
        if (fluidColor == null) return null;

        // Color mismatches can never be inserted or extracted
        if (color != null && !color.equals(fluidColor)) {
            return null;
        }

        return color == null ? fluidColor : color;
    }

    private ItemVariant makeFilledVariant(PitcherColor color, int levels) {
        return ItemVariant.of(color.getItemEntry().get()
            .copyAsFilledStack(context.getItemVariant().toStack(), levels));
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        PitcherColor color = getColorIfValid(resource);
        if (color == null) return 0;

        int currentLevels = getLevels();
        int levelCapacity = MAX_LEVELS - currentLevels;
        if (levelCapacity <= 0) return 0;
        int filledLevels = (int) Math.min(maxAmount / FLUID_PER_LEVEL, levelCapacity);
        if (filledLevels <= 0) return 0;

        ItemVariant newVariant = makeFilledVariant(color, currentLevels + filledLevels);

        if (context.exchange(newVariant, 1, transaction) == 1) {
            return filledLevels * FLUID_PER_LEVEL;
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        PitcherColor color = getColorIfValid(resource);
        if (color == null) return 0;

        int currentLevels = getLevels();
        int drainedLevels = (int) Math.min(maxAmount / FLUID_PER_LEVEL, currentLevels);
        if (drainedLevels <= 0) return 0;

        ItemVariant newVariant = makeFilledVariant(color, currentLevels - drainedLevels);

        if (context.exchange(newVariant, 1, transaction) == 1) {
            return drainedLevels * FLUID_PER_LEVEL;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getLevels() == 0;
    }

    @Override
    public FluidVariant getResource() {
        ItemStack stack = context.getItemVariant().toStack();
        if (!(stack.getItem() instanceof PaintPitcherItem item)) return FluidVariant.blank();
        if (item.getLevels(stack) == 0) return FluidVariant.blank();

        PalettesColor color = item.getColor();
        return color == null ? FluidVariant.of(Fluids.WATER) : FluidVariant.of(
            CRFluids.PAINT.get().getSource(),
            PaintFluid.setColor(new CompoundTag(), item.getColor())
        );
    }

    @Override
    public long getAmount() {
        return getLevels() * FLUID_PER_LEVEL;
    }

    @Override
    public long getCapacity() {
        return MAX_LEVELS * FLUID_PER_LEVEL;
    }

    @Override
    public String toString() {
        return "PaintPitcherFluidStorage[" + context + "]";
    }
}
