package com.railwayteam.railways.forge.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;


/*
 * What this does is
 * 1. Load's the "Level" parameter to stack
 * 2. It invokes the utility method
 * 3. Adds DUP instruction
 * 4. It creates a label to go back to the DUP instruction which holds the method call
 * 5. It then adds a Jump instruction to go to the end of the method/return default if the method call result is null
 * 6. If it's not null, aka hasn't jumped to the end, then it returns the value of the called method
 * 7. Places a label and POP so that the original code could be called
 *
 * Transformed method looks like the below in ContainerLevelAccess
 *
 *    static ContainerLevelAccess create(Level level, BlockPos pos) {
 *        GuiBlockLevelAccess var10000 = GuiBlockUtils.createNewGuiContraptionWorld(level);
 *        return (ContainerLevelAccess)(var10000 != null ? var10000 : new 2(level, pos));
 *    }
 */
public class ContainerLevelAccessASM {
    public static void processNode(ClassNode classNode) {
        for (MethodNode node : classNode.methods) {
            if (node.name.equals("create")) {
                InsnList instructions = node.instructions;
                InsnList newInstructions = new InsnList();

                // Load parameter level to stack
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

                // Call createNewGuiContraptionWorld method
                newInstructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/railwayteam/railways/content/moving_bes/GuiBlockUtils",
                        "createNewGuiContraptionWorld",
                        "(Lnet/minecraft/world/level/Level;)Lcom/railwayteam/railways/content/moving_bes/GuiBlockLevelAccess;",
                        false
                ));

                // Add DUP
                newInstructions.add(new InsnNode(Opcodes.DUP));

                // Create label for here
                LabelNode label = new LabelNode(new Label());

                // If null, jump to the label (the invoked call)
                newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, label));

                // Return the value of said invoked call
                newInstructions.add(new InsnNode(Opcodes.ARETURN));

                // insert label here
                newInstructions.add(label);

                // Add POP
                newInstructions.add(new InsnNode(Opcodes.POP));

                instructions.insertBefore(instructions.getFirst(), newInstructions);
            }
        }

//        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//        classNode.accept(writer);
//        byte[] bytes = writer.toByteArray();
//        File outputFile = new File("/home/ithundxr/Projects/Modding/Railway/common/run/out.class");
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//            outputStream.write(bytes);
//        } catch (IOException ignored) {}
    }
}
