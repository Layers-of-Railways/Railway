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

package com.railwayteam.railways.forge.asm.testing;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.railwayteam.railways.forge.asm.RollingModeEnumAdder.processRollingMode;

/*
This class is just for testing asm transformation and should not be called during normal mod use
 */
@ApiStatus.Internal
@Deprecated(forRemoval = true) // not actually going to remove this, just want using it to be a very bad warning
public class TMPMain {
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public static void main(String[] args) {
        Path path = Path.of("/home/sam/MinecraftFabric/Railway/forge/run/config/in.class");
        ClassReader reader;
        try {
            reader = new ClassReader(Files.readAllBytes(path));
        } catch (IOException ignored) {
            System.out.println("Failed");
            return;
        }
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        System.out.println("Processing...");
        processRollingMode(classNode);
        System.out.println("Success!");
    }
}
