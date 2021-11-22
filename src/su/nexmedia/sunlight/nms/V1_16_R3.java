package su.nexmedia.sunlight.nms;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;

import java.util.regex.Matcher;

public class V1_16_R3 implements SunNMS {

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
