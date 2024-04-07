package com.railwayteam.railways.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.railwayteam.railways.Railways;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public enum CRKeys {
    BOGEY_MENU("bogey_menu", GLFW.GLFW_KEY_LEFT_ALT),
    CYCLE_MENU("cycle_menu", GLFW.GLFW_KEY_LEFT_ALT);

    private KeyMapping keybind;
    private final String description;
    private final int key;
    private final boolean modifiable;

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
            registerKeyBinding(key.keybind);
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
        return KeyBindingHelper.getBoundKeyOf(keybind)
                .getValue();
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
}
