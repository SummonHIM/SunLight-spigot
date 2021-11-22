package su.nexmedia.sunlight.modules.tab;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

public class TabListName {

    private final int    priority;
    private final String format;

    public TabListName(int priority, @NotNull String format) {
        this.priority = priority;
        this.format = StringUT.color(format);
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getFormat() {
        return format;
    }
}
