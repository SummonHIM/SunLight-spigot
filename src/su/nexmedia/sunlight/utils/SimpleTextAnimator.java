package su.nexmedia.sunlight.utils;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

import java.util.List;

public class SimpleTextAnimator {

    private final String   id;
    private final String[] lines;
    private final int      interval;
    private final String   placeholder;

    public SimpleTextAnimator(@NotNull String id, @NotNull List<String> messages, int interval) {
        this.id = id.toLowerCase();
        this.lines = StringUT.color(messages).toArray(new String[messages.size()]);
        this.interval = interval;
        this.placeholder = "%animation:" + getId() + "%";
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getMessage() {
        return this.lines[(int) (System.currentTimeMillis() % (this.lines.length * this.interval) / this.interval)];
    }

    @NotNull
    public final String getPlaceholder() {
        return placeholder;
    }

    @NotNull
    public final String replace(@NotNull String text) {
        return text.replace(this.getPlaceholder(), this.getMessage());
    }
}
