package me.pigalala.pigstops.pit.management;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;

public enum Modifications {
    RANDOMISE_ON_START("a", "Randomise_Items_on_Start"),
    RANDOMISE_ON_FAIL("b", "Randomise_Items_on_Fail");

    private final String id;
    private final String displayName;

    Modifications(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Modifications ofId(String id) {
        for(Modifications mod : Modifications.values()) {
            if(mod.getId() == id) return mod;
        }

        return null;
    }

    public static Modifications ofDisplayName(String displayName) {
        for(Modifications mod : Modifications.values()) {
            if(mod.getDisplayName().equals(displayName)) return mod;
        }
        return null;
    }

    public static ContextResolver<Modifications, BukkitCommandExecutionContext> getModificationsContextResolver() {
        return c -> {
            try {
                return Modifications.ofDisplayName(c.popFirstArg());
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument();
            }
        };
    }
}
