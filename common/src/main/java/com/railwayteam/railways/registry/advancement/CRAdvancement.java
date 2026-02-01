/*
 * Steam 'n' Rails
 * Copyright (c) 2023-2025 The Railways Team
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

package com.railwayteam.railways.registry.advancement;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRAdvancements;
import com.railwayteam.railways.registry.CRTriggers;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CRAdvancement {

	static final ResourceLocation BACKGROUND = Railways.asResource("textures/gui/advancements.png");
	static final String LANG = "advancement." + Railways.MOD_ID + ".";
	static final String SECRET_SUFFIX = "\n\u00A77(Hidden Advancement)";

	private final Advancement.Builder builder;
	private final Supplier<DisplayInfo> displayInfo;
	private SimpleRailwaysTrigger builtinTrigger;
	private CRAdvancement parent;

	Advancement datagenResult;

	private final String id;
	private String title;
	private String description;

	public CRAdvancement(String id, UnaryOperator<Builder> b) {
		this.builder = Advancement.Builder.advancement();
		this.id = id;

		Builder t = new Builder();
		b.apply(t);

		if (!t.externalTrigger) {
			builtinTrigger = CRTriggers.addSimple(id + "_builtin");
			builder.addCriterion("0", builtinTrigger.instance());
		}

		this.displayInfo = () -> new DisplayInfo(
			t.icon.get(), Components.translatable(titleKey()),
			Components.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
			id.equals("root") ? BACKGROUND : null, t.type.frame, t.type.toast, t.type.announce, t.type.hide
		);

		if (t.type == TaskType.SECRET)
			description += SECRET_SUFFIX;

		CRAdvancements.ENTRIES.add(this);
	}

	private String titleKey() {
		return LANG + id;
	}

	private String descriptionKey() {
		return titleKey() + ".desc";
	}

	public boolean isAlreadyAwardedTo(Player player) {
		if (!(player instanceof ServerPlayer sp))
			return true;
		Advancement advancement = sp.getServer()
			.getAdvancements()
			.getAdvancement(Railways.asResource(id));
		if (advancement == null)
			return true;
		return sp.getAdvancements()
			.getOrStartProgress(advancement)
			.isDone();
	}

	public void awardTo(Player player) {
		if (!(player instanceof ServerPlayer sp))
			return;
		if (builtinTrigger == null)
			throw new UnsupportedOperationException(
				"Advancement " + id + " uses external Triggers, it cannot be awarded directly");
		builtinTrigger.trigger(sp);
	}

	@ApiStatus.Internal
	public void save(Consumer<Advancement> t) {
		builder.display(displayInfo.get());
		if (parent != null)
			builder.parent(parent.datagenResult);
		datagenResult = builder.save(t, Railways.asResource(id)
			.toString());
	}

	@ApiStatus.Internal
	public void provideLang(BiConsumer<String, String> consumer) {
		consumer.accept(titleKey(), title);
		consumer.accept(descriptionKey(), description);
	}

	@ApiStatus.Internal
	public enum TaskType {

		SILENT(FrameType.TASK, false, false, false),
		NORMAL(FrameType.TASK, true, false, false),
		NOISY(FrameType.TASK, true, true, false),
		EXPERT(FrameType.GOAL, true, true, false),
		SECRET(FrameType.GOAL, true, true, true),

		;

		private FrameType frame;
		private boolean toast;
		private boolean announce;
		private boolean hide;

		TaskType(FrameType frame, boolean toast, boolean announce, boolean hide) {
			this.frame = frame;
			this.toast = toast;
			this.announce = announce;
			this.hide = hide;
		}
	}

	@ApiStatus.Internal
	public class Builder {

		private TaskType type = TaskType.NORMAL;
		private boolean externalTrigger;
		private int keyIndex;
		private Supplier<ItemStack> icon;

		@ApiStatus.Internal
		public Builder special(TaskType type) {
			this.type = type;
			return this;
		}

		@ApiStatus.Internal
		public Builder after(CRAdvancement other) {
			CRAdvancement.this.parent = other;
			return this;
		}

		@ApiStatus.Internal
		public Builder icon(ItemProviderEntry<?> item) {
			return icon(() -> item.asStack());
		}

		@ApiStatus.Internal
		public Builder icon(ItemLike item) {
			return icon(() -> new ItemStack(item));
		}

		@ApiStatus.Internal
		public Builder icon(ItemStack stack) {
			return icon(() -> stack);
		}

		@ApiStatus.Internal
		public Builder icon(Supplier<ItemStack> stackSupplier) {
			icon = stackSupplier;
			return this;
		}

		@ApiStatus.Internal
		public Builder title(String title) {
			CRAdvancement.this.title = title;
			return this;
		}

		@ApiStatus.Internal
		public Builder description(String description) {
			CRAdvancement.this.description = description;
			return this;
		}

		@ApiStatus.Internal
		public Builder whenBlockPlaced(Block block) {
			return externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
		}

		@ApiStatus.Internal
		public Builder whenIconCollected() {
			return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(icon.get().getItem()));
		}

		@ApiStatus.Internal
		public Builder whenItemCollected(ItemProviderEntry<?> item) {
			return whenItemCollected(item.asStack()
				.getItem());
		}

		@ApiStatus.Internal
		public Builder whenItemCollected(ItemLike itemProvider) {
			return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
		}

		@ApiStatus.Internal
		public Builder whenItemCollected(TagKey<Item> tag) {
			return externalTrigger(InventoryChangeTrigger.TriggerInstance
				.hasItems(new ItemPredicate(tag, null, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
					EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY)));
		}

		@ApiStatus.Internal
		public Builder awardedForFree() {
			return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[] {}));
		}

		@ApiStatus.Internal
		public Builder externalTrigger(CriterionTriggerInstance trigger) {
			builder.addCriterion(String.valueOf(keyIndex), trigger);
			externalTrigger = true;
			keyIndex++;
			return this;
		}

	}

}
