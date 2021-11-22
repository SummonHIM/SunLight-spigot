package su.nexmedia.sunlight.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.core.config.CoreLang;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.type.WarpSortType;

public class Lang extends CoreLang {

    public ILangMsg Generic_Command_Cooldown_Default = new ILangMsg(this, "{message: ~prefix: false;}&cYou have to wait &c%time% &cbefore you can use &e%cmd% &cagain.");
    public ILangMsg Generic_Command_Cooldown_Onetime = new ILangMsg(this, "{message: ~prefix: false;}&cThis command is one-time and you already have used it.");

    public ILangMsg Command_Air_Desc        = new ILangMsg(this, "Change air level.");
    public ILangMsg Command_Air_Usage       = new ILangMsg(this, "<amount> [player]");

    // A - Commands --------------------------------------------------------
    public ILangMsg Command_Air_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Changed &a%player%'s &7air level!");
    public ILangMsg Command_Air_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Air level changed!");
    public ILangMsg Command_Anvil_Desc        = new ILangMsg(this, "Open portable anvil.");
    public ILangMsg Command_Anvil_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_Anvil_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Opened anvil for &a%player%&7.");
    public ILangMsg Command_Armor_Desc  = new ILangMsg(this, "Show player's equipment");
    public ILangMsg Command_Armor_Usage = new ILangMsg(this, "<player>");
    public ILangMsg Command_Back_Desc           = new ILangMsg(this, "Return to previous location");
    public ILangMsg Command_Back_Usage          = new ILangMsg(this, "[player]");

    // B - Commands --------------------------------------------------------
    public ILangMsg Command_Back_Error_Empty    = new ILangMsg(this, "{message: ~prefix: false;}&7No previous location.");
    public ILangMsg Command_Back_Error_BadWorld = new ILangMsg(this, "{message: ~prefix: false;}&cYou can't teleport back to that world.");
    public ILangMsg Command_Back_Done           = new ILangMsg(this, "{message: ~prefix: false;}&7Return to previous location.");
    public ILangMsg Command_Broadcast_Desc   = new ILangMsg(this, "Broadcast message.");
    public ILangMsg Command_Broadcast_Usage  = new ILangMsg(this, "<message>");
    public ILangMsg Command_Broadcast_Format = new ILangMsg(this, "{message: ~prefix: false;}&6[&eBroadcast&6] &c%msg%");
    public ILangMsg Command_Burn_Desc  = new ILangMsg(this, "Ignite a player");
    public ILangMsg Command_Burn_Usage = new ILangMsg(this, "<player> <time>");
    public ILangMsg Command_Burn_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Player &a%player% &7ignited for &a%time% &7seconds.");
    public ILangMsg Command_ClearChat_Desc = new ILangMsg(this, "Clear chat.");
    public ILangMsg Command_ClearChat_Done = new ILangMsg(this, "{message: ~prefix: false;}&7Chat has been cleared by &a%player%&7.");

    // C - Commands --------------------------------------------------------
    public ILangMsg Command_ClearInv_Desc        = new ILangMsg(this, "Clear inventory.");
    public ILangMsg Command_ClearInv_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_ClearInv_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Cleared &a%player%'s &7inventory!");
    public ILangMsg Command_ClearInv_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Inventory cleared!");
    public ILangMsg Command_CText_Invalid = new ILangMsg(this, "{message: ~prefix: false;}&7TXT file &c%file% &7not found!");
    public ILangMsg Command_Condense_Desc             = new ILangMsg(this, "Condense items into blocks.");
    public ILangMsg Command_Condense_Error_Nothing    = new ILangMsg(this, "{message: ~prefix: false;}&7Nothing to condense.");
    public ILangMsg Command_Condense_Error_NotEnought = new ILangMsg(this, "{message: ~prefix: false;}&7Not enough items to convert &c%item-from% &7to &c%item-result%&7. Need at least &c%amount%&7.");
    public ILangMsg Command_Condense_Done             = new ILangMsg(this, "{message: ~prefix: false;}&7Converted &ax%amount-from% %item-from% &7to &ax%amount-result% %item-result%");
    public ILangMsg Command_DeathBack_Desc           = new ILangMsg(this, "Return to death location");
    public ILangMsg Command_DeathBack_Usage          = new ILangMsg(this, "[player]");

    // D - Commands --------------------------------------------------------
    public ILangMsg Command_DeathBack_Error_Empty    = new ILangMsg(this, "{message: ~prefix: false;}&7No death location.");
    public ILangMsg Command_DeathBack_Error_BadWorld = new ILangMsg(this, "{message: ~prefix: false;}&cYou can't teleport back to that world.");
    public ILangMsg Command_DeathBack_Done           = new ILangMsg(this, "{message: ~prefix: false;}&7Return to death location...");
    public ILangMsg Command_Disposal_Desc = new ILangMsg(this, "Open virtual disposal.");
    public ILangMsg Command_Enchant_Desc  = new ILangMsg(this, "Enchant an item.");
    public ILangMsg Command_Enchant_Usage = new ILangMsg(this, "<enchantment> <level>");

    // E - Commands --------------------------------------------------------
    public ILangMsg Command_Enchant_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Enchanted!");
    public ILangMsg Command_Enchanting_Desc = new ILangMsg(this, "Open portable enchantment table.");
    public ILangMsg Command_Enderchest_Desc  = new ILangMsg(this, "Open portable ender chest.");
    public ILangMsg Command_Enderchest_Usage = new ILangMsg(this, "[player]");
    public ILangMsg Command_Exp_Desc  = new ILangMsg(this, "Exp manager.");
    public ILangMsg Command_Exp_Usage = new ILangMsg(this, "[player] &7or &e<give/set> <amount> &7or &e<give/set> <player> <amount>");
    public ILangMsg Command_Exp_Show  = new ILangMsg(this, "{message: ~prefix: false;}&7Exp &a%player%: &2%total% &7exp, &2%lvl% &7levels, &2%up% &7until next level.");
    public ILangMsg Command_Exp_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Done! New exp &a%player%: &2%exp% &7exp.");
    public ILangMsg Command_Ext_Desc        = new ILangMsg(this, "Extinguish the player.");
    public ILangMsg Command_Ext_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_Ext_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7You have been extinguished.");
    public ILangMsg Command_Ext_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%player% &7has been extinguished.");
    public ILangMsg Command_Feed_Desc        = new ILangMsg(this, "Restore the hunger.");
    public ILangMsg Command_Feed_Usage       = new ILangMsg(this, "[player]");

    // F - Commands --------------------------------------------------------
    public ILangMsg Command_Feed_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Feeded!");
    public ILangMsg Command_Feed_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Feeded &e%player%&7!");
    public ILangMsg Command_Fly_Desc           = new ILangMsg(this, "Toggle fly mode.");
    public ILangMsg Command_Fly_Usage          = new ILangMsg(this, "<0/1> [player]");
    public ILangMsg Command_Fly_Done_Others    = new ILangMsg(this, "{message: ~prefix: false;}&7Fly mode for &e%player%&7: &e%state%");
    public ILangMsg Command_Fly_Done_Self      = new ILangMsg(this, "{message: ~prefix: false;}&7Fly mode: &e%state%");
    public ILangMsg Command_Fly_Error_BadWorld = new ILangMsg(this, "{message: ~prefix: false;}&cFlying is not allowed here!");
    public ILangMsg Command_GameMode_Desc        = new ILangMsg(this, "Change gamemode.");
    public ILangMsg Command_GameMode_Usage       = new ILangMsg(this, "<0/1/2/3> [player]");

    // G - Commands --------------------------------------------------------
    public ILangMsg Command_GameMode_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Gamemode: &e%gm%&7.");
    public ILangMsg Command_GameMode_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Gamemode for &e%player%&7: &e%gm%&7.");
    public ILangMsg Command_Give_Desc        = new ILangMsg(this, "Give specified item to a player.");
    public ILangMsg Command_Give_Usage       = new ILangMsg(this, "<item> [amount] [player]");
    public ILangMsg Command_Give_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Given &ex%amount% %item% &7to &e%player%&7.");
    public ILangMsg Command_Give_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7You recieved &ex%amount% %item%&7.");
    public ILangMsg Command_God_Desc          = new ILangMsg(this, "Toggle god mode.");
    public ILangMsg Command_God_Usage         = new ILangMsg(this, "[1/0] [player]");
    public ILangMsg Command_God_Toggle_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7God mode: &e%state%");
    public ILangMsg Command_God_Toggle_Others = new ILangMsg(this, "{message: ~prefix: false;}&7God mode for &e%player%&7: &e%state%");
    public ILangMsg Command_God_Error_World   = new ILangMsg(this, "{message: ~prefix: false;}&cGod mode is not allowed here!");
    public ILangMsg Command_Hat_Desc = new ILangMsg(this, "Put item on hand.");
    public ILangMsg Command_Hat_Done = new ILangMsg(this, "{message: ~prefix: false;}&7Enjoy your new hat :)");

    // H - Commands --------------------------------------------------------
    public ILangMsg Command_Heal_Desc        = new ILangMsg(this, "Restore health.");
    public ILangMsg Command_Heal_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_Heal_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7You have been healed!");
    public ILangMsg Command_Heal_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&e%player% &7healed!");
    public ILangMsg Command_Ignore_Desc          = new ILangMsg(this, "Ignore specified player.");
    public ILangMsg Command_Ignore_Usage         = new ILangMsg(this, "[player]");

    // I - Commands --------------------------------------------------------
    public ILangMsg Command_Ignore_Done          = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%player% &7added in black-list. You can change settings or unblock him in &e/ignore");
    public ILangMsg Command_Ignore_Error_Already = new ILangMsg(this, "{message: ~prefix: false;}&cYou're already ignoring &e%player%&c.");
    public ILangMsg Command_Ignore_Error_Bypass  = new ILangMsg(this, "{message: ~prefix: false;}&cYou can not ignore &e%player%&c!");
    public ILangMsg Command_Inv_Desc  = new ILangMsg(this, "View player's inventory.");
    public ILangMsg Command_Inv_Usage = new ILangMsg(this, "<player>");
    public ILangMsg Command_Item_Desc  = new ILangMsg(this, "Get specified item.");
    public ILangMsg Command_Item_Usage = new ILangMsg(this, "<item> [amount]");
    public ILangMsg Command_Item_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7You recieved &ex%amount% %item%&7.");
    public ILangMsg Command_Itemname_Desc  = new ILangMsg(this, "Modify item name.");
    public ILangMsg Command_Itemname_Usage = new ILangMsg(this, "<name>");
    public ILangMsg Command_Itemname_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Item renamed!");
    public ILangMsg Command_ItemLore_Desc  = new ILangMsg(this, "Modify item lore.");
    public ILangMsg Command_ItemLore_Usage = new ILangMsg(this, "add|del|clear");
    public ILangMsg Command_ItemLore_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Item lore changed!");
    public ILangMsg Command_List_Desc = new ILangMsg(this, "Show online players and ranks.");
    public ILangMsg Command_Me_Desc  = new ILangMsg(this, "Show action in chat.");

    // J - Commands --------------------------------------------------------

    // K - Commands --------------------------------------------------------

    // L - Commands --------------------------------------------------------
    public ILangMsg Command_Me_Usage = new ILangMsg(this, "<action>");

    // M - Commands --------------------------------------------------------
    public ILangMsg Command_Mobkill_Desc      = new ILangMsg(this, "Kill specified mobs.");
    public ILangMsg Command_Mobkill_Usage     = new ILangMsg(this, "<type>");
    public ILangMsg Command_Mobkill_Done_Type = new ILangMsg(this, "{message: ~prefix: false;}&7Killed &a%amount% %type%&7!");
    public ILangMsg Command_Mobkill_Done_All  = new ILangMsg(this, "{message: ~prefix: false;}&7Killed &a%amount% &7mobs!");
    public ILangMsg Command_More_Desc = new ILangMsg(this, "Increase hand item amount.");
    public ILangMsg Command_Near_Desc       = new ILangMsg(this, "Show nearest players.");
    public ILangMsg Command_Near_Error_None = new ILangMsg(this, "{message: ~prefix: false;}&7There are no players in a radius of &e%radius% blocks&7.");

    // N - Commands --------------------------------------------------------
    public ILangMsg Command_Nick_Desc              = new ILangMsg(this, "Change name displayed.");
    public ILangMsg Command_Nick_Usage             = new ILangMsg(this, "[nick]");
    public ILangMsg Command_Nick_Done_Others       = new ILangMsg(this, "{message: ~prefix: false;}&e%player% &7nick changed to &e%nick%&7.");
    public ILangMsg Command_Nick_Done_Self         = new ILangMsg(this, "{message: ~prefix: false;}&7Your nick changed to &e%nick%&7.");
    public ILangMsg Command_Nick_Error_Blacklisted = new ILangMsg(this, "{message: ~prefix: false;}&cNick contains forbidden words.");
    public ILangMsg Command_Nick_Error_Long        = new ILangMsg(this, "{message: ~prefix: false;}&cNick can not be longer than &e20 chars&c.");
    public ILangMsg Command_Nick_Error_Short       = new ILangMsg(this, "{message: ~prefix: false;}&cNick can not be shorted than &e3 chars&c.");
    public ILangMsg Command_NoPhantom_Desc          = new ILangMsg(this, "Toggle phantom attacks.");
    public ILangMsg Command_NoPhantom_Usage         = new ILangMsg(this, "[0/1] [player]");
    public ILangMsg Command_NoPhantom_Toggle_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Anti-Phantom: &e%state%");
    public ILangMsg Command_NoPhantom_Toggle_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Anti-Phantom for &e%player%&7: &e%state%");
    public ILangMsg Command_PlayerInfo_Desc  = new ILangMsg(this, "Show player info.");
    public ILangMsg Command_PlayerInfo_Usage = new ILangMsg(this, "<player>");

    // O - Commands --------------------------------------------------------

    // P - Commands --------------------------------------------------------
    public ILangMsg Command_Potion_Desc                = new ILangMsg(this, "Modify potion items.");
    public ILangMsg Command_Potion_Usage               = new ILangMsg(this, "<effect> <amplifier> <duration>");
    public ILangMsg Command_Potion_Error_NotAPotion    = new ILangMsg(this, "{message: ~prefix: false;}&cYou must hold a potion!");
    public ILangMsg Command_Potion_Error_InvalidEffect = new ILangMsg(this, "{message: ~prefix: false;}&cInvalid effect!");
    public ILangMsg Command_Potion_Done                = new ILangMsg(this, "{message: ~prefix: false;}&7Potion created!");
    public ILangMsg Command_Repair_Desc      = new ILangMsg(this, "Repair item(s) in inventory.");
    public ILangMsg Command_Repair_Usage     = new ILangMsg(this, "[all]");

    // Q - Commands --------------------------------------------------------

    // R - Commands --------------------------------------------------------
    public ILangMsg Command_Repair_Done_Hand = new ILangMsg(this, "{message: ~prefix: false;}&7Item repaired!");
    public ILangMsg Command_Repair_Done_All  = new ILangMsg(this, "{message: ~prefix: false;}&7Repaired all items in inventory!");
    public ILangMsg Command_Reply_Desc        = new ILangMsg(this, "Quick reply on previous private message.");
    public ILangMsg Command_Reply_Usage       = new ILangMsg(this, "<text>");
    public ILangMsg Command_Reply_Error_Empty = new ILangMsg(this, "{message: ~prefix: false;}&7No one to reply.");
    public ILangMsg Command_Skull_Desc  = new ILangMsg(this, "Get player head by name.");
    public ILangMsg Command_Skull_Usage = new ILangMsg(this, "<name>");

    // S - Commands --------------------------------------------------------
    public ILangMsg Command_Skull_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7You got &e%player%'s &7head.");
    public ILangMsg Command_SocialSpy_Desc          = new ILangMsg(this, "Spy player's PMs.");
    public ILangMsg Command_SocialSpy_Usage         = new ILangMsg(this, "[1/0] [player]");
    public ILangMsg Command_SocialSpy_Toggle_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Socialspy: &e%state%");
    public ILangMsg Command_SocialSpy_Toggle_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Socialspy for &e%player%&7: &e%state%");
    public ILangMsg Command_Spawner_Desc        = new ILangMsg(this, "Change spawner type.");
    public ILangMsg Command_Spawner_Usage       = new ILangMsg(this, "<type>");
    public ILangMsg Command_Spawner_Done        = new ILangMsg(this, "{message: ~prefix: false;}&7Spawner type changed to &e%type%&7.");
    public ILangMsg Command_Spawner_Error_Type  = new ILangMsg(this, "{message: ~prefix: false;}&7This type can not be spawned.");
    public ILangMsg Command_Spawner_Error_Block = new ILangMsg(this, "{message: ~prefix: false;}&cYou must look at &espawner");
    public ILangMsg Command_SpawnMob_Desc  = new ILangMsg(this, "Spawn specified mob(s).");
    public ILangMsg Command_SpawnMob_Usage = new ILangMsg(this, "<type> [amount]");
    public ILangMsg Command_SpawnMob_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Created &ex%amount% %type%&7.");
    public ILangMsg Command_Speed_Desc             = new ILangMsg(this, "Change fly/walk speed.");
    public ILangMsg Command_Speed_Usage            = new ILangMsg(this, "<speed> [player]");
    public ILangMsg Command_Speed_Done_Self_Walk   = new ILangMsg(this, "{message: ~prefix: false;}&7Set walk speed: &e%speed%");
    public ILangMsg Command_Speed_Done_Self_Fly    = new ILangMsg(this, "{message: ~prefix: false;}&7Set fly speed: &e%speed%");
    public ILangMsg Command_Speed_Done_Others_Walk = new ILangMsg(this, "{message: ~prefix: false;}&7Set walk speed: &e%speed% &7for &e%player%");
    public ILangMsg Command_Speed_Done_Others_Fly  = new ILangMsg(this, "{message: ~prefix: false;}&7Set fly speed: &e%speed% &7for &e%player%");
    public ILangMsg Command_Sudo_Desc  = new ILangMsg(this, "Force player to execute a command or chat message.");
    public ILangMsg Command_Sudo_Usage = new ILangMsg(this, "<player> <type> <message>");
    public ILangMsg Command_Sudo_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Forced &e%player% &7to perform: &e%message%");
    public ILangMsg Command_Suicide_Desc = new ILangMsg(this, "Commit suicide.");
    public ILangMsg Command_Suicide_Done = new ILangMsg(this, "{message: ~prefix: false;}&4%player%&c commited suicide.");
    public ILangMsg Command_Summon_Desc  = new ILangMsg(this, "Summon player at your location.");
    public ILangMsg Command_Summon_Usage = new ILangMsg(this, "<player>");
    public ILangMsg Command_System_Desc = new ILangMsg(this, "Server system info.");
    public ILangMsg Command_Tell_Desc  = new ILangMsg(this, "Send private message.");
    public ILangMsg Command_Tell_Usage = new ILangMsg(this, "<player> <message>");

    // T - Commands --------------------------------------------------------
    public ILangMsg Command_Time_Desc = new ILangMsg(this, "Set or view world time.");
    public ILangMsg Command_Time_Done = new ILangMsg(this, "{message: ~prefix: false;}&7Time set: &e%time%&7 in world &e%world%&7.");
    public ILangMsg Command_Time_Info = new ILangMsg(this, "{message: ~prefix: false;}&7Time in world &e%world%&7: &6%ticks% ticks&7, &6%time%");
    public ILangMsg Command_Thunder_Desc        = new ILangMsg(this, "Summon lightning.");
    public ILangMsg Command_Thunder_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_Thunder_Done_Player = new ILangMsg(this, "{message: ~prefix: false;}&7Summoned lightning on &e%player%&7!");
    public ILangMsg Command_Thunder_Done_Block  = new ILangMsg(this, "{message: ~prefix: false;}&7Summoned lightning!");
    public ILangMsg Command_TeleportRequest_Error_Cooldown     = new ILangMsg(this, "You can send request again in &e%time%");
    public ILangMsg Command_TeleportRequest_Error_Disabled     = new ILangMsg(this, "&e%player% &cdeclines teleport request.");
    public ILangMsg Command_TeleportRequest_Call_Desc          = new ILangMsg(this, "Send teleport request.");
    public ILangMsg Command_TeleportRequest_Call_Usage         = new ILangMsg(this, "<player>");
    public ILangMsg Command_TeleportRequest_Call_Notify_From   = new ILangMsg(this, "{message: ~prefix: false;}&7Sent teleport request to &e%player%&7.");
    public ILangMsg Command_TeleportRequest_Call_Notify_To     = new ILangMsg(
        this,
        """
            {message: ~prefix: false;}&6&m                 &6&l[ &e&lTeleport Request &6&l]&6&m                 &7
            &6Player &e%player%&6 wants to teleport to you.
            &6Type &a/tpaccept %player% &6to accept
            &6or &c/tpdeny %player% &6to decline.
            &7
            &7             {json: ~hint: &a%player% &7Will be teleported to you.; ~chat-type: /tpaccept %player%;}&a&l[Accept]{end-json}         {json: ~hint:&c%player% &7won't be teleported to you.; ~chat-type: /tpdeny %player%;}&c&l[Decline]{end-json}
            &7
            """);
    public ILangMsg Command_TeleportRequest_Summon_Desc        = new ILangMsg(this, "Ask player to teleport to you.");
    public ILangMsg Command_TeleportRequest_Summon_Usage       = new ILangMsg(this, "<player>");
    public ILangMsg Command_TeleportRequest_Summon_Notify_From = new ILangMsg(this, "{message: ~prefix: false;}&7Sent teleport request to &e%player%&7.");
    public ILangMsg Command_TeleportRequest_Summon_Notify_To   = new ILangMsg(
        this,
        """
            {message: ~prefix: false;}&6&m                 &6&l[ &e&lTeleport Request &6&l]&6&m                 &7
            &6Player &e%player%&6 wants you to teleport to him.
            &6Type &a/tpaccept %player% &6to accept and teleport.
            &6or &c/tpdeny %player% &6to decline.
            &7
            &7             {json: ~hint: &7You will be teleported to &a%player%&7.; ~chat-type: /tpaccept %player%;}&a&l[Accept]{end-json}         {json: ~hint:&7You won't be teleported to &c%player%&7.; ~chat-type: /tpdeny %player%;}&c&l[Decline]{end-json}
            &7
            """);
    public ILangMsg Command_TpAccept_Desc        = new ILangMsg(this, "Accept teleport request.");
    public ILangMsg Command_TpAccept_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_TpAccept_Done        = new ILangMsg(this, "{message: ~prefix: false;}&7Teleport request accepted!");
    public ILangMsg Command_TpAccept_Error_Empty = new ILangMsg(this, "{message: ~prefix: false;}&7Nothing to accept (or teleport time is expired).");
    public ILangMsg Command_TpDeny_Desc        = new ILangMsg(this, "Decline teleport request.");
    public ILangMsg Command_TpDeny_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_TpDeny_Done_In     = new ILangMsg(this, "{message: ~prefix: false;}&7Declined teleport request.");
    public ILangMsg Command_TpDeny_Done_Out    = new ILangMsg(this, "{message: ~prefix: false;}&e%player% &cdeclines teleport request.");
    public ILangMsg Command_TpDeny_Error_Empty = new ILangMsg(this, "{message: ~prefix: false;}&7Nothing to decline (or teleport time is expired).");
    public ILangMsg Command_TpToggle_Desc          = new ILangMsg(this, "Toggle teleport requests.");
    public ILangMsg Command_TpToggle_Usage         = new ILangMsg(this, "[0/1] [player]");
    public ILangMsg Command_TpToggle_Toggle_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Teleport Requests: &e%state%");
    public ILangMsg Command_TpToggle_Toggle_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Teleport Requests for &e%player%&7: &e%state%");
    public ILangMsg Command_Tp_Desc        = new ILangMsg(this, "Teleport to specified player.");
    public ILangMsg Command_Tp_Usage       = new ILangMsg(this, "<player> &7or &f/%cmd% <who> <to>");
    public ILangMsg Command_Tp_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Teleporting to &e%player%&7...");
    public ILangMsg Command_Tp_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%who% &7teleported to &e%to%&7!");
    public ILangMsg Command_Tppos_Desc        = new ILangMsg(this, "Teleport to specified coordinates.");
    public ILangMsg Command_Tppos_Usage       = new ILangMsg(this, "<x> <y> <z> [player]");
    public ILangMsg Command_Tppos_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Teleporting...");
    public ILangMsg Command_Tppos_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%player% &7teleported to: &e%w%&7, &e%x%&7, &e%y%&7, &e%z%&7!");
    public ILangMsg Command_Top_Desc        = new ILangMsg(this, "Teleport on the highest block above.");
    public ILangMsg Command_Top_Usage       = new ILangMsg(this, "[player]");
    public ILangMsg Command_Top_Done_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Teleporting on the highest block...");
    public ILangMsg Command_Top_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%player% &7teleported on the highest block.");
    public ILangMsg Command_UnIgnore_Desc          = new ILangMsg(this, "Unblock player(s).");
    public ILangMsg Command_UnIgnore_Usage         = new ILangMsg(this, "[player]");

    // U - Commands --------------------------------------------------------
    public ILangMsg Command_UnIgnore_Done          = new ILangMsg(this, "{message: ~prefix: false;}&7Player &e%player% &7removed from ignore list.");
    public ILangMsg Command_UnIgnore_Error_Already = new ILangMsg(this, "{message: ~prefix: false;}&cPlayer &e%player% &cis not in ignore list.");
    public ILangMsg Command_Vanish_Desc   = new ILangMsg(this, "Toggle vanish.");
    public ILangMsg Command_Vanish_Toggle = new ILangMsg(this, "{message: ~prefix: false;}&7Vanish: &e%state%");

    // V - Commands --------------------------------------------------------
    public ILangMsg Command_Weather_Desc  = new ILangMsg(this, "Change world weather.");
    public ILangMsg Command_Weather_Usage = new ILangMsg(this, "<sun/storm> [world]");

    // W - Commands --------------------------------------------------------
    public ILangMsg Command_Weather_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Set &e%weather%&7 weather in world &e%world%&7.");
    public ILangMsg Command_Workbench_Desc = new ILangMsg(this, "Open portable workbench.");
    // ------------------------------------------------------------------------- //
    public ILangMsg User_Ignore_PrivateMessage  = new ILangMsg(this, "{message: ~prefix: false;}&cThis player forbids you to send him private messages.");
    public ILangMsg User_Ignore_TeleportRequest = new ILangMsg(this, "{message: ~prefix: false;}&cThis player forbids you to send him teleport requests.");

    // X - Commands --------------------------------------------------------

    // Y - Commands --------------------------------------------------------

    // Z - Commands --------------------------------------------------------
    public ILangMsg Other_Free     = new ILangMsg(this, "&aFree");
    public ILangMsg Other_On       = new ILangMsg(this, "&aON");
    public ILangMsg Other_Off      = new ILangMsg(this, "&cOFF");
    public ILangMsg Other_Eternity = new ILangMsg(this, "Eternity");
    public ILangMsg Error_InvalidName = new ILangMsg(this, "{message: ~prefix: false;}&cInvalid name: &e%name%&c. Contains forbidden characters.");
    public ILangMsg Error_Material    = new ILangMsg(this, "{message: ~prefix: false;}&cInvalid material!");
    public ILangMsg Error_Enchant     = new ILangMsg(this, "{message: ~prefix: false;}&cInvalid enchantment!");
    public ILangMsg Error_Self        = new ILangMsg(this, "{message: ~prefix: false;}&cUnable to apply on self!");
    public Lang(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    public void setup() {
        super.setup();
        this.setupEnum(WarpSortType.class);
    }

    @NotNull
    public String getOnOff(boolean b) {
        if (b) {
            return this.Other_On.getMsg();
        }
        else {
            return this.Other_Off.getMsg();
        }
    }
}
