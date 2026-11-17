package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.impl.FpsModule;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FIRED CLIENT's rebuilt 1.8.9 ClickGUI.
 * Responsive Lunar-inspired layout with original FIRED branding and colors.
 */
public class ClickGui extends GuiScreen {
    private static final int TAB_MODS = 0;
    private static final int TAB_COSMETICS = 1;
    private static final int TAB_SETTINGS = 2;

    private static final int ORANGE = 0xFFFF7433;
    private static final int RED = 0xFFE43B27;
    private static final int TEXT = 0xFFF4F0ED;
    private static final int MUTED = 0xFF9E9692;
    private static final int SURFACE = 0xF0181719;
    private static final int CARD = 0xFF242225;

    private int tab = TAB_MODS;
    private int left;
    private int top;
    private int guiWidth;
    private int guiHeight;
    private int scrollRow;
    private int selectedCosmetic;
    private ClientModule settingsModule;
    private GuiTextField searchField;

    @Override
    public void initGui() {
        guiWidth = Math.max(300, Math.min(560, width - 20));
        guiHeight = Math.max(220, Math.min(360, height - 20));
        left = (width - guiWidth) / 2;
        top = (height - guiHeight) / 2;
        searchField = new GuiTextField(10, fontRendererObj, left + 16, top + 72,
                Math.min(190, guiWidth / 2 - 24), 18);
        searchField.setMaxStringLength(40);
        searchField.setEnableBackgroundDrawing(true);
        searchField.setTextColor(0xFFECE7E3);
        searchField.setDisabledTextColour(MUTED);
        scrollRow = 0;
    }

    private int right() { return left + guiWidth; }
    private int bottom() { return top + guiHeight; }

    private List<ClientModule> filteredModules() {
        List<ClientModule> result = new ArrayList<ClientModule>();
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            String name = module.getName().toLowerCase(Locale.ROOT);
            String category = module instanceof CatalogModule
                    ? ((CatalogModule) module).getCategory().toLowerCase(Locale.ROOT) : "hud";
            if (query.length() == 0 || name.contains(query) || category.contains(query)) result.add(module);
        }
        return result;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && tab == TAB_MODS && settingsModule == null) {
            int columns = 2;
            int visibleRows = Math.max(1, (guiHeight - 132) / 54);
            int totalRows = (filteredModules().size() + columns - 1) / columns;
            int max = Math.max(0, totalRows - visibleRows);
            scrollRow = Math.max(0, Math.min(max, scrollRow + (wheel > 0 ? -1 : 1)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(left, top, right(), bottom(), SURFACE);
        drawGradientRect(left, top, right(), top + 3, RED, ORANGE);
        drawHeader();
        drawNavigation();

        if (tab == TAB_MODS) drawMods(mouseX, mouseY);
        else if (tab == TAB_COSMETICS) drawCosmetics(mouseX, mouseY);
        else drawClientSettings();

        if (settingsModule != null) drawModuleSettings();
        drawString(fontRendererObj, "FIRED CLIENT  •  MINECRAFT 1.8.9",
                left + 16, bottom() - 13, MUTED);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawHeader() {
        drawRect(left, top + 3, right(), top + 58, 0xFF1D1B1E);
        // Original FIRED flame mark, drawn from simple geometric shapes.
        drawRect(left + 16, top + 14, left + 48, top + 46, 0xFF3A1D1B);
        drawGradientRect(left + 23, top + 19, left + 40, top + 42, RED, ORANGE);
        drawRect(left + 28, top + 29, left + 35, top + 40, 0xFFFFC078);
        drawString(fontRendererObj, "FIRED CLIENT", left + 58, top + 17, 0xFFFF8A52);
        drawString(fontRendererObj, "YOUR GAME. YOUR STYLE.", left + 58, top + 32, MUTED);
        drawString(fontRendererObj, "1.8.9", right() - 48, top + 24, 0xFFC7BDB8);
    }

    private void drawNavigation() {
        int y = top + 58;
        drawRect(left, y, right(), y + 34, 0xFF211F22);
        int x = left + 14;
        drawTab(x, y + 5, 76, "MODS", tab == TAB_MODS);
        drawTab(x + 82, y + 5, 108, "COSMETICS", tab == TAB_COSMETICS);
        drawTab(x + 196, y + 5, 96, "SETTINGS", tab == TAB_SETTINGS);
    }

    private void drawTab(int x, int y, int w, String label, boolean active) {
        if (active) {
            drawRect(x, y, x + w, y + 24, 0xFF48231F);
            drawRect(x, y + 21, x + w, y + 24, ORANGE);
        }
        drawCenteredString(fontRendererObj, label, x + w / 2, y + 8,
                active ? 0xFFFFB18B : 0xFFB8AFAB);
    }

    private void drawMods(int mouseX, int mouseY) {
        searchField.drawTextBox();
        drawString(fontRendererObj, "BROWSE MODS", left + 16, top + 99, TEXT);
        List<ClientModule> modules = filteredModules();
        String count = modules.size() + " ITEMS";
        drawString(fontRendererObj, count, right() - fontRendererObj.getStringWidth(count) - 16,
                top + 100, MUTED);

        int contentTop = top + 116;
        int contentBottom = bottom() - 25;
        int gap = 8;
        int cardWidth = (guiWidth - 40) / 2;
        int cardHeight = 46;
        int visibleRows = Math.max(1, (contentBottom - contentTop + gap) / (cardHeight + gap));
        int start = scrollRow * 2;

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < 2; col++) {
                int index = start + row * 2 + col;
                if (index >= modules.size()) continue;
                ClientModule module = modules.get(index);
                int x = left + 16 + col * (cardWidth + gap);
                int y = contentTop + row * (cardHeight + gap);
                boolean hovered = inside(mouseX, mouseY, x, y, x + cardWidth, y + cardHeight);
                drawRect(x, y, x + cardWidth, y + cardHeight,
                        hovered ? 0xFF30292B : CARD);
                drawRect(x, y, x + 3, y + cardHeight,
                        module.isEnabled() ? ORANGE : 0xFF51494A);

                String category = module instanceof CatalogModule
                        ? ((CatalogModule) module).getCategory() : "HUD";
                drawString(fontRendererObj, module.getName(), x + 11, y + 8, TEXT);
                drawString(fontRendererObj, category.toUpperCase(Locale.ROOT), x + 11, y + 25, MUTED);

                if (module instanceof CatalogModule) {
                    drawString(fontRendererObj, "SOON", x + cardWidth - 49, y + 9, 0xFFB9A39A);
                } else {
                    drawRect(x + cardWidth - 48, y + 8, x + cardWidth - 10, y + 26,
                            module.isEnabled() ? 0xFF743322 : 0xFF454044);
                    drawCenteredString(fontRendererObj, module.isEnabled() ? "ON" : "OFF",
                            x + cardWidth - 29, y + 14,
                            module.isEnabled() ? 0xFFFFB18B : 0xFFB8AFAB);
                }
                drawString(fontRendererObj, "⚙", x + cardWidth - 17, y + 29, 0xFFFFA06A);
            }
        }

        if (modules.isEmpty()) {
            drawCenteredString(fontRendererObj, "NO MODS FOUND", left + guiWidth / 2,
                    contentTop + 24, MUTED);
        }
    }

    private void drawCosmetics(int mouseX, int mouseY) {
        drawString(fontRendererObj, "MAKE IT YOURS", left + 16, top + 75, TEXT);
        drawString(fontRendererObj, "Choose a cosmetic collection", left + 16, top + 89, MUTED);
        String[] sections = {"CAPES", "HAND", "TRAILS", "AURAS", "HATS", "WINGS"};
        int cardWidth = (guiWidth - 48) / 3;
        for (int i = 0; i < sections.length; i++) {
            int x = left + 16 + (i % 3) * (cardWidth + 8);
            int y = top + 112 + (i / 3) * 58;
            boolean active = selectedCosmetic == i;
            boolean hovered = inside(mouseX, mouseY, x, y, x + cardWidth, y + 48);
            drawRect(x, y, x + cardWidth, y + 48,
                    active ? 0xFF48231F : (hovered ? 0xFF30292B : CARD));
            if (active) drawRect(x, y + 45, x + cardWidth, y + 48, ORANGE);
            drawCenteredString(fontRendererObj, sections[i], x + cardWidth / 2, y + 17,
                    active ? 0xFFFFB18B : TEXT);
            drawCenteredString(fontRendererObj, "COLLECTION", x + cardWidth / 2, y + 30, MUTED);
        }
        drawString(fontRendererObj, "Cosmetic assets will appear here when added to the project.",
                left + 16, bottom() - 42, MUTED);
    }

    private void drawClientSettings() {
        drawString(fontRendererObj, "CLIENT APPEARANCE", left + 16, top + 78, TEXT);
        drawString(fontRendererObj, "FIRED red-orange theme", left + 16, top + 99, 0xFFFFA06A);
        drawString(fontRendererObj, "Window scaling adapts to your Minecraft resolution.",
                left + 16, top + 119, MUTED);
        drawString(fontRendererObj, "More RGB and background controls can be connected here.",
                left + 16, top + 135, MUTED);
    }

    private void drawModuleSettings() {
        int w = Math.min(220, guiWidth - 32);
        int x = right() - w - 12;
        int y = top + 104;
        drawRect(x, y, x + w, y + 142, 0xFF171518);
        drawRect(x, y, x + w, y + 3, ORANGE);
        drawString(fontRendererObj, settingsModule.getName().toUpperCase(Locale.ROOT),
                x + 10, y + 13, 0xFFFFA06A);
        if (settingsModule instanceof FpsModule) {
            drawSettingLine(x + 10, y + 40, "Show FPS", FpsModule.showFps);
            drawSettingLine(x + 10, y + 62, "Background", FpsModule.showBackground);
            drawString(fontRendererObj, "Click an option to toggle it", x + 10, y + 91, MUTED);
        } else {
            drawString(fontRendererObj, "No settings exposed yet", x + 10, y + 45, MUTED);
        }
        drawString(fontRendererObj, "CLOSE  X", x + 10, y + 119, 0xFFFFA06A);
    }

    private void drawSettingLine(int x, int y, String label, boolean enabled) {
        drawString(fontRendererObj, label, x, y, TEXT);
        drawString(fontRendererObj, enabled ? "ON" : "OFF", x + 145, y,
                enabled ? 0xFFFFA06A : MUTED);
    }

    private boolean inside(int mx, int my, int x1, int y1, int x2, int y2) {
        return mx >= x1 && mx <= x2 && my >= y1 && my <= y2;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            int navY = top + 63;
            if (inside(mouseX, mouseY, left + 14, navY, left + 90, navY + 24)) {
                tab = TAB_MODS;
                settingsModule = null;
            } else if (inside(mouseX, mouseY, left + 96, navY, left + 204, navY + 24)) {
                tab = TAB_COSMETICS;
                settingsModule = null;
            } else if (inside(mouseX, mouseY, left + 210, navY, left + 306, navY + 24)) {
                tab = TAB_SETTINGS;
                settingsModule = null;
            } else if (tab == TAB_MODS && settingsModule != null) {
                int w = Math.min(220, guiWidth - 32);
                int x = right() - w - 12;
                int y = top + 104;
                if (inside(mouseX, mouseY, x + 5, y + 108, x + w - 5, y + 136)) {
                    settingsModule = null;
                } else if (settingsModule instanceof FpsModule) {
                    if (inside(mouseX, mouseY, x + 5, y + 32, x + w - 5, y + 56))
                        FpsModule.showFps = !FpsModule.showFps;
                    else if (inside(mouseX, mouseY, x + 5, y + 55, x + w - 5, y + 79))
                        FpsModule.showBackground = !FpsModule.showBackground;
                }
            } else if (tab == TAB_MODS) {
                List<ClientModule> modules = filteredModules();
                int contentTop = top + 116;
                int cardWidth = (guiWidth - 40) / 2;
                int cardHeight = 46;
                int gap = 8;
                int visibleRows = Math.max(1, (bottom() - 25 - contentTop + gap) / (cardHeight + gap));
                for (int row = 0; row < visibleRows; row++) {
                    for (int col = 0; col < 2; col++) {
                        int index = scrollRow * 2 + row * 2 + col;
                        if (index >= modules.size()) continue;
                        int x = left + 16 + col * (cardWidth + gap);
                        int y = contentTop + row * (cardHeight + gap);
                        if (!inside(mouseX, mouseY, x, y, x + cardWidth, y + cardHeight)) continue;
                        ClientModule module = modules.get(index);
                        if (inside(mouseX, mouseY, x + cardWidth - 25, y + 25, x + cardWidth, y + 46)) {
                            settingsModule = module;
                        } else if (!(module instanceof CatalogModule)) {
                            module.toggle();
                        }
                        return;
                    }
                }
            } else if (tab == TAB_COSMETICS) {
                String[] sections = {"CAPES", "HAND", "TRAILS", "AURAS", "HATS", "WINGS"};
                int cardWidth = (guiWidth - 48) / 3;
                for (int i = 0; i < sections.length; i++) {
                    int x = left + 16 + (i % 3) * (cardWidth + 8);
                    int y = top + 112 + (i / 3) * 58;
                    if (inside(mouseX, mouseY, x, y, x + cardWidth, y + 48)) selectedCosmetic = i;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField != null && tab == TAB_MODS && settingsModule == null
                && searchField.textboxKeyTyped(typedChar, keyCode)) {
            scrollRow = 0;
            return;
        }
        if (keyCode == 1) {
            if (settingsModule != null) settingsModule = null;
            else mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
