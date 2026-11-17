package br.com.firedclient.module;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface HudModule {
    void render(RenderGameOverlayEvent.Text event);
}
