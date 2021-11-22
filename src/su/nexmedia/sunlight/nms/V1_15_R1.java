package su.nexmedia.sunlight.nms;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.Containers;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_15_R1.StatisticList;

public class V1_15_R1 implements SunNMS {

    @Override
    public double getTPS() {
        CraftServer server = (CraftServer) Bukkit.getServer();
        return server.getServer().recentTps[1];
    }

    @Override
    public void resetSleepTime(@NotNull Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().getStatisticManager().setStatistic(cp.getHandle(), StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST), 0);
    }

    @Override
    public void virtAnvil(@NotNull Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, Containers.ANVIL, new ChatMessage("Repairing", 9)));
    }

    @Override
    @NotNull
    public Object getHexedChatComponent(@NotNull String text) {
        return text;
    }
}
