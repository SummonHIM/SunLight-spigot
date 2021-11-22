package su.nexmedia.sunlight.nms;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface SunNMS {

    void virtAnvil(@NotNull Player player);

    void resetSleepTime(@NotNull Player player);

    double getTPS();

    @NotNull
    Object getHexedChatComponent(@NotNull String text);
}
