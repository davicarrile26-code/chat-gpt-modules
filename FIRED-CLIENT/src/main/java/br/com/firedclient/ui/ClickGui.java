package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.compat.OptiFineCompatibility;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.impl.FpsModule;
import br.com.firedclient.module.impl.FeatureModule;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FIRED ClickGUI - a clean, Lunar-inspired client dashboard with original FIRED styling.
 * All coordinates are authored in a virtual 720x430 canvas and scaled to fit the window.
 */
public class ClickGui extends GuiScreen {
    private static final int TAB_MODS = 0, TAB_COSMETICS = 1;
    private static final int BASE_W = 720, BASE_H = 430;
    private static final int SIDEBAR_W = 142;
    private static final int CARD_W = 264, CARD_H = 76, CARD_GAP_X = 12, CARD_GAP_Y = 10;
    private static final int FIRST_CARD_Y = 112, VISIBLE_ROWS = 3;
    private static final String[] CATEGORIES = {"TODOS", "HUD", "RENDER", "COMBAT", "MOVEMENT", "WORLD", "MISC", "COSMETICS"};
    private static final String[] COSMETIC_SECTIONS = {"CAPES", "ASAS", "CHAPEUS", "TRAILS", "AURAS"};

    private int tab = TAB_MODS;
    private int scroll;
    private int selectedCosmeticSection;
    private float guiScale = 1.0F;
    private int guiOffsetX, guiOffsetY;
    private String selectedCategory = "TODOS";
    private ClientModule settingsModule;
    private GuiTextField searchField;

    @Override
    public void initGui() {
        guiScale = Math.min(1.0F, Math.min((width - 16.0F) / BASE_W, (height - 16.0F) / BASE_H));
        guiScale = Math.max(0.1F, guiScale);
        guiOffsetX = (int) ((width - BASE_W * guiScale) / 2.0F);
        guiOffsetY = (int) ((height - BASE_H * guiScale) / 2.0F);
        searchField = new GuiTextField(9001, fontRendererObj, 548, 78, 151, 20);
        searchField.setMaxStringLength(40);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setTextColor(0xFFF2E9E5);
        searchField.setFocused(false);
        scroll = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && tab == TAB_MODS && settingsModule == null) {
            int max = Math.max(0, (getFilteredModules().size() + 1) / 2 - VISIBLE_ROWS);
            scroll = Math.max(0, Math.min(max, scroll + (wheel > 0 ? -1 : 1)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiOffsetX, guiOffsetY, 0.0F);
        GlStateManager.scale(guiScale, guiScale, 1.0F);

        // Main window and header
        drawRect(0, 0, BASE_W, BASE_H, 0xF00D0F13);
        drawRect(0, 0, BASE_W, 3, 0xFFFF573D);
        drawRect(0, 3, BASE_W, 56, 0xFF171A20);
        drawRect(0, 56, BASE_W, BASE_H, 0xFF101318);
        drawRect(SIDEBAR_W, 56, SIDEBAR_W + 1, BASE_H, 0xFF282D35);
        drawFireMark(18, 15);
        drawString(fontRendererObj, "FIRED", 54, 17, 0xFFFF6848);
        drawString(fontRendererObj, "CLIENT", 54, 29, 0xFFF0E8E4);
        drawString(fontRendererObj, "MINECRAFT 1.8.9", 105, 22, 0xFF858D98);

        drawTopTab(284, "MODS", tab == TAB_MODS);
        drawTopTab(374, "COSMETICOS", tab == TAB_COSMETICS);
        drawString(fontRendererObj, "RSHIFT", 650, 24, 0xFF737B86);
        drawString(fontRendererObj, "FECHAR", 690, 24, 0xFFB1B7C0);

        if (tab == TAB_MODS) drawMods(mouseX, mouseY);
        else drawCosmetics(mouseX, mouseY);

        drawString(fontRendererObj, "FIRED CLIENT  •  PERSONALIZE SUA EXPERIENCIA",
                16, BASE_H - 17, 0xFF626B76);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawFireMark(int x, int y) {
        drawRect(x + 8, y, x + 17, y + 5, 0xFFFFB04D);
        drawRect(x + 5, y + 5, x + 20, y + 10, 0xFFFF783B);
        drawRect(x + 2, y + 10, x + 23, y + 21, 0xFFE63738);
        drawRect(x + 6, y + 21, x + 19, y + 27, 0xFFB91F31);
        drawRect(x + 9, y + 8, x + 15, y + 17, 0xFFFFC06A);
        drawRect(x + 10, y + 15, x + 16, y + 23, 0xFFFF873D);
    }

    private void drawTopTab(int x, String label, boolean active) {
        int color = active ? 0xFFFF7654 : 0xFF9AA2AD;
        drawString(fontRendererObj, label, x, 25, color);
        if (active) drawRect(x - 8, 52, x + fontRendererObj.getStringWidth(label) + 8, 55, 0xFFFF573D);
    }

    private void drawMods(int mouseX, int mouseY) {
        drawSidebar(mouseX, mouseY);
        drawString(fontRendererObj, "MODS", 164, 72, 0xFFF3F4F6);
        drawString(fontRendererObj, "Gerencie e personalize seus mods", 164, 86, 0xFF7F8995);

        // Search field
        drawRect(540, 72, 704, 101, 0xFF20252D);
        drawRect(540, 72, 704, 73, 0xFF343B46);
        drawString(fontRendererObj, "⌕", 550, 80, 0xFFFF7654);
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused())
            drawString(fontRendererObj, "Pesquisar mods...", 568, 81, 0xFF77818D);

        List<ClientModule> modules = getFilteredModules();
        int rows = (modules.size() + 1) / 2;
        int maxScroll = Math.max(0, rows - VISIBLE_ROWS);
        if (scroll > maxScroll) scroll = maxScroll;

        if (settingsModule != null) {
            drawRect(164, 108, 704, 394, 0xFF171C23);
            drawRect(164, 108, 704, 110, 0xFFFF573D);
            drawString(fontRendererObj, "CONFIGURACOES DO MOD", 184, 128, 0xFFFF7654);
            drawString(fontRendererObj, settingsModule.getName(), 184, 151, 0xFFF1F2F4);
            String moduleCategory = getCategory(settingsModule);
            drawString(fontRendererObj, settingsModule instanceof CatalogModule
                    ? "RECURSO PLANEJADO  /  " + moduleCategory
                    : "CONFIGURACOES  /  " + moduleCategory, 184, 166, 0xFF89939F);
            if (!(settingsModule instanceof CatalogModule)) {
                drawSettingLine(184, 190, "Ativar mod", settingsModule.isEnabled());
            }
            if (settingsModule instanceof FpsModule) {
                drawSettingLine(184, 230, "Mostrar FPS", FpsModule.showFps);
                drawSettingLine(184, 264, "Mostrar fundo", FpsModule.showBackground);
                drawString(fontRendererObj, "As alteracoes sao aplicadas imediatamente.", 184, 310, 0xFF89939F);
            } else if (settingsModule instanceof FeatureModule
                    && "Keystrokes".equals(settingsModule.getName())) {
                FeatureModule keys = (FeatureModule) settingsModule;
                drawSettingLine(184, 230, "Mostrar WASD", keys.isKeystrokesShowMovement());
                drawSettingLine(184, 264, "Mostrar SPACE", keys.isKeystrokesShowSpace());
                drawSettingLine(184, 298, "Mostrar botoes do mouse", keys.isKeystrokesShowMouse());
                drawString(fontRendererObj, "As mudancas aparecem no HUD imediatamente.", 184, 330, 0xFF89939F);
            } else if (settingsModule instanceof FeatureModule
                    && "Nametags".equals(settingsModule.getName())) {
                FeatureModule nametags = (FeatureModule) settingsModule;
                drawSettingLine(184, 230, "Mostrar icone FIRED no meu nick", nametags.isNametagsShowFiredIcon());
                drawString(fontRendererObj, "O icone tem tamanho fixo. Para reconhecer outros", 184, 270, 0xFF89939F);
                drawString(fontRendererObj, "clientes FIRED, o servidor precisa retransmitir o sinal.", 184, 286, 0xFF89939F);
            } else if (settingsModule instanceof CatalogModule) {
                drawString(fontRendererObj, "Este recurso ainda nao foi implementado.", 184, 207, 0xFFB7A6A0);
                drawString(fontRendererObj, "A pagina individual ja esta preparada.", 184, 224, 0xFF89939F);
            } else {
                drawString(fontRendererObj, "Este mod esta disponivel para ativacao.", 184, 238, 0xFFE5E9ED);
                drawString(fontRendererObj, "Opcoes avancadas serao exibidas aqui", 184, 258, 0xFF89939F);
                drawString(fontRendererObj, "quando forem implementadas para este mod.", 184, 273, 0xFF89939F);
            }
            drawRect(184, 350, 284, 378, 0xFF3A2023);
            drawCenteredString(fontRendererObj, "VOLTAR", 234, 360, 0xFFFF9277);
            return;
        }

        if (modules.isEmpty()) {
            drawRect(164, 112, 704, 394, 0xFF171C23);
            drawCenteredString(fontRendererObj, "NENHUM MOD ENCONTRADO", 434, 236, 0xFF929BA6);
            return;
        }

        for (int i = 0; i < modules.size(); i++) {
            int row = i / 2 - scroll;
            if (row < 0 || row >= VISIBLE_ROWS) continue;
            int col = i % 2;
            int x = 164 + col * (CARD_W + CARD_GAP_X);
            int y = FIRST_CARD_Y + row * (CARD_H + CARD_GAP_Y);
            drawModuleCard(modules.get(i), x, y, mouseX, mouseY);
        }
        drawString(fontRendererObj, modules.size() + " MODS", 164, 391, 0xFF69737F);
        drawString(fontRendererObj, "ROLE PARA VER MAIS", 590, 391, 0xFF69737F);
    }

    private void drawSidebar(int mouseX, int mouseY) {
        drawRect(0, 56, SIDEBAR_W, BASE_H, 0xFF15191F);
        drawString(fontRendererObj, "BIBLIOTECA", 17, 75, 0xFF737D89);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = 91 + i * 34;
            boolean active = selectedCategory.equals(CATEGORIES[i]);
            boolean hovered = inside(mouseX, mouseY, 9, y - 5, SIDEBAR_W - 10, y + 23);
            if (active) {
                drawRect(8, y - 5, SIDEBAR_W - 10, y + 23, 0xFF382126);
                drawRect(8, y - 5, 11, y + 23, 0xFFFF573D);
            } else if (hovered) drawRect(8, y - 5, SIDEBAR_W - 10, y + 23, 0xFF222831);
            drawString(fontRendererObj, categoryLabel(CATEGORIES[i]), 20, y + 4,
                    active ? 0xFFFF9277 : 0xFFADB5BF);
            int count = countCategory(CATEGORIES[i]);
            String amount = String.valueOf(count);
            drawString(fontRendererObj, amount, 120 - fontRendererObj.getStringWidth(amount), y + 4,
                    active ? 0xFFFF9277 : 0xFF68727E);
        }
        drawRect(16, 369, 126, 370, 0xFF303640);
        drawString(fontRendererObj, "FIRED", 18, 380, 0xFFFF7654);
        drawString(fontRendererObj, "CLIENT", 55, 380, 0xFF87919D);
    }

    private String categoryLabel(String category) {
        if ("TODOS".equals(category)) return "Todos os mods";
        if ("HUD".equals(category)) return "HUD";
        if ("RENDER".equals(category)) return "Renderizacao";
        if ("COMBAT".equals(category)) return "Combate";
        if ("MOVEMENT".equals(category)) return "Movimento";
        if ("WORLD".equals(category)) return "Mundo";
        if ("MISC".equals(category)) return "Diversos";
        return "Cosmeticos";
    }

    private int countCategory(String category) {
        if ("TODOS".equals(category)) return FiredClient.MODULES.getModules().size();
        int count = 0;
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            if (getCategory(module).equals(category)) count++;
        }
        return count;
    }

    private void drawModuleCard(ClientModule module, int x, int y, int mouseX, int mouseY) {
        boolean hovered = inside(mouseX, mouseY, x, y, x + CARD_W, y + CARD_H);
        boolean enabled = module.isEnabled();
        int bg = hovered ? 0xFF242B34 : 0xFF1B2027;
        drawRect(x, y, x + CARD_W, y + CARD_H, bg);
        drawRect(x, y, x + CARD_W, y + 1, hovered ? 0xFF555F6D : 0xFF303741);
        drawRect(x, y, x + 3, y + CARD_H, enabled ? 0xFFFF573D : 0xFF343B45);
        drawRect(x + 13, y + 14, x + 45, y + 46, enabled ? 0xFF4A2527 : 0xFF292F38);
        drawCenteredString(fontRendererObj, module.getName().substring(0, 1).toUpperCase(),
                x + 29, y + 25, enabled ? 0xFFFFA17F : 0xFF9AA4B0);
        drawString(fontRendererObj, module.getName(), x + 55, y + 13, 0xFFF0F2F5);
        String description = module instanceof CatalogModule ? "Em desenvolvimento"
                : "Clique para " + (enabled ? "desativar" : "ativar");
        drawString(fontRendererObj, description, x + 55, y + 29, 0xFF828D99);
        if (module instanceof CatalogModule) {
            drawString(fontRendererObj, "BREVE", x + 55, y + 48, 0xFFB58A75);
        } else {
            drawToggle(x + CARD_W - 53, y + 14, enabled);
        }
        drawGear(x + CARD_W - 25, y + 48, 0xFF929BA6);
    }

    private void drawToggle(int x, int y, boolean enabled) {
        drawRect(x, y, x + 37, y + 19, enabled ? 0xFF8E302A : 0xFF343B45);
        drawRect(enabled ? x + 20 : x + 2, y + 2, enabled ? x + 35 : x + 17, y + 17,
                enabled ? 0xFFFFB18F : 0xFF9AA4AF);
    }

    private void drawGear(int x, int y, int color) {
        drawRect(x + 3, y, x + 7, y + 10, color);
        drawRect(x, y + 3, x + 10, y + 7, color);
        drawRect(x + 1, y + 1, x + 3, y + 3, color);
        drawRect(x + 7, y + 1, x + 9, y + 3, color);
        drawRect(x + 1, y + 7, x + 3, y + 9, color);
        drawRect(x + 7, y + 7, x + 9, y + 9, color);
        drawRect(x + 4, y + 4, x + 6, y + 6, 0xFF1B2027);
    }

    private void drawSettingLine(int x, int y, String label, boolean value) {
        drawString(fontRendererObj, label, x, y, 0xFFE5E9ED);
        drawToggle(x + 260, y - 4, value);
    }

    private void drawCosmetics(int mouseX, int mouseY) {
        drawString(fontRendererObj, "COSMETICOS", 164, 72, 0xFFF3F4F6);
        drawString(fontRendererObj, "Escolha efeitos visuais para seu personagem", 164, 86, 0xFF7F8995);
        for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
            int col = i % 3, row = i / 3, x = 164 + col * 178, y = 122 + row * 112;
            boolean active = selectedCosmeticSection == i;
            boolean hovered = inside(mouseX, mouseY, x, y, x + 164, y + 92);
            drawRect(x, y, x + 164, y + 92, active ? 0xFF382126 : hovered ? 0xFF242B34 : 0xFF1B2027);
            drawRect(x, y, x + 164, y + 2, active ? 0xFFFF573D : 0xFF303741);
            drawRect(x + 13, y + 15, x + 49, y + 51, 0xFF492329);
            drawCenteredString(fontRendererObj, String.valueOf(i + 1), x + 31, y + 28, 0xFFFF9B7B);
            drawString(fontRendererObj, COSMETIC_SECTIONS[i], x + 60, y + 17, 0xFFF0F2F5);
            ClientModule effect = i == 3 ? FiredClient.MODULES.getByName("Trails")
                    : i == 4 ? FiredClient.MODULES.getByName("Aura") : null;
            String status = i == 0 ? "3 CAPAS" : effect == null ? "COLECAO"
                    : effect.isEnabled() ? "ATIVO" : "DISPONIVEL";
            drawString(fontRendererObj, status, x + 60, y + 35,
                    effect != null && effect.isEnabled() ? 0xFFFF7654 : 0xFF89939F);
            drawString(fontRendererObj, "ABRIR", x + 13, y + 69, 0xFFFF9277);
        }
        if (selectedCosmeticSection == 0) {
            drawRect(164, 244, 698, 338, 0xFF171C23);
            drawString(fontRendererObj, "CAPAS DISPONIVEIS", 178, 253, 0xFFFF7654);
            String[] names = {"FIRED FLAME", "ROYAL EMBER", "INFERNO"};
            String[] ids = {"fired_flame", "royal_ember", "inferno_stripes"};
            for (int i = 0; i < names.length; i++) {
                int x = 178 + i * 171;
                boolean equipped = br.com.firedclient.cosmetic.CapeManager.getSelectedCape().equals(ids[i]);
                drawRect(x, 273, x + 158, 326, equipped ? 0xFF492329 : 0xFF242B34);
                drawRect(x, 273, x + 158, 275, equipped ? 0xFFFF573D : 0xFF3A424D);
                drawString(fontRendererObj, names[i], x + 9, 285, 0xFFF0F2F5);
                drawString(fontRendererObj, equipped ? "SELECIONADA" : "CLIQUE PARA EQUIPAR",
                        x + 9, 304, equipped ? 0xFFFF9277 : 0xFF9AA4AF);
            }
        }
        drawRect(164, 350, 698, 391, 0xFF1B2027);
        drawString(fontRendererObj, "STATUS DO CLIENT", 178, 359, 0xFFFF7654);
        drawString(fontRendererObj, "OptiFine: " + OptiFineCompatibility.getStatus(), 178, 375, 0xFF9AA4AF);
    }

    private List<ClientModule> getFilteredModules() {
        List<ClientModule> result = new ArrayList<ClientModule>();
        String query = searchField == null ? "" : searchField.getText().toLowerCase();
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            boolean categoryMatch = "TODOS".equals(selectedCategory) || getCategory(module).equals(selectedCategory);
            boolean searchMatch = module.getName().toLowerCase().contains(query);
            if (categoryMatch && searchMatch) result.add(module);
        }
        return result;
    }

    private String getCategory(ClientModule module) {
        String category = module instanceof CatalogModule ? ((CatalogModule) module).getCategory()
                : module instanceof FeatureModule ? ((FeatureModule) module).getCategory() : "HUD";
        return category.toUpperCase();
    }

    private boolean inside(int mx, int my, int left, int top, int right, int bottom) {
        return mx >= left && mx <= right && my >= top && my <= bottom;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField != null && searchField.isFocused()) {
            if (keyCode == 1) searchField.setFocused(false);
            else {
                searchField.textboxKeyTyped(typedChar, keyCode);
                scroll = 0;
            }
            return;
        }
        if (keyCode == 54 || keyCode == 56) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int mx = (int) ((mouseX - guiOffsetX) / guiScale);
        int my = (int) ((mouseY - guiOffsetY) / guiScale);
        if (searchField != null) searchField.mouseClicked(mx, my, mouseButton);
        if (mouseButton == 0) {
            if (inside(mx, my, 650, 12, 718, 48)) {
                mc.displayGuiScreen(null);
                return;
            }
            if (inside(mx, my, 270, 8, 355, 55)) {
                tab = TAB_MODS;
                settingsModule = null;
            } else if (inside(mx, my, 365, 8, 485, 55)) {
                tab = TAB_COSMETICS;
                settingsModule = null;
            } else if (tab == TAB_MODS) {
                if (settingsModule != null) {
                    if (inside(mx, my, 184, 350, 284, 378)) settingsModule = null;
                    else if (!(settingsModule instanceof CatalogModule)
                            && inside(mx, my, 180, 176, 470, 218)) settingsModule.toggle();
                    else if (settingsModule instanceof FpsModule) {
                        if (inside(mx, my, 180, 214, 470, 252)) FpsModule.showFps = !FpsModule.showFps;
                        else if (inside(mx, my, 180, 248, 470, 286))
                            FpsModule.showBackground = !FpsModule.showBackground;
                    } else if (settingsModule instanceof FeatureModule
                            && "Keystrokes".equals(settingsModule.getName())) {
                        FeatureModule keys = (FeatureModule) settingsModule;
                        if (inside(mx, my, 180, 214, 470, 252)) keys.toggleKeystrokesShowMovement();
                        else if (inside(mx, my, 180, 248, 470, 286)) keys.toggleKeystrokesShowSpace();
                        else if (inside(mx, my, 180, 282, 470, 320)) keys.toggleKeystrokesShowMouse();
                    } else if (settingsModule instanceof FeatureModule
                            && "Nametags".equals(settingsModule.getName())) {
                        FeatureModule nametags = (FeatureModule) settingsModule;
                        if (inside(mx, my, 180, 214, 470, 252)) nametags.toggleNametagsShowFiredIcon();
                    }
                } else {
                    for (int i = 0; i < CATEGORIES.length; i++) {
                        int y = 91 + i * 34;
                        if (inside(mx, my, 8, y - 5, SIDEBAR_W - 10, y + 23)) {
                            selectedCategory = CATEGORIES[i];
                            scroll = 0;
                            break;
                        }
                    }
                    List<ClientModule> modules = getFilteredModules();
                    for (int i = 0; i < modules.size(); i++) {
                        int row = i / 2 - scroll;
                        if (row < 0 || row >= VISIBLE_ROWS) continue;
                        int col = i % 2;
                        int x = 164 + col * (CARD_W + CARD_GAP_X);
                        int y = FIRST_CARD_Y + row * (CARD_H + CARD_GAP_Y);
                        ClientModule module = modules.get(i);
                        if (inside(mx, my, x + CARD_W - 32, y + 43, x + CARD_W - 5, y + 69)) {
                            settingsModule = module;
                            break;
                        }
                        if (!(module instanceof CatalogModule) && inside(mx, my, x, y, x + CARD_W - 40, y + CARD_H)) {
                            module.toggle();
                            break;
                        }
                    }
                }
            } else if (tab == TAB_COSMETICS) {
                boolean clickedSection = false;
                for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
                    int x = 164 + (i % 3) * 178, y = 122 + (i / 3) * 112;
                    if (inside(mx, my, x, y, x + 164, y + 92)) {
                        selectedCosmeticSection = i;
                        ClientModule effect = i == 3 ? FiredClient.MODULES.getByName("Trails")
                                : i == 4 ? FiredClient.MODULES.getByName("Aura") : null;
                        if (effect != null) effect.toggle();
                        clickedSection = true;
                        break;
                    }
                }
                if (!clickedSection && selectedCosmeticSection == 0 && inside(mx, my, 178, 273, 678, 326)) {
                    if (mx < 336) br.com.firedclient.cosmetic.CapeManager.selectCape("fired_flame");
                    else if (mx < 507) br.com.firedclient.cosmetic.CapeManager.selectCape("royal_ember");
                    else br.com.firedclient.cosmetic.CapeManager.selectCape("inferno_stripes");
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
