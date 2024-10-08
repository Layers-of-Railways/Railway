/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.custom_tracks.phantom;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleSpriteContents;
import com.railwayteam.railways.registry.CRTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PhantomSpriteManager {
    private static final Map<ResourceLocation, WeakReference<SpriteContents>> map = new ConcurrentHashMap<>();
    private static boolean lastVisible = false;
    public static boolean firstRun = true;
    public static boolean hasChanged = false;
    private static boolean visible = false;

    public static boolean isVisible() {
        return visible;
    }

    public static boolean register(SpriteContents sprite) {
        if (sprite.name().getNamespace().equals(Railways.MOD_ID) && sprite.name().getPath().startsWith("block/track/phantom/")) {
            map.put(sprite.name(), new WeakReference<>(sprite));
            firstRun = true;
            return true;
        }
        return false;
    }

    @MultiLoaderEvent
    public static void tick(Minecraft mc) {
        visible = mc.player != null
            && (CRTags.AllItemTags.PHANTOM_TRACK_REVEALING.matches(mc.player.getItemBySlot(EquipmentSlot.MAINHAND).getItem())
                || CRTags.AllItemTags.PHANTOM_TRACK_REVEALING.matches(mc.player.getItemBySlot(EquipmentSlot.OFFHAND).getItem()));
        if (visible != lastVisible || firstRun) {
            firstRun = false;
            lastVisible = visible;
            hasChanged = true;
            List<ResourceLocation> expired = new ArrayList<>();
            for (Map.Entry<ResourceLocation, WeakReference<SpriteContents>> entry : map.entrySet()) {
                if (entry.getValue().get() == null)
                    expired.add(entry.getKey());
            }
            for (ResourceLocation key : expired) {
                map.remove(key);
            }
        }
    }

    public static void renderTick() {
        if (hasChanged) {
            hasChanged = false;
            for (WeakReference<SpriteContents> ref : map.values()) {
                SpriteContents sprite = ref.get();
                if (sprite != null) {
                    ((IPotentiallyInvisibleSpriteContents) sprite).railways$uploadFrame(lastVisible);
                }
            }
        }
    }
}
