package me.leafs.fakelunar.asm;

import me.leafs.fakelunar.LunarForge;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class TransformNetClient implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        // don't touch the class if it's not nethandlerplayclient
        if (!transformedName.equalsIgnoreCase("net.minecraft.client.network.NetHandlerPlayClient")) {
            return bytes;
        }

        // read the class, you know the drill
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        // go through and find the right method
        for (MethodNode method : node.methods) {
            String mappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(node.name, method.name, method.desc);

            if (!mappedName.equalsIgnoreCase("handleJoinGame") && !mappedName.equalsIgnoreCase("func_147282_a")) {
                continue; // fuck this method, not the one we want
            }

            String packetHelper = Type.getInternalName(LunarForge.class);

            // inject a method call at the end of the method to LunarForge.sendLC
            MethodInsnNode insn = new MethodInsnNode(Opcodes.INVOKESTATIC, packetHelper, "registerChannels", Type.getMethodDescriptor(Type.VOID_TYPE));

            // find a RETURN opcode and inject a call to the method
            ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode next = iterator.next();
                if (next.getOpcode() != Opcodes.RETURN) {
                    continue;
                }

                method.instructions.insertBefore(next, insn);
                break;
            }

            break;
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        // return the transformed class
        return writer.toByteArray();
    }
}
