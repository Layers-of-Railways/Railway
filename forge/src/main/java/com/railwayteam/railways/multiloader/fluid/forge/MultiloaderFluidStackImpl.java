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

package com.railwayteam.railways.multiloader.fluid.forge;

import com.mojang.serialization.Codec;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.multiloader.fluid.MultiloaderFluidStack;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ImplClass
public class MultiloaderFluidStackImpl extends MultiloaderFluidStack {

    public static Codec<MultiloaderFluidStack> makeCodec() {
        return FluidStack.CODEC.xmap(MultiloaderFluidStackImpl::new, fs -> ((MultiloaderFluidStackImpl) fs).wrapped);
    }

    public static MultiloaderFluidStack makeEmpty() {
        return new MultiloaderFluidStackImpl(FluidStack.EMPTY);
    }

    private final FluidStack wrapped;

    public MultiloaderFluidStackImpl(FluidStack wrapped) {
        this.wrapped = wrapped;
    }

    public MultiloaderFluidStackImpl(Fluid fluid, int amount) {
        this(new FluidStack(fluid, amount));
    }

    public MultiloaderFluidStackImpl(Fluid fluid, int amount, CompoundTag nbt) {
        this(new FluidStack(fluid, amount, nbt));
    }

    public MultiloaderFluidStackImpl(FluidStack stack, int amount) {
        this(new FluidStack(stack, amount));
    }

    public MultiloaderFluidStackImpl(MultiloaderFluidStack stack, int amount) {
        this(((MultiloaderFluidStackImpl) stack).wrapped, amount);
    }

    @Override
    public MultiloaderFluidStack setAmount(long amount) {
        wrapped.setAmount((int) amount);
        return this;
    }

    @Override
    public Fluid getFluid() {
        return wrapped.getFluid();
    }

    public final Fluid getRawFluid() {
        return wrapped.getRawFluid();
    }

    @Override
    public long getAmount() {
        return wrapped.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean isFluidEqual(MultiloaderFluidStack other) {
        return wrapped.isFluidEqual(((MultiloaderFluidStackImpl) other).wrapped);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag nbt) {
        return wrapped.writeToNBT(nbt);
    }

    public static MultiloaderFluidStack loadFluidStackFromNBT(CompoundTag tag) {
        return new MultiloaderFluidStackImpl(FluidStack.loadFluidStackFromNBT(tag));
    }

    @Override
    public void setTag(CompoundTag tag) {
        wrapped.setTag(tag);
    }

    @Override
    public @Nullable CompoundTag getTag() {
        return wrapped.getTag();
    }

    public @Nullable CompoundTag getChildTag(String childName) {
        return wrapped.getChildTag(childName);
    }

    public @Nullable CompoundTag getOrCreateChildTag(String childName) {
        return wrapped.getOrCreateChildTag(childName);
    }

    @Override
    public Component getDisplayName() {
        return wrapped.getDisplayName();
    }

    public String getTranslationKey() {
        return wrapped.getTranslationKey();
    }

    public static MultiloaderFluidStack readFromPacket(FriendlyByteBuf buffer) {
        return new MultiloaderFluidStackImpl(FluidStack.readFromPacket(buffer));
    }

    @Override
    public FriendlyByteBuf writeToPacket(FriendlyByteBuf buffer) {
        wrapped.writeToPacket(buffer);
        return buffer;
    }

    @Override
    public MultiloaderFluidStack copy() {
        return new MultiloaderFluidStackImpl(wrapped.copy());
    }

    @Override
    public boolean containsFluid(@NotNull MultiloaderFluidStack other) {
        return wrapped.containsFluid(((MultiloaderFluidStackImpl) other).wrapped);
    }

    @Override
    public boolean isFluidStackIdentical(MultiloaderFluidStack other) {
        return wrapped.isFluidStackIdentical(((MultiloaderFluidStackImpl) other).wrapped);
    }

    @Override
    public boolean isFluidEqual(@NotNull ItemStack other) {
        return wrapped.isFluidEqual(other);
    }

    @Override
    public boolean isLighterThanAir() {
        return getFluid().getFluidType().isLighterThanAir();
    }

    @Override
    public FluidIngredient asFluidIngredient() {
        return FluidIngredient.fromFluidStack(getWrapped());
    }

    @Override
    public final int hashCode() {
        return wrapped.hashCode();
    }

    /**
     * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof MultiloaderFluidStack fs))
            return false;

        return isFluidEqual(fs);
    }

    public static MultiloaderFluidStack create(Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        return new MultiloaderFluidStackImpl(fluid, (int) amount, nbt);
    }

    public FluidStack getWrapped() {
        return wrapped;
    }
}
