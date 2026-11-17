package br.com.firedclient.module;

import br.com.firedclient.module.impl.FpsModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private final List<ClientModule> modules = new ArrayList<ClientModule>();

    public void registerDefaults() {
        register(new FpsModule());
    }

    public void register(ClientModule module) { modules.add(module); }
    public List<ClientModule> getModules() { return Collections.unmodifiableList(modules); }

    public ClientModule getByName(String name) {
        for (ClientModule module : modules)
            if (module.getName().equalsIgnoreCase(name)) return module;
        return null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (ClientModule module : modules)
            if (module.isEnabled() && module instanceof TickModule)
                ((TickModule) module).onTick();
    }

    public void renderHud(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        for (ClientModule module : modules)
            if (module.isEnabled() && module instanceof HudModule)
                ((HudModule) module).render(event);
    }
}
