package br.com.firedclient.module;

/** Catalog entry for a feature that is planned but not implemented yet. */
public class CatalogModule extends ClientModule {
    private final String category;

    public CatalogModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() { return category; }
    public boolean isImplemented() { return false; }
}
