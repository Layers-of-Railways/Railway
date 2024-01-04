package com.railwayteam.railways.content.legacy.selection_menu;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.bogey_menu.BogeyMenuScreen;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.EntityUtils;
import com.railwayteam.railways.util.Utils;
import com.railwayteam.railways.util.packet.BogeyStyleSelectionPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllKeys;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Pair;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Deprecated(forRemoval = true)
@Environment(EnvType.CLIENT)
public class BogeyCategoryHandlerClient {

    static final ResourceLocation FAVORITES_CATEGORY = Railways.asResource("favorites_category");
    static final ResourceLocation MANAGE_FAVORITES_CATEGORY = Railways.asResource("manage_favorites_category");

    private static final Map<ResourceLocation, NonNullSupplier<? extends ItemLike>> STYLE_CATEGORIES = new HashMap<>();
    private static final List<ResourceLocation> STYLE_CATEGORY_ORDER = new ArrayList<>();

    public static ResourceLocation getCategoryId(int i) {
        if (i == 0)
            return FAVORITES_CATEGORY;
        return STYLE_CATEGORY_ORDER.get(i - 1);
    }

    public static int categoryCount() {
        return STYLE_CATEGORIES.size() + 1;
    }

    public static NonNullSupplier<? extends ItemLike> getCategoryIcon(ResourceLocation id) {
        if (id == FAVORITES_CATEGORY)
            return () -> Items.ENCHANTED_GOLDEN_APPLE;
        return STYLE_CATEGORIES.get(id);
    }

    public static NonNullSupplier<? extends ItemLike> getCategoryIcon(int i) {
        return getCategoryIcon(getCategoryId(i));
    }

    public static Map<ResourceLocation, BogeyStyle> getStylesInCategory(int i) {
        return getStylesInCategory(getCategoryId(i));
    }

    private static Map<ResourceLocation, BogeyStyle> filterHidden(Map<ResourceLocation, BogeyStyle> orig, boolean copyFirst) {
        if (orig.isEmpty())
            return orig;
        Map<ResourceLocation, BogeyStyle> map;
        if (copyFirst) {
            map = new HashMap<>(orig);
        } else {
            map = orig;
        }

        map.entrySet().removeIf(entry -> CRBogeyStyles.hideInSelectionMenu(entry.getValue()));
        return map;
    }

    public static Map<ResourceLocation, BogeyStyle> getStylesInCategory(ResourceLocation id) {
        if (id == MANAGE_FAVORITES_CATEGORY || id == FAVORITES_CATEGORY) {
            Map<ResourceLocation, BogeyStyle> map = new HashMap<>();
            for (BogeyStyle style : getFavorites()) {
                map.put(style.name, style);
            }
            return map;
        }
        if(Mods.EXTENDEDBOGEYS.isLoaded){
            if(id.equals(Railways.asResource("extendedbogeys"))) {
                Map<ResourceLocation, BogeyStyle> EB = new HashMap<>(AllBogeyStyles.CYCLE_GROUPS.get(Create.asResource(AllBogeyStyles.STANDARD_CYCLE_GROUP)));
                EB.remove(AllBogeyStyles.STANDARD.name);
                EB.remove(CRBogeyStyles.INVISIBLE.name);
                EB.remove(CRBogeyStyles.WIDE_DEFAULT.name);
                EB.remove(CRBogeyStyles.NARROW_DEFAULT.name);
                EB.remove(CRBogeyStyles.NARROW_DOUBLE_SCOTCH.name);
                return filterHidden(EB, false);
            }
            if(id.equals(Create.asResource(AllBogeyStyles.STANDARD_CYCLE_GROUP))){
                Map<ResourceLocation, BogeyStyle> noEB = new HashMap<>(AllBogeyStyles.CYCLE_GROUPS.get(Create.asResource(AllBogeyStyles.STANDARD_CYCLE_GROUP)));
                noEB.remove(new ResourceLocation("extendedbogeys", "single_axle"));
                noEB.remove(new ResourceLocation("extendedbogeys", "double_axle"));
                noEB.remove(new ResourceLocation("extendedbogeys", "triple_axle"));
                return filterHidden(noEB, false);
            }
        }
        return filterHidden(AllBogeyStyles.CYCLE_GROUPS.getOrDefault(id, new HashMap<>()), true);
    }

    public static ResourceLocation getStyleId(ResourceLocation categoryId, int styleIdx) {
        if (categoryId == MANAGE_FAVORITES_CATEGORY) {
            return getFavorites().get(styleIdx).name;
        }
        return getStylesInCategory(categoryId).keySet().toArray(ResourceLocation[]::new)[styleIdx];
    }

    public static BogeyStyle getStyle(int categoryIdx, int styleIdx) {
        return getStyle(getCategoryId(categoryIdx), styleIdx);
    }

    public static BogeyStyle getStyle(ResourceLocation categoryId, int styleIdx) {
        int offset = 0;
        for (int i = 0; i < styleCount(categoryId); i++) {
            BogeyStyle style = getStylesInCategory(categoryId).get(getStyleId(categoryId, i));

            if (!showIndividualSizes(style)) {
                if (i + offset == styleIdx)
                    return style;
                continue;
            }
            for (int sizeIdx = 0; sizeIdx < style.validSizes().size(); sizeIdx++) {
                if (i + offset + sizeIdx == styleIdx)
                    return style;
            }
            offset += style.validSizes().size() - 1;
        }

        if (Utils.isDevEnv())
            Railways.LOGGER.error("getStyle failed! category: " + categoryId + ", styleIdx: " + styleIdx);
        return getStylesInCategory(categoryId).get(getStyleId(categoryId, styleIdx));
    }

    public static @Nullable BogeySize getSize(ResourceLocation categoryId, int styleIdx) {
        int offset = 0;
        for (int i = 0; i < styleCount(categoryId); i++) {
            BogeyStyle style = getStylesInCategory(categoryId).get(getStyleId(categoryId, i));

            if (!showIndividualSizes(style)) {
                if (i + offset == styleIdx)
                    return null;
                continue;
            }
            int sizeIdx = 0;
            for (BogeySize size : style.validSizes()) {
                if (i + offset + sizeIdx == styleIdx)
                    return size;
                sizeIdx += 1;
            }

            offset += style.validSizes().size() - 1;
        }

        if (Utils.isDevEnv())
            Railways.LOGGER.error("getStyle failed! category: " + categoryId + ", styleIdx: " + styleIdx);
        return null;
    }

    public static int styleCount(ResourceLocation categoryId) {
        if (categoryId == MANAGE_FAVORITES_CATEGORY)
            return styleAndSizeCount(getFavorites());
        return styleAndSizeCount(getStylesInCategory(categoryId).values());
    }

    private static int styleAndSizeCount(Collection<BogeyStyle> styles) {
        int count = 0;
        for (BogeyStyle style : styles) {
            count += showIndividualSizes(style) ? style.validSizes().size() : 1;
        }
        return count;
    }

    public static void registerStyleCategory(String name, NonNullSupplier<? extends ItemLike> icon) {
        registerStyleCategory(Railways.asResource(name), icon);
    }

    public static void registerStyleCategory(ResourceLocation id, NonNullSupplier<? extends ItemLike> icon) {
        STYLE_CATEGORIES.put(id, icon);
        STYLE_CATEGORY_ORDER.add(id);
    }

    @ApiStatus.Internal
    public static int COOLDOWN = 0;

    public static @Nullable BogeyStyle getSelectedStyle() {
        return selectedStyle;
    }

    public static @Nullable BogeySize getSelectedSize() {
        return selectedSize;
    }

    @Nullable
    private static BogeyStyle selectedStyle;
    @Nullable
    private static BogeySize selectedSize;

    static void setSelectedStyle(@Nullable BogeyStyle style, @Nullable BogeySize size) {
        if (selectedStyle == style && selectedSize == size)
            return;
        selectedStyle = style;
        selectedSize = size;
        if (style == null)
            style = AllBogeyStyles.STANDARD;
        CRPackets.PACKETS.send(new BogeyStyleSelectionPacket(style, size));
    }

    @Nullable
    private static List<BogeyStyle> favorites = null;

    @NotNull
    static List<BogeyStyle> getFavorites() {
        if (favorites == null)
            loadFavorites();
        return favorites;
    }

    static void optimizeFavorites() {
        List<BogeyStyle> newFavorites = new ArrayList<>();
        for (BogeyStyle style : getFavorites()) {
            if (!newFavorites.contains(style))
                newFavorites.add(style);
        }
        favorites = newFavorites;
        saveFavorites();
    }

    public static void loadFavorites() {
        favorites = new ArrayList<>();
        try {
            Minecraft mc = Minecraft.getInstance();
            File file = new File(mc.gameDirectory, "snr_favorite_styles.nbt");
            CompoundTag tag = NbtIo.read(file);
            if (tag == null)
                return;

            if (tag.contains("Favorites", Tag.TAG_LIST)) {
                ListTag favoritesList = tag.getList("Favorites", Tag.TAG_STRING);
                if (favorites == null)
                    favorites = new ArrayList<>();
                favorites.clear();
                for (Tag favoriteTag : favoritesList) {
                    if (favoriteTag instanceof StringTag stringTag) {
                        ResourceLocation loc = ResourceLocation.tryParse(stringTag.getAsString());
                        if (loc == null)
                            continue;
                        if (AllBogeyStyles.BOGEY_STYLES.containsKey(loc)) {
                            favorites.add(AllBogeyStyles.BOGEY_STYLES.get(loc));
                        }
                    }
                }
            }
            optimizeFavorites();
        } catch (Exception e) {
            Railways.LOGGER.error("Failed to load favorite styles", e);
        }
    }

    public static boolean showIndividualSizes(BogeyStyle style) {
        return style.name.getNamespace().equals("extendedbogeys") && style.name.getPath().equals("double_axle");
    }

    public static void saveFavorites() {
        if (favorites == null)
            return;
        try {
            CompoundTag tag = new CompoundTag();
            ListTag listTag = new ListTag();
            for (BogeyStyle style : favorites) {
                listTag.add(StringTag.valueOf(style.name.toString()));
            }
            tag.put("Favorites", listTag);
            NbtIo.write(tag, new File(Minecraft.getInstance().gameDirectory, "snr_favorite_styles.nbt"));
        } catch (Exception e) {
            Railways.LOGGER.error("Failed to save favorite styles", e);
        }
    }

    public static void clientTick() {
        if (COOLDOWN > 0 && !AllKeys.TOOL_MENU.isPressed())
            COOLDOWN--;
    }

    public static void onKeyInput(int key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (key != AllKeys.TOOL_MENU.getBoundCode() || !pressed)
            return;
        if (COOLDOWN > 0)
            return;
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        if (!EntityUtils.isHolding(player, AllBlocks.RAILWAY_CASING::isIn))
            return;

        ScreenOpener.open(new BogeyMenuScreen());
    }

    public static final Map<Pair<BogeyStyle, @Nullable BogeySize>, ResourceLocation> ICONS = new HashMap<>();

    public static void addIcon(BogeyStyle style, String name) {
        ICONS.put(Pair.of(style, null), Railways.asResource("textures/gui/bogey_icons/"+name+"_icon.png"));
    }

    /**
     * Note: it is only valid to add a sized icon for a style that shows individual sizes
     */
    public static void addIcon(BogeyStyle style, BogeySize size, String name) {
        if (!showIndividualSizes(style)) {
            if (Utils.isDevEnv()) {
                throw new IllegalStateException("Cannot add sized icon for style that does not show individual sizes");
            } else {
                addIcon(style, name);
                return;
            }
        }
        ICONS.put(Pair.of(style, size), Railways.asResource("textures/gui/bogey_icons/"+name+"_icon.png"));
    }

    public static boolean hasIcon(BogeyStyle style, BogeySize size) {
        return (showIndividualSizes(style) && ICONS.containsKey(Pair.of(style, size))) || ICONS.containsKey(Pair.of(style, null));
    }

    public static ResourceLocation getIcon(BogeyStyle style, BogeySize size) {
        if (showIndividualSizes(style) && ICONS.containsKey(Pair.of(style, size)))
            return ICONS.get(Pair.of(style, size));
        return ICONS.get(Pair.of(style, null));
    }

    static {
        for (BogeyStyle style : AllBogeyStyles.BOGEY_STYLES.values()) {
            if (CRBogeyStyles.hideInSelectionMenu(style))
                continue;
            if (style.name.getNamespace().equals(Railways.MODID))
                addIcon(style, style.name.getPath());
            else if (Mods.EXTENDEDBOGEYS.isLoaded && style.name.getNamespace().equals("extendedbogeys"))
                addIcon(style, "eb_" + style.name.getPath());
        }
        addIcon(AllBogeyStyles.STANDARD, "default");
    }
}
