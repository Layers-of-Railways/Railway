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

package com.railwayteam.railways.forge.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public abstract class RollingModeEnumAdder {
    // ~/.jdks/openjdk-17.0.2/bin/javap -v -p out.class
    public static void processRollingMode(ClassNode classNode) {
        FieldNode TRACK_REPLACE_field = new FieldNode(ACC_PUBLIC | ACC_STATIC | ACC_FINAL | ACC_ENUM, "TRACK_REPLACE", "Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;", null, null);
        classNode.fields.add(3, TRACK_REPLACE_field); // insert after WIDE_FILL but before translationKey
        for (MethodNode node : classNode.methods) {
            if (node.name.equals("<clinit>")) {
                // Remove the last 3 (and place at the end)
                /*
48: invokestatic  #102                // Method $values:()[Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;
51: putstatic     #30                 // Field $VALUES:[Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;
54: return
                 */
                int count = 3;
                AbstractInsnNode[] end = new AbstractInsnNode[count];
                for (int i = count-1; i >= 0; i--) {
                    AbstractInsnNode insnNode = node.instructions.getLast();
                    node.instructions.remove(insnNode);
                    end[i] = insnNode;
                }
                Label label = new Label();
                node.visitLabel(label);
                node.visitLineNumber(150, label);
                node.visitTypeInsn(NEW, "com/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode");
                node.visitInsn(DUP);
                node.visitLdcInsn("TRACK_REPLACE");
                node.visitInsn(ICONST_3);
                node.visitFieldInsn(GETSTATIC, "com/railwayteam/railways/registry/CRIcons", "I_SWAP_TRACKS", "Lcom/railwayteam/railways/registry/CRIcons;");
                node.visitMethodInsn(INVOKESPECIAL,
                    "com/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode",
                    "<init>", "(Ljava/lang/String;ILcom/simibubi/create/foundation/gui/AllIcons;)V", false);
                node.visitFieldInsn(PUTSTATIC, "com/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode", "TRACK_REPLACE", "Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;");
                for (AbstractInsnNode insnNode : end) {
                    node.instructions.add(insnNode);
                }
            }

            if (node.name.equals("$values")) {
                // Remove the last 3 (and place at the end)
                /*
48: invokestatic  #102                // Method $values:()[Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;
51: putstatic     #30                 // Field $VALUES:[Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;
54: return
                 */
                AbstractInsnNode labelInsn = node.instructions.get(0);
                AbstractInsnNode lineNumInsn = node.instructions.get(1);
                AbstractInsnNode sizeInsn = node.instructions.get(2); // discard this
                node.instructions.remove(labelInsn);
                node.instructions.remove(lineNumInsn);
                node.instructions.remove(sizeInsn);

                node.instructions.insert(new InsnNode(ICONST_4)); // change size of values to 4
                node.instructions.insert(lineNumInsn); // re-insert headers at the end
                node.instructions.insert(labelInsn);

                // temporarily take out return instruction
                AbstractInsnNode ret = node.instructions.getLast();
                node.instructions.remove(ret);

                // add TRACK_REPLACE item
                node.visitInsn(DUP);
                node.visitInsn(ICONST_3); // index
                node.visitFieldInsn(GETSTATIC, "com/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode", "TRACK_REPLACE", "Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;");
                node.visitInsn(AASTORE);
                node.instructions.add(ret);
            }
        }
        /*
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        byte[] bytes = writer.toByteArray();
        File outputFile = new File("/home/sam/MinecraftFabric/Railway/forge/run/config/out.class");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(bytes);
        } catch (IOException ignored) {} // */
        //return ILaunchPluginService.super.processClass(phase, classNode, classType);
    }
}
