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

package com.railwayteam.railways.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.railwayteam.railways.Railways;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public enum CRKeys {
    BOGEY_MENU("bogey_menu", GLFW.GLFW_KEY_LEFT_ALT),
    CYCLE_MENU("cycle_menu", GLFW.GLFW_KEY_LEFT_ALT);

    private KeyMapping keybind;
    private final String description;
    private final int key;
    private final boolean modifiable;

    // Needed to make keys not appear to conflict between Steam 'n' Rails & Create
    public static final Set<KeyMapping> NON_CONFLICTING_KEYMAPPINGS = new HashSet<>();

    CRKeys(String description, int defaultKey) {
        this.description = Railways.MODID + ".keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
    }

    public static void register() {
        for (CRKeys key : values()) {
            if (!key.modifiable)
                continue;
            key.keybind = new KeyMapping(key.description, key.key, Railways.NAME);
            NON_CONFLICTING_KEYMAPPINGS.add(key.keybind);
            registerKeyBinding(key.keybind);
        }
    }

    public static void fixBinds() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        for (CRKeys key : values()) {
            if (key.keybind == null || key.keybind.isUnbound())
                continue;
            key.keybind.setDown(InputConstants.isKeyDown(window, key.getBoundCode()));
        }
    }

    public KeyMapping getKeybind() {
        return keybind;
    }

    public boolean isPressed() {
        if (!modifiable)
            return isKeyDown(key);
        return keybind.isDown();
    }

    public String getBoundKey() {
        return keybind.getTranslatedKeyMessage()
                .getString()
                .toUpperCase();
    }

    public int getBoundCode() {
        return getBoundCode(keybind);
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), key);
    }

    public static boolean isMouseButtonDown(int button) {
        return GLFW.glfwGetMouseButton(Minecraft.getInstance()
                .getWindow()
                .getWindow(), button) == 1;
    }

    public static boolean ctrlDown() {
        return Screen.hasControlDown();
    }

    public static boolean shiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean altDown() {
        return Screen.hasAltDown();
    }

    @ExpectPlatform
    private static void registerKeyBinding(KeyMapping keyMapping) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static int getBoundCode(KeyMapping keyMapping) {
        throw new AssertionError();
    }
}
