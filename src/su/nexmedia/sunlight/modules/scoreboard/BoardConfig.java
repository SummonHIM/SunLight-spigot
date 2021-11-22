package su.nexmedia.sunlight.modules.scoreboard;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardConfig {

    private final String       id;
    private final int          updateInterval;
    private final int          priority;
    private final Set<String>  worlds;
    private final Set<String>  groups;
    private final String       title;
    private final List<String> lines;

    public BoardConfig(@NotNull String id, int updateInterval, int priority,
                       @NotNull Set<String> worlds, @NotNull Set<String> groups,
                       @NotNull String title, @NotNull List<String> lines) {
        this.id = id.toLowerCase();
        this.updateInterval = Math.max(1, updateInterval);
        this.priority = priority;
        this.worlds = worlds;
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.title = StringUT.color(title);
        this.lines = StringUT.color(lines);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public Set<String> getWorlds() {
        return worlds;
    }

    @NotNull
    public Set<String> getGroups() {
        return groups;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getLines() {
        return lines;
    }
}
