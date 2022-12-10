package me.pigalala.pigstops.pit.management;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;

public enum Modifications {
    RANDOMISE_ON_START("a"),
    RANDOMISE_ON_FAIL("b");

    private final String id;

    Modifications(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static Modifications of(String id) {
        for(Modifications mod : Modifications.values()) {
            if(mod.getId() == id) return mod;
        }

        return null;
    }

    public static ContextResolver<Modifications, BukkitCommandExecutionContext> getModificationsContextResolver() {
        return c -> {
            try {
                return Modifications.valueOf(c.popFirstArg().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument();
            }
        };
    }
}
