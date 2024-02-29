package com.railwayteam.railways;

import com.railwayteam.railways.api.bogeymenu.BogeyMenuManager;
import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.impl.bogeymenu.internal.CategoryEntry;
import com.railwayteam.railways.registry.*;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.chat.Component;

public class ModSetup {
  public static void register() {
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRItems.register();
    CRSpriteShifts.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRPalettes.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
    CRDataFixers.register();
    CRExtraRegistration.register();
    CasingCollisionUtils.register();
    CRInteractionBehaviours.register();
    CRPortalTracks.register();

    // Compat
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
    DreamsAndDesiresTrackCompat.register();
    QuarkTrackCompat.register();

    //fixme
    //BogeyMenuManager.INSTANCE.registerCategory(Component.literal("Standard"), Railways.asResource("standard"), null);
    CategoryEntry e = BogeyMenuManager.INSTANCE.registerCategory(Component.literal("Steam 'n' Rails"), Railways.asResource("railways"), null);

    //BogeyMenuManager.INSTANCE.addToCategory(e, CRBogeyStyles.ARCHBAR, Railways.asResource("textures/gui/bogey_icons/archbar_icon.png"));
    //BogeyMenuManager.INSTANCE.addToCategory(e, CRBogeyStyles.LEAFSPRING, Railways.asResource("textures/gui/bogey_icons/leafspring_icon.png"));

    for (BogeyStyle style : AllBogeyStyles.BOGEY_STYLES.values()) {
      if (CRBogeyStyles.hideInSelectionMenu(style))
        continue;
      if (style.name.getPath().equals("invisible_monobogey") ||
              style.name.getPath().equals("handcar") ||
              style.name.getPath().equals("monobogey"))
        continue;
      if (style.name.getNamespace().equals(Railways.MODID))
        BogeyMenuManager.INSTANCE.addToCategory(e, style, Railways.asResource("textures/gui/bogey_icons/" + style.name.getPath() + "_icon.png"));
    }

    BogeyMenuManager.INSTANCE.addToCategory(e, AllBogeyStyles.STANDARD, Railways.asResource("textures/gui/bogey_icons/default_icon.png"));
  }
}
