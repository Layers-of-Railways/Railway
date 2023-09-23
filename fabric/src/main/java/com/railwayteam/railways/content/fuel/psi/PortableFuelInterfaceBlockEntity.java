package com.railwayteam.railways.content.fuel.psi;

import com.railwayteam.railways.mixin.AccessorCarriageContraption;
import com.railwayteam.railways.mixin_interfaces.IContraptionFuel;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.fabric.ListeningStorageView;
import com.simibubi.create.foundation.utility.fabric.ProcessingIterator;
import io.github.fabricators_of_create.porting_lib.transfer.WrappedStorage;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class PortableFuelInterfaceBlockEntity extends PortableStorageInterfaceBlockEntity implements SidedStorageBlockEntity {

    protected InterfaceFluidHandler capability;

    public PortableFuelInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        capability = createEmptyHandler();
    }

    @Override
    public void startTransferringTo(Contraption contraption, float distance) {
        CombinedTankWrapper ctw = ((IContraptionFuel) contraption).snr$getSharedFuelTanks();
        if (contraption instanceof CarriageContraption carriageContraption) {
            MountedStorageManager storageProxy = ((AccessorCarriageContraption) carriageContraption).getStorageProxy();
            ctw = ((IFuelInventory) storageProxy).snr$getFuelFluids();
        }
        capability.setWrapped(ctw);
        super.startTransferringTo(contraption, distance);
    }

    @Override
    protected void invalidateCapability() {
        capability.setWrapped(Storage.empty());
    }

    @Override
    protected void stopTransferring() {
        capability.setWrapped(Storage.empty());
        super.stopTransferring();
    }

    private InterfaceFluidHandler createEmptyHandler() {
        return new InterfaceFluidHandler(Storage.empty());
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction face) {
        return capability;
    }

    boolean isConnected() {
        int timeUnit = getTransferTimeout();
        return transferTimer >= ANIMATION && transferTimer <= timeUnit + ANIMATION;
    }

    public class InterfaceFluidHandler extends WrappedStorage<FluidVariant> {

        public InterfaceFluidHandler(Storage<FluidVariant> wrapped) {
            super(wrapped);
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!isConnected())
                return 0;
            long fill = wrapped.insert(resource, maxAmount, transaction);
            if (fill > 0)
                TransactionCallback.onSuccess(transaction, this::keepAlive);
            return fill;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!canTransfer())
                return 0;
            long drain = wrapped.extract(resource, maxAmount, transaction);
            if (drain != 0)
                TransactionCallback.onSuccess(transaction, this::keepAlive);
            return drain;
        }

        @Override
        public @Nullable StorageView<FluidVariant> exactView(FluidVariant resource) {
            return listen(super.exactView(resource));
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return new ProcessingIterator<>(super.iterator(), this::listen);
        }

        public <T> StorageView<T> listen(StorageView<T> view) {
            return new ListeningStorageView<>(view, this::keepAlive);
        }

        public void keepAlive() {
            onContentTransferred();
        }

        private void setWrapped(Storage<FluidVariant> wrapped) {
            this.wrapped = wrapped;
        }
    }
}