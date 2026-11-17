package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.module.ClientModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class ClickGui extends GuiScreen {
    private int panelLeft;
    private int panelTop;

    @Override
    public void initGui() {
        panelLeft = width / 2 - 110;
        panelTop = height / 2 - 90;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(panelLeft, panelTop, panelLeft + 220, panelTop + 180, 0xE8101724);
        drawRect(panelLeft, panelTop, panelLeft + 220, panelTop + 25, 0xFF16BCEB);
        drawCenteredString(fontRendererObj, "FIRED CLIENT", width / 2, panelTop + 8, 0xFFFFFF);
        int y = panelTop + 36;
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            int color = module.isEnabled() ? 0xFF55DDFF : 0xFFB8C2D1;
            drawString(fontRendererObj, module.getName(), panelLeft + 12, y, color);
            drawString(fontRendererObj, module.isEnabled() ? "ON" : "OFF", panelLeft + 180, y, color);
            y += 20;
        }
        drawCenteredString(fontRendererObj, "Clique no módulo para ativar/desativar", width / 2, panelTop + 158, 0xFFB8C2D1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && mouseX >= panelLeft && mouseX <= panelLeft + 220
                && mouseY >= panelTop + 36
                && mouseY < panelTop + 36 + FiredClient.MODULES.getModules().size() * 20) {
            int index = (mouseY - (panelTop + 36)) / 20;
            if (index >= 0 && index < FiredClient.MODULES.getModules().size())
                FiredClient.MODULES.getModules().get(index).toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
