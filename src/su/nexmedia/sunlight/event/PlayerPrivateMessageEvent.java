package su.nexmedia.sunlight.event;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.event.ICancellableEvent;

public class PlayerPrivateMessageEvent extends ICancellableEvent {

    private final CommandSender sender;
    private final CommandSender target;
    private final String        message;

    public PlayerPrivateMessageEvent(
        @NotNull CommandSender sender,
        @NotNull CommandSender target,
        @NotNull String message
    ) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    @NotNull
    public CommandSender getSender() {
        return this.sender;
    }

    @NotNull
    public CommandSender getTarget() {
        return this.target;
    }

    @NotNull
    public String getMessage() {
        return this.message;
    }
}
