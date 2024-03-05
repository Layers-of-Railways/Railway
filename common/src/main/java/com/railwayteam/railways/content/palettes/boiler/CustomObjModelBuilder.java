package com.railwayteam.railways.content.palettes.boiler;

import com.google.gson.JsonObject;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CustomObjModelBuilder<T extends ModelBuilder<T>> extends ObjModelBuilder<T> {
    protected CustomObjModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(parent, existingFileHelper);
    }

    public static <T extends ModelBuilder<T>> CustomObjModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CustomObjModelBuilder<>(parent, existingFileHelper);
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        json.addProperty("porting_lib:loader", "porting_lib:obj");
        return json;
    }
}
