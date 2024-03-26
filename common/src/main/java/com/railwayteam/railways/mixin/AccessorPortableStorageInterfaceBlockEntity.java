package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PortableStorageInterfaceBlockEntity.class)
public interface AccessorPortableStorageInterfaceBlockEntity {
    @Invoker("isConnected")
    boolean railways$isConnected();
}
