package br.com.firedclient.module;

public abstract class ClientModule {
    private final String name;
    private boolean enabled;

    protected ClientModule(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }
    protected void onEnable() {}
    protected void onDisable() {}
}
