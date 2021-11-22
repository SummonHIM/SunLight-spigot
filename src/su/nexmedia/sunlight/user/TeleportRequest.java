package su.nexmedia.sunlight.user;

import org.jetbrains.annotations.NotNull;

public class TeleportRequest {

    private final String  target;
    private final String  sender;
    private final boolean isSummon;
    private       long    expireDate;

    public TeleportRequest(@NotNull String sender, @NotNull String target, boolean isSummon, int expire) {
        this.sender = sender;
        this.target = target;
        this.isSummon = isSummon;
        this.expireDate = System.currentTimeMillis() + expire * 1000L;
    }

    @NotNull
    public String getSender() {
        return this.sender;
    }

    @NotNull
    public String getTarget() {
        return this.target;
    }

    public boolean isSummon() {
        return this.isSummon;
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    public boolean isExpired() {
        return this.expireDate < System.currentTimeMillis();
    }

    public void expire() {
        this.expireDate = System.currentTimeMillis();
    }
}
