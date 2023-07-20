/*package com.railwayteam.railways.mixin;

/*
These different mappings may all be needed to ensure that this mixin works in all environments

 */
/*
@Mixin(value = LootTable.class, priority = 10000)
public class MixinLootTables {
    private static boolean ignoreNextError = false;
    // fix-me
    @Inject(method = "m_upgvhpqp", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "method_20711", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError1(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "a", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError2(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private void cancelError3(ResourceManager manager, ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    // fix-me
    @SuppressWarnings({"UnresolvedMixinReference"})
    @Redirect(method = {
            "m_upgvhpqp",
            "method_20711",
            "a",
            "lambda$apply$0"
    }, at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private static void snr$error(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Redirect(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void snr$error2(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }
}
*/