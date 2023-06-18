package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.EdgeData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EdgeData.class, remap = false)
public class MixinEdgeData implements ISwitchDisabledEdge {
    private boolean enabled = true;
    private boolean automatic = false;
    private int selectPriority = -1;

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    @Override
    public boolean isAutomatic() {
        return automatic;
    }

    @Override
    public void setAutomaticallySelected() {
        selectPriority = TrackSwitch.getSelectionPriority();
        enabled = true;
    }

    @Override
    public int getAutomaticallySelectedPriority() {
        return selectPriority;
    }

    @Override
    public boolean isAutomaticallySelected() {
        return selectPriority != -1;
    }

    @Override
    public void ackAutomaticSelection() {
        selectPriority = -1;
    }
}
