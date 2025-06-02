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
import com.railwayteam.railways.registry.CRFluids;
import com.railwayteam.railways.registry.CRItems;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.railwayteam.railways.content.palettes.painting.PaintPitcherItem.FLUID_PER_LEVEL;
import static com.railwayteam.railways.content.palettes.painting.PaintPitcherItem.MAX_LEVELS;

@SuppressWarnings("UnstableApiUsage")
class PaintPitcherFluidStorage implements SingleSlotStorage<FluidVariant> {
    private final ContainerItemContext context;

    public PaintPitcherFluidStorage(ContainerItemContext context) {
        this.context = context;
    }

    private @Nullable PalettesColor getColor() {
        if (context.getItemVariant().getItem() instanceof PaintPitcherItem item) {
            return item.getColor();
        }
        return null;
    }

    private int getLevels() {
        ItemStack stack = context.getItemVariant().toStack();
        if (!(stack.getItem() instanceof PaintPitcherItem item)) return 0;
        return item.getLevels(stack);
    }

    @Nullable
    private PalettesColor getColorIfValid(FluidVariant resource) {
        if (!CRFluids.PAINT.get().isSame(resource.getFluid())) return null;
        PalettesColor color = getColor();
        PalettesColor fluidColor = PaintFluid.getColor(resource.getNbt()).orElse(null);

        // Color mismatches can never be inserted or extracted
        if (color != null && fluidColor != null && color != fluidColor) {
            return null;
        }

        return color == null ? fluidColor : color;
    }

    private ItemVariant makeFilledVariant(PalettesColor color, int levels) {
        return ItemVariant.of(CRItems.PAINT_PITCHERS.get(color).get()
            .copyAsFilledStack(context.getItemVariant().toStack(), levels));
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        PalettesColor color = getColorIfValid(resource);
        if (color == null) return 0;

        int currentLevels = getLevels();
        int levelCapacity = MAX_LEVELS - currentLevels;
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

        PalettesColor color = getColorIfValid(resource);
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

        return FluidVariant.of(
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
