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

package com.railwayteam.railways.multiloader;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.CRTagGen;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Consumer;

/**
 * A common tag is a trio of tags: one for common, one for fabric, and one for forge.
 * The common tag contains both loader tags and should only be used for querying.
 * Content is added to both loader tags separately.
 */
public class CommonTag<T> {
    public final TagKey<T> tag, fabric, forge;

    public CommonTag(TagKey<T> common, TagKey<T> fabric, TagKey<T> forge) {
        this.tag = common;
        this.fabric = fabric;
        this.forge = forge;
    }

    public CommonTag(ResourceKey<? extends Registry<T>> registry, ResourceLocation common, ResourceLocation fabric, ResourceLocation forge) {
        this(TagKey.create(registry, common), TagKey.create(registry, fabric), TagKey.create(registry, forge));
    }

    public static <T> CommonTag<T> conventional(ResourceKey<? extends Registry<T>> registry, String common, String fabric, String forge) {
        return new CommonTag<>(
                registry,
                Railways.asResource("internal/" + common),
                new ResourceLocation("c", fabric),
                new ResourceLocation("forge", forge)
        );
    }

    public CommonTag<T> generateBoth(RegistrateTagsProvider<T> tags, Consumer<TagAppender<T>> consumer) {
        consumer.accept(CRTagGen.tagAppender(tags, fabric));
        consumer.accept(CRTagGen.tagAppender(tags, forge));
        return this;
    }

    public CommonTag<T> generateCommon(RegistrateTagsProvider<T> tags) {
        CRTagGen.tagAppender(tags, tag)
                .addOptionalTag(fabric.location())
                .addOptionalTag(forge.location());
        return this;
    }
}
