package su.nexmedia.sunlight.nms;

import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

import java.util.regex.Matcher;

public class V1_17_R1 implements SunNMS {

    @Override
    public double getTPS() {
        CraftServer server = (CraftServer) Bukkit.getServer();
        DedicatedServer ded = server.getServer();
        return ded.recentTps[1];
    }

    @Override
    public void resetSleepTime(@NotNull Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().getStatisticManager().setStatistic(cp.getHandle(), StatisticList.i.b(StatisticList.n), 0);
    }

    @Override
    public void virtAnvil(@NotNull Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        int c = p.nextContainerCounter();
        p.b.sendPacket(new PacketPlayOutOpenWindow(c, Containers.h, new ChatMessage("Repairing", 9)));
    }

    @Override
    @NotNull
    public IChatMutableComponent getHexedChatComponent(@NotNull String str) {
        str = StringUT.colorHexRaw(str);
        Matcher matcher = StringUT.HEX_PATTERN.matcher(str);
        if (!matcher.find()) return new ChatComponentText(str);

        ChatComponentText compPrefix = new ChatComponentText("");
        String[] colorsPrefix = str.split("\\#");
        for (String wordColor : colorsPrefix) {
            if (wordColor.isEmpty()) continue;

            String hexRaw = wordColor.substring(0, 6);
            wordColor = wordColor.substring(6);

            ChatHexColor colorHex = ChatHexColor.a("#" + hexRaw);
            ChatModifier modifierPrefix = ChatModifier.a.setColor(colorHex);
            IChatMutableComponent comp = new ChatComponentText(wordColor).setChatModifier(modifierPrefix);
            compPrefix.addSibling(comp);
        }
        return compPrefix;
    }
}
