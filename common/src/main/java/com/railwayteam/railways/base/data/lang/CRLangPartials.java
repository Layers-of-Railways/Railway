package com.railwayteam.railways.base.data.lang;

import com.google.gson.JsonElement;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorLangMerger;
import com.simibubi.create.foundation.data.AllLangPartials;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.data.LangPartial;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.data.PackOutput;

import java.util.function.Supplier;

public enum CRLangPartials implements LangPartial {
	INTERFACE("UI & Messages"),
	TOOLTIPS("Item Descriptions"),

	;

	private final String display;
	private final Supplier<JsonElement> provider;

	private CRLangPartials(String display) {
		this.display = display;
		this.provider = this::fromResource;
	}

	private CRLangPartials(String display, Supplier<JsonElement> customProvider) {
		this.display = display;
		this.provider = customProvider;
	}

	public String getDisplayName() {
		return display;
	}

	public JsonElement provide() {
		return provider.get();
	}

	private JsonElement fromResource() {
		String fileName = Lang.asId(name());
		String filepath = "assets/" + Railways.MODID + "/lang/default/" + fileName + ".json";
		JsonElement element = FilesHelper.loadJsonResource(filepath);
		if (element == null)
			throw new IllegalStateException(String.format("Could not find default lang file: %s", filepath));
		return element;
	}

	public static <T extends LangPartial> LangMerger createMerger(PackOutput output, String modid, String displayName,
                                                                  LangPartial[] partials) {
		LangMerger merger = new LangMerger(output, modid, displayName, new AllLangPartials[0]);
		((AccessorLangMerger) merger).setLangPartials(partials);
		return merger;
	}
}
