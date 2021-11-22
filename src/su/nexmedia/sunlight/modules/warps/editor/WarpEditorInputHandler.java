package su.nexmedia.sunlight.modules.warps.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.editor.SunEditorInputHandler;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.config.WarpConfig;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

import java.util.List;

public class WarpEditorInputHandler extends SunEditorInputHandler<Warp> {

    public static JYML LIST;
    public static JYML WARP;

    private final WarpManager warpManager;

    public WarpEditorInputHandler(@NotNull WarpManager warpManager) {
        super(warpManager.plugin());
        this.warpManager = warpManager;

        LIST = JYML.loadOrExtract(plugin, warpManager.getPath() + "editor/list.yml");
        WARP = JYML.loadOrExtract(plugin, warpManager.getPath() + "editor/warp.yml");
    }

    @Override
    public boolean onType(
        @NotNull Player player, @NotNull Warp warp,
        @NotNull SunEditorType type, @NotNull String msg) {

        switch (type) {
            case WARP_CHANGE_WELCOME_MESSAGE -> warp.setWelcomeMessage(msg);
            case WARP_CHANGE_TELEPORT_COST_MONEY -> {
                double input = StringUT.getDouble(msg, -999, true);
                if (input == -999) {
                    EditorUtils.errorNumber(player, false);
                    return false;
                }
                warp.setTeleportCostMoney(input);
            }
            case WARP_CHANGE_NAME -> warp.setName(msg);
            case WARP_CHANGE_DESCRIPTION -> {
                List<String> description = warp.getDescription();
                if (!player.hasPermission(WarpPerms.BYPASS_EDITOR_DESCRIPTION_LIMIT)) {
                    if (description.size() >= WarpConfig.DESCRIPTION_LINE_AMOUNT) {
                        warpManager.getLang().Editor_Error_Description_Size
                            .replace("%length%", WarpConfig.DESCRIPTION_LINE_AMOUNT)
                            .send(player);
                        return true;
                    }

                    String msgRaw = StringUT.colorOff(msg);
                    if (msgRaw.length() > WarpConfig.DESCRIPTION_LINE_LENGTH) {
                        warpManager.getLang().Editor_Error_Description_Length
                            .replace("%size%", WarpConfig.DESCRIPTION_LINE_LENGTH)
                            .send(player);
                        return true;
                    }
                }
                description.add(msg);
            }
            default -> {}
        }

        warp.save();
        return true;
    }
}
