package com.railwayteam.railways.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.Set;
import java.util.function.Function;

public class DebugRendererExtensions {
    @Environment(EnvType.CLIENT)
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

    @Environment(EnvType.CLIENT)
    public static Set<Renderers> getEnabledRenderers() {
        if (!Utils.isDevEnv())
            return EMPTY;

        return ImmutableSet.of(
            //Renderers.COLLISION_BOX
            //Renderers.SOLID_FACE
        );
    }

    @Environment(EnvType.CLIENT)
    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        for (Renderers renderer : getEnabledRenderers())
            renderer.getRenderer().render(poseStack, bufferSource, camX, camY, camZ);
    }
}
