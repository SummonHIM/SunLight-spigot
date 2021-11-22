package su.nexmedia.sunlight.modules.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

public class PunishmentReason {

    private final String id;
    private       String message;

    public PunishmentReason(@NotNull String id, @NotNull String message) {
        this.id = id.toLowerCase();
        this.setMessage(message);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = StringUT.color(message);
    }
}
