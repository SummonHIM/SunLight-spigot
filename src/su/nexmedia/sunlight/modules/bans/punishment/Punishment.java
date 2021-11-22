package su.nexmedia.sunlight.modules.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.config.Config;
import su.nexmedia.sunlight.modules.bans.BanManager;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class Punishment implements IPlaceholder {

    public static final String PLACEHOLDER_TYPE         = "%punishment_type%";
    public static final String PLACEHOLDER_USER         = "%punishment_user%";
    public static final String PLACEHOLDER_REASON       = "%punishment_reason%";
    public static final String PLACEHOLDER_PUNISHER     = "%punishment_punisher%";
    public static final String PLACEHOLDER_DATE_CREATED = "%punishment_date_created%";
    public static final String PLACEHOLDER_DATE_EXPIRED = "%punishment_date_expired%";
    public static final String PLACEHOLDER_EXPIRE_IN    = "%punishment_expires_in%";
    private final UUID           id;
    private final PunishmentType type;
    private final String         user;
    private final String         reason;
    private final String         admin;
    private final long           createdDate;
    private final boolean        isIp;
    private       long           expireDate;

    public Punishment(@NotNull PunishmentType type, @NotNull String user, @NotNull String reason, @NotNull String admin, long expireDate) {
        this(UUID.randomUUID(), type, user, reason, admin, System.currentTimeMillis(), expireDate);
    }

    public Punishment(@NotNull UUID id, @NotNull PunishmentType type, @NotNull String user, @NotNull String reason, @NotNull String admin, long createdDate, long expireDate) {
        this.id = id;
        this.type = type;
        this.user = user.toLowerCase();
        this.reason = StringUT.color(reason);
        this.admin = admin;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.isIp = RegexUT.isIpAddress(this.getUser());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        BanManager banManager = SunLight.getInstance().getModuleCache().getBanManager();
        if (banManager == null) return str -> str;

        return str -> str
            .replace(PLACEHOLDER_TYPE, banManager.getLang().getEnum(this.getType()))
            .replace(PLACEHOLDER_USER, this.getUser())
            .replace(PLACEHOLDER_REASON, this.getReason())
            .replace(PLACEHOLDER_PUNISHER, this.getAdmin())
            .replace(PLACEHOLDER_EXPIRE_IN, this.getExpireDate() > 0 ? TimeUT.formatTimeLeft(this.getExpireDate()) : banManager.getLang().Other_Never.getMsg())
            .replace(PLACEHOLDER_DATE_CREATED, Config.GENERAL_DATE_FORMAT.format(this.getCreatedDate()))
            .replace(PLACEHOLDER_DATE_EXPIRED, Config.GENERAL_DATE_FORMAT.format(this.getExpireDate()))
            ;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public PunishmentType getType() {
        return type;
    }

    @NotNull
    public String getUser() {
        return this.user;
    }

    public boolean isIp() {
        return isIp;
    }

    @NotNull
    public String getReason() {
        return this.reason;
    }

    @NotNull
    public String getAdmin() {
        return this.admin;
    }

    public long getCreatedDate() {
        return this.createdDate;
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    public void expire() {
        this.expireDate = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return !this.isPermanent() && System.currentTimeMillis() >= this.getExpireDate();
    }

    public boolean isPermanent() {
        return this.getExpireDate() < 0;
    }

    @Override
    public String toString() {
        return "Punishment{" +
            "type=" + type +
            ", user='" + user + '\'' +
            ", reason='" + reason + '\'' +
            ", admin='" + admin + '\'' +
            ", createdDate=" + createdDate +
            ", expireDate=" + expireDate +
            '}';
    }
}
