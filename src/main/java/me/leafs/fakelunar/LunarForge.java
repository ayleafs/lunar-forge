package me.leafs.fakelunar;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.leafs.fakelunar.events.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.common.MinecraftForge;

public class LunarForge {
    private static final String LUNAR_CHANNEL = "Lunar-Client";

    /**
     *  A call to this is injected into the Minecraft startGame method.
     *  The reason this is necessary is to stop Forge from including this
     *  in the mod list (even though a mod list and saying you're on Lunar
     *  is still a dead giveaway you're on Forge).
     */
    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }

    public static void registerLC() {
        NetworkManager nm = Minecraft.getMinecraft().getNetHandler().getNetworkManager();

        // create the packet buffer
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(LUNAR_CHANNEL.getBytes());

        // send custom plugin channel register packet
        nm.sendPacket(new C17PacketCustomPayload("REGISTER", new PacketBuffer(buffer)));
    }
}
