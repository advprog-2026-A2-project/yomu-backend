package id.ac.ui.cs.advprog.yomubackend.clan.enums;

public enum TierType {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    public boolean hasHigherTier() {
        return ordinal() < values().length - 1;
    }

    public boolean hasLowerTier() {
        return ordinal() > 0;
    }

    public TierType getHigherTier() {
        if (!hasHigherTier()) {
            return this;
        }
        return values()[ordinal() + 1];
    }

    public TierType getLowerTier() {
        if (!hasLowerTier()) {
            return this;
        }
        return values()[ordinal() - 1];
    }
}
