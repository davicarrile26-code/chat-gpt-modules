package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class ClickGui extends GuiScreen {
    private int panelLeft;
    private int panelTop;
    private int scroll;

    @Override
    public void initGui() {
        panelLeft = width / 2 - 145;
        panelTop = height / 2 - 112;
        scroll = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int max = Math.max(0, FiredClient.MODULES.getModules().size() - 9);
            scroll = Math.max(0, Math.min(max, scroll + (wheel > 0 ? -2 : 2)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int right = panelLeft + 290;
        int bottom = panelTop + 224;
        drawRect(panelLeft, panelTop, right, bottom, 0xF0121010);
        drawGradientRect(panelLeft, panelTop, right, panelTop + 3, 0xFFFF3B16, 0xFFFFA21A);
        drawRect(panelLeft, panelTop + 3, right, panelTop + 31, 0xFF211411);
        drawString(fontRendererObj, "FIRED CLIENT", panelLeft + 12, panelTop + 11, 0xFFFF8A24);
        drawString(fontRendererObj, "ALPHA 0.2  |  66 MODULES", panelLeft + 157, panelTop + 12, 0xFFFFC18A);
        drawRect(panelLeft + 8, panelTop + 36, right - 8, bottom - 17, 0xB8201714);

        List<ClientModule> modules = FiredClient.MODULES.getModules();
        int visible = 9;
        for (int row = 0; row < visible; row++) {
            int index = scroll + row;
            if (index >= modules.size()) break;
            ClientModule module = modules.get(index);
            int y = panelTop + 42 + row * 18;
            boolean hovered = mouseX >= panelLeft + 10 && mouseX <= right - 10
                    && mouseY >= y - 3 && mouseY < y + 14;
            if (hovered) drawRect(panelLeft + 10, y - 4, right - 10, y + 14, 0x663D2118);
            String category = module instanceof CatalogModule ? ((CatalogModule) module).getCategory() : "HUD";
            drawString(fontRendererObj, category.toUpperCase(), panelLeft + 16, y, 0xFFFF9B42);
            drawString(fontRendererObj, module.getName(), panelLeft + 94, y, 0xFFF5E9DF);
            if (module instanceof CatalogModule) {
                drawString(fontRendererObj, "EM BREVE", right - 67, y, 0xFFB98770);
            } else {
                int color = module.isEnabled() ? 0xFFFF7A1A : 0xFF9B8A80;
                drawString(fontRendererObj, module.isEnabled() ? "ON" : "OFF", right - 42, y, color);
            }
        }
        drawString(fontRendererObj, "RShift fecha/abre  |  Role a lista para navegar", panelLeft + 12, bottom - 12, 0xFFB99A87);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && mouseX >= panelLeft + 10 && mouseX <= panelLeft + 280
                && mouseY >= panelTop + 39 && mouseY < panelTop + 204) {
            int row = (mouseY - (panelTop + 39)) / 18;
            int index = scroll + row;
            List<ClientModule> modules = FiredClient.MODULES.getModules();
            if (row >= 0 && row < 9 && index >= 0 && index < modules.size()
                    && !(modules.get(index) instanceof CatalogModule)) {
                modules.get(index).toggle();
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
