package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.entities.SteadyMinecartEntity;
import com.railwayteam.railways.content.entities.conductor.ConductorEntity;
import com.railwayteam.railways.content.entities.conductor.ConductorRenderer;
import com.railwayteam.railways.content.entities.handcar.HandcarEntity;
import com.railwayteam.railways.content.entities.handcar.HandcarRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.OneTimeEventReceiver;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

public class CREntities {
    public static EntityEntry<Entity> R_ENTITY_STEADYCART;
    public static EntityEntry<ConductorEntity> R_ENTITY_CONDUCTOR;
    public static EntityEntry<HandcarEntity> R_ENTITY_HANDCAR;

    public static void register(Registrate reg) {
        R_ENTITY_STEADYCART = reg.entity(SteadyMinecartEntity.name, SteadyMinecartEntity::new, EntityClassification.MISC)
                .lang("Steady Minecart")
                .register();

        R_ENTITY_CONDUCTOR = reg.entity(ConductorEntity.name, ConductorEntity::new, EntityClassification.MISC)
                .lang(ConductorEntity.defaultDisplayName).properties(p -> p.size(0.5F, 1.3F))
                .renderer(() -> ConductorRenderer::new)
                .register();

        R_ENTITY_HANDCAR = reg.entity(HandcarEntity.name, HandcarEntity::new, EntityClassification.MISC)
                .lang("Handcar")
                .renderer(() -> HandcarRenderer::new)
                .properties(p -> p.size(2, 1.7F))
                .register();

        OneTimeEventReceiver.addListener(Railways.MOD_EVENT_BUS, EntityAttributeCreationEvent.class, (e) -> {
            e.put(R_ENTITY_CONDUCTOR.get(), LivingEntity.registerAttributes().create());
            //this is the closest I could get it to working, any help would be appreciated
            /*OneTimeEventReceiver.addListener(Railways.MOD_EVENT_BUS, EntityAttributeCreationEvent.class, (e) -> {
                e.put(R_ENTITY_CONDUCTOR.get(), LivingEntity.registerAttributes().hasAttribute(Attributes.FOLLOW_RANGE, 16)).build(); //this is the closest I could get it to working, any help would be appreciated
            });*/
        });
    }
}
