package su.nexmedia.sunlight.modules.tab;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.StringUT;

public class TabNametag {

    private final String    teamId;
    private       int       priority;
    private       String    prefix;
    private       String    suffix;
    private       ChatColor color;

    public TabNametag(@NotNull String teamId) {
        this.teamId = teamId;
        this.color = ChatColor.WHITE;
    }

    @NotNull
    public String getTeamId() {
        return teamId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@NotNull String prefix) {
        this.prefix = StringUT.color(prefix);
    }

    @NotNull
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(@NotNull String suffix) {
        this.suffix = StringUT.color(suffix);
    }

    @NotNull
    public ChatColor getColor() {
        return color;
    }

    public void setColor(@Nullable ChatColor color) {
        if (color == null) color = ChatColor.WHITE;
        this.color = color;
    }
}
