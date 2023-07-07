package com.railwayteam.railways.forge;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.EnumSet;

import static org.objectweb.asm.Opcodes.*;

public class CRLaunchPluginService implements ILaunchPluginService {
    @Override
    public String name() {
        return "railways";
    }

    private static final EnumSet<Phase> YES = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NO = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        if (classType.getDescriptor().equals("Lcom/simibubi/create/content/contraptions/actors/roller/RollerBlockEntity$RollingMode;")) {
            return YES;
        }
        return NO;
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType) {
        processRollingMode(classNode);
        return true;
    }

    // ~/.jdks/openjdk-17.0.2/bin/javap -v -p out.class
    public void processRollingMode(ClassNode classNode) {
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
