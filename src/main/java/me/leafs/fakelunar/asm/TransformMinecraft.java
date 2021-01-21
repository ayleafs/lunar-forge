package me.leafs.fakelunar.asm;

import me.leafs.fakelunar.LunarForge;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformMinecraft implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (!transformedName.equalsIgnoreCase("net.minecraft.client.Minecraft")) {
            return bytes;
        }

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        for (MethodNode method : node.methods) {
            String mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(node.name, method.name, method.desc);
            if (!mapped.equalsIgnoreCase("startGame") && !mapped.equalsIgnoreCase("func_71384_a")) {
                continue;
            }

            String packetHelper = Type.getInternalName(LunarForge.class);

            // inject a method call at the end of the method to LunarForge.registerEvents (idiot)
            MethodInsnNode insn = new MethodInsnNode(Opcodes.INVOKESTATIC, packetHelper, "registerEvents", Type.getMethodDescriptor(Type.VOID_TYPE));
            method.instructions.insertBefore(method.instructions.getFirst(), insn);

            break;
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);

        return writer.toByteArray();
    }
}
