package su.nexmedia.sunlight.event;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.event.ICancellableEvent;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.user.TeleportRequest;
import su.nexmedia.sunlight.data.SunUser;

public class PlayerTeleportRequestEvent extends ICancellableEvent {

    private final TeleportRequest request;

    public PlayerTeleportRequestEvent(@NotNull TeleportRequest request) {
        this.request = request;
    }

    @NotNull
    public TeleportRequest getRequest() {
        return this.request;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);

        SunUser user = SunLight.getInstance().getUserManager().getOrLoadUser(this.getRequest().getTarget(), false);
        if (user == null) return;

        if (this.isCancelled()) {
            user.getTeleportRequests().remove(this.getRequest());
        }
        else {
            user.addTeleportRequest(this.getRequest(), false);
        }
    }
}
