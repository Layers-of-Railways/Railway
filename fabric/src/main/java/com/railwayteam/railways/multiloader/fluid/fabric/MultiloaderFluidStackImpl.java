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

package com.railwayteam.railways.multiloader.fluid.fabric;

import com.mojang.serialization.Codec;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.multiloader.fluid.MultiloaderFluidStack;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
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

    public MultiloaderFluidStackImpl(FluidVariant type, long amount) {
        this(new FluidStack(type, amount));
    }

    public MultiloaderFluidStackImpl(FluidVariant type, long amount, @Nullable CompoundTag tag) {
        this(new FluidStack(type, amount, tag));
    }

    public MultiloaderFluidStackImpl(StorageView<FluidVariant> view) {
        this(new FluidStack(view));
    }

    public MultiloaderFluidStackImpl(ResourceAmount<FluidVariant> resource) {
        this(new FluidStack(resource));
    }

    /**
     * Avoid this constructor when possible, may result in NBT loss
     */
    public MultiloaderFluidStackImpl(Fluid fluid, long amount) {
        this(new FluidStack(fluid, amount));
    }

    public MultiloaderFluidStackImpl(Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        this(new FluidStack(fluid, amount, nbt));
    }

    public MultiloaderFluidStackImpl(FluidStack copy, long amount) {
        this(new FluidStack(copy, amount));
    }

    public MultiloaderFluidStackImpl(MultiloaderFluidStack copy, long amount) {
        this(((MultiloaderFluidStackImpl) copy).wrapped, amount);
    }

    @Override
    public MultiloaderFluidStack setAmount(long amount) {
        wrapped.setAmount(amount);
        return this;
    }

    public FluidVariant getType() {
        return wrapped.getType();
    }

    @Override
    public Fluid getFluid() {
        return wrapped.getFluid();
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

    public boolean isFluidEqual(FluidVariant other) {
        return wrapped.isFluidEqual(other);
    }

    public static boolean isFluidEqual(FluidVariant mine, FluidVariant other) {
        return FluidStack.isFluidEqual(mine, other);
    }

    public boolean canFill(FluidVariant var) {
        return wrapped.canFill(var);
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

    @Override
    public Component getDisplayName() {
        return wrapped.getDisplayName();
    }

    public static MultiloaderFluidStack readFromPacket(FriendlyByteBuf buffer) {
        return new MultiloaderFluidStackImpl(FluidStack.readFromPacket(buffer));
    }

    @Override
    public FriendlyByteBuf writeToPacket(FriendlyByteBuf buffer) {
        return wrapped.writeToPacket(buffer);
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
        return FluidVariantAttributes.isLighterThanAir(wrapped.getType());
    }

    @Override
    public FluidIngredient asFluidIngredient() {
        return FluidIngredient.fromFluidStack(getWrapped());
    }

    public FluidStack getWrapped() {
        return wrapped;
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
        return new MultiloaderFluidStackImpl(fluid, amount, nbt);
    }
}
