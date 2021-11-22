package su.nexmedia.sunlight.modules.bans;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

public enum BanTime {

    SECONDS("s", 1000L),
    MINUTES("min", 1000L * 60L),
    HOURS("h", 1000L * 60L * 60L),
    DAYS("d", 1000L * 60L * 60L * 24L),
    WEEKS("w", 1000L * 60L * 60L * 24L * 7L),
    MONTHS("mon", 1000L * 60L * 60L * 24L * 30L),
    YEARS("y", 1000L * 60L * 60L * 24L * 365L),
    ;

    private final long   modifier;
    private       String alias;

    BanTime(@NotNull String alias, long modifier) {
        this.setAlias(alias);
        this.modifier = modifier;
    }

    public static long parse(@NotNull String timeRaw) {
        long mod = 1000L; // Seconds
        long time = 0L;

        for (BanTime banTime : BanTime.values()) {
            String timeAlias = banTime.getAlias();
            if (timeRaw.endsWith(timeAlias)) {
                time = StringUT.getInteger(timeRaw.replace(timeAlias, ""), 0);
                mod = banTime.getModifier();
                break;
            }
        }
        return time <= 0L ? -1L : System.currentTimeMillis() + time * mod;
    }

    @NotNull
    public String getAlias() {
        return this.alias;
    }

    public void setAlias(@NotNull String alias) {
        this.alias = alias;
    }

    public long getModifier() {
        return this.modifier;
    }
}
