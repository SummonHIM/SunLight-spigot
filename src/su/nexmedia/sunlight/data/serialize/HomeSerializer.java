package su.nexmedia.sunlight.data.serialize;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.Material;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.modules.homes.Home;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class HomeSerializer implements JsonSerializer<Home>, JsonDeserializer<Home> {

    @Override
    public JsonElement serialize(Home src, Type type, JsonSerializationContext contex) {

        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("owner", src.getOwner());
        object.addProperty("name", StringUT.colorRaw(src.getName()));
        object.addProperty("iconMaterial", src.getIconMaterial().name());
        object.addProperty("location", LocUT.serialize(src.getLocation()));
        object.add("invitedPlayers", contex.serialize(src.getInvitedPlayers()));
        object.addProperty("isRespawnPoint", src.isRespawnPoint());

        return object;
    }

    @Override
    public Home deserialize(JsonElement json, Type type, JsonDeserializationContext contex)
        throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        String owner = object.get("owner").getAsString();
        Material iconMaterial = Material.getMaterial(object.get("iconMaterial").getAsString());
        if (iconMaterial == null) iconMaterial = Material.GRASS_BLOCK;

        Location location = LocUT.deserialize(object.get("location").getAsString());
        if (location == null) return null;

        Set<String> invitedPlayers = new HashSet<>();
        JsonArray jInvites = object.get("invitedPlayers").getAsJsonArray();
        for (JsonElement e : jInvites) {
            invitedPlayers.add(e.getAsString());
        }
        boolean isRespawnPoint = object.get("isRespawnPoint").getAsBoolean();

        return new Home(id, owner, name, iconMaterial, location, invitedPlayers, isRespawnPoint);
    }
}
