package com.railwayteam.railways.content.palettes.boiler.fabric;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.CustomLoaderBuilder;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ObjModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    public static <T extends ModelBuilder<T>> ObjModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ObjModelBuilder<>(parent, existingFileHelper);
    }

    private ResourceLocation modelLocation;
    private Boolean automaticCulling;
    private Boolean shadeQuads;
    private Boolean flipV;
    private Boolean emissiveAmbient;
    private ResourceLocation mtlOverride;

    // needed so that the right loader is called for forge and fabric
    private static final ResourceLocation FORGE_OBJ = new ResourceLocation("forge", "obj");
    private static final ResourceLocation PORTING_LIB_LOADER = new ResourceLocation("porting_lib", "loader");
    private static final ResourceLocation PORTING_LIB_OBJ = new ResourceLocation("porting_lib", "obj");

    protected ObjModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(FORGE_OBJ, parent, existingFileHelper);
    }

    public ObjModelBuilder<T> modelLocation(ResourceLocation modelLocation) {
        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(modelLocation, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", modelLocation);
        this.modelLocation = modelLocation;
        return this;
    }

    public ObjModelBuilder<T> automaticCulling(boolean automaticCulling) {
        this.automaticCulling = automaticCulling;
        return this;
    }

    public ObjModelBuilder<T> shadeQuads(boolean shadeQuads) {
        this.shadeQuads = shadeQuads;
        return this;
    }

    public ObjModelBuilder<T> flipV(boolean flipV) {
        this.flipV = flipV;
        return this;
    }

    public ObjModelBuilder<T> emissiveAmbient(boolean ambientEmissive) {
        this.emissiveAmbient = ambientEmissive;
        return this;
    }

    public ObjModelBuilder<T> overrideMaterialLibrary(ResourceLocation mtlOverride) {
        Preconditions.checkNotNull(mtlOverride, "mtlOverride must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(mtlOverride, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", mtlOverride);
        this.mtlOverride = mtlOverride;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        json.addProperty(PORTING_LIB_LOADER.toString(), PORTING_LIB_OBJ.toString());

        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");

        json.addProperty("model", modelLocation.toString());

        if (automaticCulling != null)
            json.addProperty("automatic_culling", automaticCulling);

        if (shadeQuads != null)
            json.addProperty("shade_quads", shadeQuads);

        if (flipV != null)
            json.addProperty("flip_v", flipV);

        if (emissiveAmbient != null)
            json.addProperty("emissive_ambient", emissiveAmbient);

        if (mtlOverride != null)
            json.addProperty("mtl_override", mtlOverride.toString());

        return json;
    }
}
