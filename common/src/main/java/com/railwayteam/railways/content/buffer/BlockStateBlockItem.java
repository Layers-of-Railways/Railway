package com.railwayteam.railways.content.buffer;

import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStateBlockItem<T extends Comparable<T>> extends BlockItem {
    protected final Property<T> property;
    protected final T value;

    protected BlockStateBlockItem(Block block, Properties properties, Property<T> property, T value) {
        super(block, properties);
        this.property = property;
        this.value = value;
    }

    public static <T extends Comparable<T>> NonNullFunction<Properties, BlockStateBlockItem<T>> create(NonNullSupplier<Block> blockSupplier, Property<T> property, T value) {
        return (p) -> new BlockStateBlockItem<>(blockSupplier.get(), p, property, value);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    /*@Override
    public void fillItemCategory(@NotNull CreativeModeTab category, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(category)) {
            items.add(new ItemStack(this));
        }
    }*/

    @Nullable
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        if (blockState != null) {
            blockState = blockState.setValue(this.property, this.value);
        }
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }
}
