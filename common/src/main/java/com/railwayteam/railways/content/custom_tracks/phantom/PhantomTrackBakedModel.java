package com.railwayteam.railways.content.custom_tracks.phantom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

public class PhantomTrackBakedModel extends ConditionallyInvisibleBakedModel { //this only reloads on chunk rebuild, bad
    public PhantomTrackBakedModel(BakedModel originalModel) {
        super(originalModel, () -> {
            Minecraft mc = Minecraft.getInstance();
            return mc.player != null && mc.player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.IRON_HELMET;
        });
    }
}
