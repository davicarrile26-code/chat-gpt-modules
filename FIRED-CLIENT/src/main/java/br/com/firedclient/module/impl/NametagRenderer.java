package br.com.firedclient.module.impl;

import br.com.firedclient.FiredClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Custom player nameplates for FIRED CLIENT 1.8.9. */
public class NametagRenderer {
    @SubscribeEvent
    public void onRenderPlayerName(RenderLivingEvent.Specials.Pre event) {
        if (!(event.entity instanceof EntityPlayer)) return;
        br.com.firedclient.module.ClientModule base = FiredClient.MODULES.getByName("Nametags");
        if (base == null || !base.isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.fontRendererObj == null) return;
        EntityPlayer player = (EntityPlayer) event.entity;
        String name = player.getDisplayName().getFormattedText();
        boolean showIcon = player.getUniqueID().equals(mc.thePlayer.getUniqueID())
                && ((FeatureModule) base).isNametagsShowFiredIcon();
        RenderManager renderManager = mc.getRenderManager();
        int textWidth = mc.fontRendererObj.getStringWidth(name);
        int totalWidth = textWidth + (showIcon ? 9 : 0);
        int left = -totalWidth / 2;
        event.setCanceled(true);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) event.x, (float) event.y + player.height + 0.5F, (float) event.z);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        Gui.drawRect(left - 2, -2, left + totalWidth + 2, 10, 0x55000000);
        mc.fontRendererObj.drawStringWithShadow(name, left, 0, 0xFFFFFFFF);
        if (showIcon) drawFiredMark(left + textWidth + 2, 1);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
    /** Fixed 5x8-pixel FIRED flame, regardless of player distance or name length. */
    private void drawFiredMark(int x, int y) {
        Gui.drawRect(x + 2, y, x + 4, y + 2, 0xFFFFB04D);
        Gui.drawRect(x + 1, y + 2, x + 5, y + 5, 0xFFFF783B);
        Gui.drawRect(x, y + 4, x + 5, y + 7, 0xFFE63738);
        Gui.drawRect(x + 2, y + 5, x + 3, y + 7, 0xFFFFC06A);
    }
}