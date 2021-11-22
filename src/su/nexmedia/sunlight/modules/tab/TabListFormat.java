package su.nexmedia.sunlight.modules.tab;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

import java.util.Set;
import java.util.stream.Collectors;

public class TabListFormat {

    private final String      id;
    private final int         priority;
    private final Set<String> worlds;
    private final Set<String> groups;
    private final String      header;
    private final String      footer;

    public TabListFormat(@NotNull String id, int priority,
                         @NotNull Set<String> worlds, @NotNull Set<String> groups,
                         @NotNull String header, @NotNull String footer) {
        this.id = id.toLowerCase();
        this.priority = priority;
        this.worlds = worlds;
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.header = StringUT.color(header);
        this.footer = StringUT.color(footer);
    }

    @NotNull
    public String getId() {
        return id;
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
    public String getHeader() {
        return header;
    }

    @NotNull
    public String getFooter() {
        return footer;
    }
}
