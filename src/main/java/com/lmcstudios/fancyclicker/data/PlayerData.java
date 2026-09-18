package com.lmcstudios.fancyclicker.data;

import java.util.UUID;

public class PlayerData {

    public enum Preference { LEFT, RIGHT }

    private final UUID uuid;
    private long cookies;
    private long totalCookies;
    private int level;
    private Preference preference;

    public PlayerData(UUID uuid, long cookies, long totalCookies, int level, Preference preference) {
        this.uuid = uuid;
        this.cookies = cookies;
        this.totalCookies = totalCookies;
        this.level = Math.max(1, level);
        this.preference = preference == null ? Preference.LEFT : preference;
    }

    public UUID getUuid() { return uuid; }

    public long getCookies() { return cookies; }
    public void setCookies(long cookies) { this.cookies = Math.max(0, cookies); }

    public long getTotalCookies() { return totalCookies; }

    public void addCookies(long amount) {
        if (amount <= 0) return;
        this.cookies += amount;
        this.totalCookies += amount;
    }

    public boolean removeCookies(long amount) {
        if (amount <= 0) return true;
        if (this.cookies < amount) return false;
        this.cookies -= amount;
        return true;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public void levelUp() { this.level++; }

    public Preference getPreference() { return preference; }
    public void setPreference(Preference preference) {
        this.preference = preference == null ? Preference.LEFT : preference;
    }
    public void togglePreference() {
        this.preference = (this.preference == Preference.LEFT) ? Preference.RIGHT : Preference.LEFT;
    }
}