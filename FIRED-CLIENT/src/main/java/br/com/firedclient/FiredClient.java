package br.com.firedclient;

import br.com.firedclient.module.ModuleManager;
import br.com.firedclient.ui.ClickGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = FiredClient.MODID, name = FiredClient.NAME, version = FiredClient.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class FiredClient {
    public static final String MODID = "firedclient";
    public static final String NAME = "FIRED CLIENT";
    public static final String VERSION = "1.0.0";

    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding guiKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MODULES.registerDefaults();
        guiKey = new KeyBinding("key.firedclient.gui", Keyboard.KEY_RSHIFT, "key.categories.firedclient");
        ClientRegistry.registerKeyBinding(guiKey);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(MODULES);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (guiKey.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new ClickGui());
        }
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Text event) {
        MODULES.renderHud(event);
    }
}
