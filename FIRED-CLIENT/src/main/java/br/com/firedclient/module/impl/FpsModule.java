package br.com.firedclient.module.impl;

import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class FpsModule extends ClientModule implements HudModule {
    public static boolean showFps = true;
    public static boolean showBackground = false;
    public static int textColor = 0xFFFF7654;
    public static int backgroundColor = 0xCC171A20;

    public FpsModule() { super("FPS"); }

    @Override
    public void render(RenderGameOverlayEvent.Text event) {
        if (!showFps) return;
        Minecraft mc = Minecraft.getMinecraft();
        String text = "FPS: " + Minecraft.getDebugFPS();
        int x = 4;
        int y = 4;
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        if (showBackground) {
            mc.fontRendererObj.drawStringWithShadow(text, x, y, textColor);
            net.minecraft.client.gui.Gui.drawRect(x - 2, y - 2, x + textWidth + 2, y + 10, backgroundColor);
            mc.fontRendererObj.drawStringWithShadow(text, x, y, textColor);
        } else {
            mc.fontRendererObj.drawStringWithShadow(text, x, y, textColor);
        }
    }
}
