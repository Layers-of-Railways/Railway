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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.registry.advancement.CriterionTriggerBase;
import com.railwayteam.railways.registry.advancement.SimpleRailwaysTrigger;
import net.minecraft.advancements.CriteriaTriggers;

import java.util.LinkedList;
import java.util.List;

public class CRTriggers {

	private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

	public static SimpleRailwaysTrigger addSimple(String id) {
		return add(new SimpleRailwaysTrigger(id));
	}

	private static <T extends CriterionTriggerBase<?>> T add(T instance) {
		triggers.add(instance);
		return instance;
	}

	public static void register() {
		triggers.forEach(CriteriaTriggers::register);
	}

}
