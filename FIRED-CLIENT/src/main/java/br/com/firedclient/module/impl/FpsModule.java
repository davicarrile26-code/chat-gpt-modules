package br.com.firedclient.module.impl;

import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class FpsModule extends ClientModule implements HudModule {
    public FpsModule() { super("FPS"); }

    @Override
    public void render(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.fontRendererObj.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), 4, 4, 0x55DDFF);
    }
}
