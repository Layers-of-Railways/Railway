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

package com.railwayteam.railways.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.Set;
import java.util.function.Function;

public class DebugRendererExtensions {
    @OnlyIn(Dist.CLIENT)
    public enum Renderers {
        PATHFINDING(d -> d.pathfindingRenderer),
        WATER(d -> d.waterDebugRenderer),
        HEIGHT_MAP(d -> d.heightMapRenderer),
        COLLISION_BOX(d -> d.collisionBoxRenderer),
        NEIGHBORS_UPDATE(d -> d.neighborsUpdateRenderer),
        STRUCTURE(d -> d.structureRenderer),
        LIGHT(d -> d.lightDebugRenderer),
        WORLD_GEN_ATTEMPT(d -> d.worldGenAttemptRenderer),
        SOLID_FACE(d -> d.solidFaceRenderer),
        CHUNK(d -> d.chunkRenderer),
        BRAIN(d -> d.brainDebugRenderer),
        VILLAGE_SECTIONS(d -> d.villageSectionsDebugRenderer),
        BEE(d -> d.beeDebugRenderer),
        RAID(d -> d.raidDebugRenderer),
        GOAL_SELECTOR(g -> g.goalSelectorRenderer),
        GAME_TEST(g -> g.gameTestDebugRenderer),
        GAME_EVENT(g -> g.gameEventListenerRenderer)
        ;

        private final Function<DebugRenderer, DebugRenderer.SimpleDebugRenderer> rendererSupplier;

        Renderers(Function<DebugRenderer, DebugRenderer.SimpleDebugRenderer> rendererSupplier) {
            this.rendererSupplier = rendererSupplier;
        }

        public DebugRenderer.SimpleDebugRenderer getRenderer() {
            return rendererSupplier.apply(Minecraft.getInstance().debugRenderer);
        }
    }

    private static final Set<Renderers> EMPTY = ImmutableSet.of();

    @OnlyIn(Dist.CLIENT)
    public static Set<Renderers> getEnabledRenderers() {
        if (!Utils.isDevEnv())
            return EMPTY;

        return ImmutableSet.of(
            //Renderers.COLLISION_BOX
            //Renderers.SOLID_FACE
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        for (Renderers renderer : getEnabledRenderers())
            renderer.getRenderer().render(poseStack, bufferSource, camX, camY, camZ);
    }
}
