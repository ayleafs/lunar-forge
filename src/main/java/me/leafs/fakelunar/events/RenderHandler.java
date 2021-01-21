package me.leafs.fakelunar.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderHandler {
    private final static ResourceLocation FORGE_LOGO = new ResourceLocation("forge-client.png");

    @SubscribeEvent
    public void onRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiScreen gui = event.gui;
        if (!(gui instanceof GuiInventory)) {
            return; // look for GuiInventory
        }

        // load the forge logo onto the stack
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        manager.bindTexture(FORGE_LOGO);

        int width = gui.width, height = gui.height;
        int onScreenW = 678 / 6;
        int onScreenH = 96 / 6;

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        // I literally have no clue what the fuck is happening in here
        Gui.drawModalRectWithCustomSizedTexture(width - onScreenW - 5, height - onScreenH - 5, 0, 0, onScreenW, onScreenH, onScreenW, onScreenH);

        GlStateManager.disableBlend();
    }
}
