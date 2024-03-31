package com.railwayteam.railways.fabric.mixin.self;

import com.google.gson.Gson;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Self mixin, I know funny right, by the power of casting I cast you to work.
 */
@Mixin(LiquidFuelManager.ReloadListener.class)
public abstract class LiquidFuelManagerReloadListenerMixin extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    public LiquidFuelManagerReloadListenerMixin(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    public ResourceLocation getFabricId() {
        return Railways.asResource(LiquidFuelManager.ReloadListener.ID);
    }
}
