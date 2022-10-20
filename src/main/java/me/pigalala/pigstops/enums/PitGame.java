package me.pigalala.pigstops.enums;

public enum PitGame {
    STANDARD("§6Standard"),
    COOKIE("§6Cookie!!"),
    MARIANA("§dMariana"),
    ONFISHE("§bOnFishe"),
    GREASY("§cGreasy Gamers lmao");

    private final String name;

    PitGame(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
