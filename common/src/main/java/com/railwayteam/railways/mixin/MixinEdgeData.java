package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.EdgeData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EdgeData.class, remap = false)
public class MixinEdgeData implements ISwitchDisabledEdge {
    private boolean enabled = true;

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
