/*
 * Steam 'n' Rails
 * Copyright (c) 2024-2025 The Railways Team
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

package com.railwayteam.railways.multiloader.fluid;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MultiloaderFluidStack {

    @ExpectPlatform
    private static Codec<MultiloaderFluidStack> makeCodec() {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static MultiloaderFluidStack makeEmpty() {
        throw new AssertionError();
    }

    public static final Codec<MultiloaderFluidStack> CODEC = makeCodec();

    public static final MultiloaderFluidStack EMPTY = makeEmpty();

    public static MultiloaderFluidStack create(Fluid fluid, long amount) {
        return create(fluid, amount, null);
    }

    @ExpectPlatform
    public static MultiloaderFluidStack create(Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        throw new AssertionError();
    }

    public abstract MultiloaderFluidStack setAmount(long amount);

    public void grow(long amount) {
        setAmount(getAmount() + amount);
    }

    public abstract Fluid getFluid();

    public abstract long getAmount();

    public abstract boolean isEmpty();

    public void shrink(int amount) {
        setAmount(getAmount() - amount);
    }

    public void shrink(long amount) {
        setAmount(getAmount() - amount);
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
     *
     * @param other
     *            The FluidStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public abstract boolean isFluidEqual(MultiloaderFluidStack other);

    public abstract CompoundTag writeToNBT(CompoundTag nbt);

    @ExpectPlatform
    public static MultiloaderFluidStack loadFluidStackFromNBT(CompoundTag tag) {
        throw new AssertionError();
    }

    public abstract void setTag(CompoundTag tag);

    @Nullable
    public abstract CompoundTag getTag();

    public CompoundTag getOrCreateTag() {
        if (getTag() == null) setTag(new CompoundTag());
        return getTag();
    }

    public void removeChildTag(String key) {
        if (getTag() == null) return;
        getTag().remove(key);
    }

    public abstract Component getDisplayName();

    public boolean hasTag() {
        return getTag() != null;
    }

    @ExpectPlatform
    public static MultiloaderFluidStack readFromPacket(FriendlyByteBuf buffer) {
        throw new AssertionError();
    }

    public abstract FriendlyByteBuf writeToPacket(FriendlyByteBuf buffer);

    public abstract MultiloaderFluidStack copy();

    private boolean isFluidStackTagEqual(MultiloaderFluidStack other) {
        CompoundTag tag = getTag();
        CompoundTag other$tag = other.getTag();
        return tag == null ? other$tag == null : other$tag != null && tag.equals(other$tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(@NotNull MultiloaderFluidStack stack1, @NotNull MultiloaderFluidStack stack2) {
        return stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public abstract boolean containsFluid(@NotNull MultiloaderFluidStack other);

    /**
     * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
     *
     * @param other
     *            - the FluidStack for comparison
     * @return true if the two FluidStacks are exactly the same
     */
    public abstract boolean isFluidStackIdentical(MultiloaderFluidStack other);

    /**
     * Determines if the FluidIDs and NBT Tags are equal compared to a registered container
     * ItemStack. This does not check amounts.
     *
     * @param other
     *            The ItemStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public abstract boolean isFluidEqual(@NotNull ItemStack other);

    public abstract boolean isLighterThanAir();

    public abstract FluidIngredient asFluidIngredient();
}
