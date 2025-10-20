/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.custom_bogeys.renderer.unified.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplayHolder;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public class UnifiedBogeyRenderer implements BogeyRenderer, BogeyDisplayHolder {
    private final Couple<Renderer> renderers;
    private final @Nullable BogeyRenderer customRenderer;

    public UnifiedBogeyRenderer(BogeyDisplay.Factory factory) {
        this.renderers = Couple.createWithContext(inContraption -> Renderer.create(factory, inContraption));
        this.customRenderer = factory.createCustomRenderer();
    }

    @Override
    public void runWithDisplay(Consumer<BogeyDisplay> consumer) {
        consumer.accept(renderers.getFirst().display);
        consumer.accept(renderers.getSecond().display);

        if (customRenderer instanceof BogeyDisplayHolder customDisplayHolder) {
            customDisplayHolder.runWithDisplay(consumer);
        }
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean inContraption) {
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        final BlockState air = Blocks.AIR.defaultBlockState();

        final Renderer renderer = renderers.get(inContraption);

        // prepare
        Matrix4f pose = poseStack.last().pose();
        for (RenderedElement element : renderer.allElements) {
            element.setTransform(pose);
        }

        // update
        renderer.display.update(bogeyData, wheelAngle);

        // render
        PoseStack ms = new PoseStack();

        for (RenderedElement.Single element : renderer.singleElements) {
            var mat = element.element().pose;
            ms.last().pose().set(mat);
            ms.last().normal().set(mat);

            CachedBuffers.partial(element.model(), air)
                .light(packedLight)
                .overlay(packedOverlay)
                .renderInto(ms, buffer);
        }

        for (RenderedElement.Multiple elements : renderer.multipleElements) {
            SuperByteBuffer sbb = CachedBuffers.partial(elements.model(), air);

            for (RenderedElement element : elements.elements()) {
                var mat = element.pose;
                ms.last().pose().set(mat);
                ms.last().normal().set(mat);

                sbb.light(packedLight)
                    .overlay(packedOverlay)
                    .renderInto(ms, buffer);
            }
        }

        for (RenderedElement.Scrolling element : renderer.scrollingElements) {
            var mat = element.element.pose;
            ms.last().pose().set(mat);
            ms.last().normal().set(mat);

            float spriteSize = element.entry.getTarget().getV1() - element.entry.getTarget().getV0();

            float scrollV = element.shiftV;
            scrollV = scrollV - Mth.floor(scrollV);
            scrollV = scrollV * spriteSize * 0.5f;

            CachedBuffers.partial(element.model, air)
                .light(packedLight)
                .overlay(packedOverlay)
                .shiftUVScrolling(element.entry, scrollV)
                .renderInto(ms, buffer);
        }

        if (customRenderer != null) {
            customRenderer.render(bogeyData, wheelAngle, partialTick, poseStack, bufferSource, packedLight, packedOverlay, inContraption);
        }
    }

    private record Renderer(
        BogeyDisplay display,
        List<RenderedElement.Single> singleElements,
        List<RenderedElement.Multiple> multipleElements,
        List<RenderedElement.Scrolling> scrollingElements,
        List<RenderedElement> allElements
    ) {
        private static Renderer create(BogeyDisplay.Factory factory, boolean inContraption) {
            ArrayList<RenderedElement.Single> singleElements = new ArrayList<>();
            ArrayList<RenderedElement.Multiple> multipleElements = new ArrayList<>();
            ArrayList<RenderedElement.Scrolling> scrollingElements = new ArrayList<>();

            var prov = new RenderedElementProvider(singleElements, multipleElements, scrollingElements);
            BogeyDisplay display = factory.create(prov, inContraption);
            prov.freeze();

            List<RenderedElement> allElements = new ArrayList<>();
            singleElements.forEach(s -> allElements.add(s.element()));
            multipleElements.forEach(m -> Collections.addAll(allElements, m.elements()));
            scrollingElements.forEach(s -> allElements.add(s.element));

            return new Renderer(display, singleElements, multipleElements, scrollingElements, allElements);
        }
    }
}
