package one.oth3r.directionhud.files;

import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.utils.Utl;

import java.io.*;
import java.util.*;

public class PlayerData {
    public static Map<ServerPlayerEntity,Map<String,Object>> playerMap = new HashMap<>();
    public static File getFile(ServerPlayerEntity player) {
        return new File(DirectionHUD.playerData+player.getUuidAsString()+".json");
    }
    private static Map<String, Object> parseObject(String jsonString) {
//        DirectionHUD.LOGGER.info("OBJECT: "+jsonString);
        String ky = Utl.createID().substring(6);
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        int depth = 0; // track depth of nested objects
        String key = null;
        while (i < jsonString.length()) {
            char c = jsonString.charAt(i);
//            DirectionHUD.LOGGER.info(ky+" "+i+" "+c);
            if (c == '"') {
                int j = i + 1;
                while (j < jsonString.length() && jsonString.charAt(j) != '"') {
                    j++;
                }
                key = jsonString.substring(i + 1, j);
                i = j + 1; // skip over the closing quote
            } else if (Character.isWhitespace(c)) {
                i++; // skip whitespace
            } else if (c == ':') {
                while (Character.isWhitespace(jsonString.charAt(i+1))) {
                    i++;
                }
                Map.Entry<Object, Integer> valuePair = parseValue(jsonString.substring(i + 1));
                Object value = valuePair.getKey();
                i += valuePair.getValue()+1; // skip over the value
                map.put(key, value);
                key = null;
            } else if (c == '{') {
                depth++;
                i++;
            } else if (c == '}') {
                depth--;
                i++;
                if (depth == 0) {
                    break; // end of object
                }
            } else if (c == ',') {
                i++;
            } else {
                throw new IllegalArgumentException("Invalid JSON string: " + jsonString);
            }
        }
        return map;
    }
    private static Map.Entry<Object, Integer> parseValue(String jsonString) {
//        DirectionHUD.LOGGER.info("VALUE:"+jsonString);
        if (jsonString.startsWith("{")) {
            Map<String, Object> map = parseObject(jsonString);
            int i = 1;
            int depth = 1;
            while (i < jsonString.length()) {
                char c = jsonString.charAt(i);
                i++;
                if (c == '{') {
                    depth++;
                }
                if (c == '}') {
                    depth--;
                    if (depth == 0) break;
                }
            }
            return new AbstractMap.SimpleEntry<>(map, i);
        } else if (jsonString.startsWith("[")) {
            int i = 1;
            int depth = 1;
            while (i < jsonString.length()) {
                char c = jsonString.charAt(i);
                i++;
                if (c == '[') {
                    depth++;
                }
                if (c == ']') {
                    depth--;
                    if (depth == 0) break;
                }
            }
            return new AbstractMap.SimpleEntry<>(parseArray(jsonString), i+1);
        } else if (jsonString.startsWith("\"")) {
            int i = 1;
            while (i < jsonString.length() && jsonString.charAt(i) != '"') {
                i++;
            }
            return new AbstractMap.SimpleEntry<>(jsonString.substring(1, i), i + 1);
        } else if (jsonString.startsWith("true")) {
            return new AbstractMap.SimpleEntry<>(true, 4);
        } else if (jsonString.startsWith("false")) {
            return new AbstractMap.SimpleEntry<>(false, 5);
        } else if (jsonString.startsWith("null")) {
            return new AbstractMap.SimpleEntry<>(null, 4);
        } else {
            int i = 0;
            while (i < jsonString.length() && (Character.isDigit(jsonString.charAt(i)) || jsonString.charAt(i) == '-')) {
                i++;
            }
            if (i < jsonString.length() && jsonString.charAt(i) == '.') {
                i++;
                while (i < jsonString.length() && Character.isDigit(jsonString.charAt(i))) {
                    i++;
                }
                return new AbstractMap.SimpleEntry<>(Double.parseDouble(jsonString.substring(0, i)), i);
            } else {
                return new AbstractMap.SimpleEntry<>(Integer.parseInt(jsonString.substring(0, i)), i);
            }
        }
    }
    private static Object parseArray(String jsonString) {
//        DirectionHUD.LOGGER.info("ARRAY: "+jsonString);
        String ky = Utl.createID().substring(6);
        ArrayList<Object> array = new ArrayList<>();
        int i = 1;
        while (i < jsonString.length()) {
            char c = jsonString.charAt(i);
//            DirectionHUD.LOGGER.info(ky+" "+i+" "+c);
            if (c == ',') {
                i++;
            } else if (Character.isWhitespace(c)) {
                i++; // skip whitespace
            } else if (c == ']') {
                break;
            } else {
                while (Character.isWhitespace(jsonString.charAt(i+1))) {
                    i++;
                }
                Map.Entry<Object, Integer> valuePair = parseValue(jsonString.substring(i));
                Object value = valuePair.getKey();
                i += valuePair.getValue(); // skip over the value
                array.add(value);
            }
        }
        return array;
    }
    public static Map<String, Object> getMap(ServerPlayerEntity player) {
        File file = getFile(player);
        if (!file.exists()) {
            return getDefaults(player);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String jsonString = br.readLine();
            return parseObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    @SuppressWarnings("unchecked")
    private static String mapToJSONString(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.append("\"");
            builder.append(entry.getKey());
            builder.append("\":");
            if (entry.getValue() instanceof Map) {
                builder.append(mapToJSONString((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof String) {
                builder.append("\"");
                builder.append(entry.getValue());
                builder.append("\"");
            } else if (entry.getValue() instanceof List) {
                List<Object> list = (List<Object>) entry.getValue();
                builder.append("[");
                for (Object item : list) {
                    if (item instanceof Map) {
                        builder.append(mapToJSONString((Map<String, Object>) item));
                    } else if (item instanceof String) {
                        builder.append("\"");
                        builder.append(item);
                        builder.append("\"");
                    } else {
                        builder.append(item);
                    }
                    builder.append(",");
                }
                if (!list.isEmpty()) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("]");
            } else {
                builder.append(entry.getValue());
            }
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }
    public static void writeMap(ServerPlayerEntity player, Map<String, Object> map) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(player)))) {
            writer.write(mapToJSONString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addPlayer(ServerPlayerEntity player) {
        Map<String, Object> map = getMap(player);
        map.put("name",Utl.player.name(player));
        writeMap(player, map);
        playerMap.put(player,removeUnnecessary(map));
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        Map<String,Object> dSet = (Map<String, Object>) dest.get("settings");
        dSet.remove("track");
        dSet.remove("send");
        dest.remove("saved");
        map.put("destination", dest);
        map.remove("name");

        //removes map.name, map.destination.saved, map.destination.settings.track & send
        return map;
    }
    public static void removePlayer(ServerPlayerEntity player) {
        playerMap.remove(player);
    }

    public static Map<String,Object> getDefaults(ServerPlayerEntity player) {
        Map<String,Object> map = new HashMap<>();
        //hud
        Map<String,Object> hud = new HashMap<>();
        hud.put("enabled", config.HUDEnabled);
        hud.put("settings", defaults.hudSettings());
        hud.put("module", defaults.hudModule());
        hud.put("order", config.HUDOrder);
        hud.put("primary", HUD.color.defaultFormat(1));
        hud.put("secondary", HUD.color.defaultFormat(2));
        //dest
        Map<String,Object> destination = new HashMap<>();
        destination.put("xyz", "f");
        destination.put("settings", defaults.destSetting());
        destination.put("lastdeath", "f f f");
        destination.put("track", null);
        destination.put("suspended", null);
        //base
        map.put("version", 1.0);
        map.put("name", Utl.player.name(player));
        map.put("hud", hud);
        map.put("destination", destination);
        return map;
    }
    public static class defaults {
        public static Map<String,Object> hudSettings() {
            Map<String,Object> hudSettings = new HashMap<>();
            hudSettings.put("time24h", config.HUD24HR);
            return hudSettings;
        }
        public static Map<String,Object> hudModule() {
            Map<String,Object> hudModule = new HashMap<>();
            hudModule.put("coordinates", config.HUDCoordinates);
            hudModule.put("distance", config.HUDDistance);
            hudModule.put("destination", config.HUDDestination);
            hudModule.put("direction", config.HUDDirection);
            hudModule.put("compass", config.HUDCompass);
            hudModule.put("time", config.HUDTime);
            hudModule.put("weather", config.HUDWeather);
            return hudModule;
        }
        public static Map<String,Object> destSetting() {
            Map<String,Object> destSettings = new HashMap<>();
            destSettings.put("autoclear", config.DESTAutoClear);
            destSettings.put("autoclearradius", config.DESTAutoClearRad);
            destSettings.put("ylevel", config.DESTYLevel);
            destSettings.put("send", config.DESTSend);
            destSettings.put("track", config.DESTTrack);
            destSettings.put("particles", destParticles());
            return destSettings;
        }
        public static Map<String,Object> destParticles() {
            Map<String,Object> destParticles = new HashMap<>();
            destParticles.put("line", config.DESTLineParticles);
            destParticles.put("linecolor", config.DESTLineParticleColor);
            destParticles.put("dest", config.DESTDestParticles);
            destParticles.put("destcolor", config.DESTDestParticleColor);
            return destParticles;
        }
    }
    //TODO
    // WE ARE CLOSER /DEST SAVED DOESNT WORK IDK WHY I GOTTA SLEEP THO
    // GL ON THE TEST ME!!
    public static Map<String,Object> getFromMap(ServerPlayerEntity player) {
        return playerMap.get(player);
    }
    @SuppressWarnings("unchecked")
    public static class get {
        public static class hud {
            private static Map<String,Object> get(ServerPlayerEntity player) {
                return (Map<String, Object>) getFromMap(player).get("hud");
            }
            public static Map<String,Object> getSettings(ServerPlayerEntity player) {
                return (Map<String,Object>) get(player).get("settings");
            }
            public static Map<String,Object> getModule(ServerPlayerEntity player) {
                return (Map<String,Object>) get(player).get("module");
            }
            public static String order(ServerPlayerEntity player) {
                return (String) get(player).get("order");
            }
            public static boolean state(ServerPlayerEntity player) {
                return (boolean) get(player).get("enabled");
            }
            public static String primary(ServerPlayerEntity player) {
                return (String) get(player).get("primary");
            }
            public static String secondary(ServerPlayerEntity player) {
                return (String) get(player).get("secondary");
            }
            public static class settings {
                public static boolean time24h(ServerPlayerEntity player) {
                    return (boolean) getSettings(player).get("time24h");
                }
            }
            public static class module {
                public static boolean coordinates(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("coordinates");
                }
                public static boolean distance(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("distance");
                }
                public static boolean destination(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("destination");
                }
                public static boolean direction(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("direction");
                }
                public static boolean compass(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("compass");
                }
                public static boolean time(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("time");
                }
                public static boolean weather(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("weather");
                }
            }
        }
        public static class dest {
            private static Map<String,Object> get(ServerPlayerEntity player, boolean map) {
                if (map) return (Map<String,Object>) getFromMap(player).get("destination");
                DirectionHUD.LOGGER.info("MAP DEST: "+getFromMap(player).get("destination"));
                DirectionHUD.LOGGER.info("FILE DEST: "+getMap(player).get("destination"));
                DirectionHUD.LOGGER.info("MAP: "+getFromMap(player));
                DirectionHUD.LOGGER.info("FILE: "+getMap(player));
                return (Map<String, Object>) getMap(player).get("destination");
            }
            private static Map<String,Object> getSetting(ServerPlayerEntity player, boolean map) {
                return (Map<String,Object>) get(player,map).get("settings");
            }
            private static Map<String,Object> getParticleSetting(ServerPlayerEntity player) {
                return (Map<String,Object>) dest.getSetting(player, true).get("particles");
            }
            private static Map<String,Object> getTrack(ServerPlayerEntity player) {
                if (get(player,true).get("track") == null) return new HashMap<>();
                return (Map<String,Object>) get(player,true).get("track");
            }
            private static Map<String,Object> getSuspended(ServerPlayerEntity player) {
                if (get(player,true).get("suspended") == null) return new HashMap<>();
                return (Map<String,Object>) get(player,true).get("suspended");
            }
            public static String getLastdeath(ServerPlayerEntity player) {
                return get(player,false).get("lastdeath").toString();
            }
            public static boolean getTrackingPending(ServerPlayerEntity player) {
                return get(player,true).get("track") != null;
            }
            public static boolean getSuspendedState(ServerPlayerEntity player) {
                return get(player,true).get("suspended") != null;
            }
            public static String getDest(ServerPlayerEntity player) {
                return get(player,true).get("xyz").toString();
            }
            public static ArrayList<String> getSaved(ServerPlayerEntity player) {
                if (get(player,false).get("saved") == null) return new ArrayList<>();
                return (ArrayList<String>) get(player,false).get("saved");
            }
            public static class settings {
                public static boolean autoclear(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, true).get("autoclear");
                }
                public static int autoclearrad(ServerPlayerEntity player) {
                    return Integer.parseInt(getSetting(player, true).get("autoclearradius").toString());
                }
                public static boolean ylevel(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, true).get("ylevel");
                }
                public static boolean send(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, false).get("send");
                }
                public static boolean track(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, false).get("track");
                }
                public static class particle {
                    public static boolean line(ServerPlayerEntity player) {
                        return (boolean) getParticleSetting(player).get("line");
                    }
                    public static String linecolor(ServerPlayerEntity player) {
                        return (String) getParticleSetting(player).get("linecolor");
                    }
                    public static boolean dest(ServerPlayerEntity player) {
                        return (boolean) getParticleSetting(player).get("dest");
                    }
                    public static String destcolor(ServerPlayerEntity player) {
                        return (String) getParticleSetting(player).get("destcolor");
                    }
                }
            }
            public static class track {
                public static String id(ServerPlayerEntity player) {
                    return (String) getTrack(player).get("id");
                }
                public static int expire(ServerPlayerEntity player) {
                    return Integer.parseInt(getTrack(player).get("expire").toString());
                }
                public static String target(ServerPlayerEntity player) {
                    return (String) getTrack(player).get("target");
                }
            }
            public static class suspended {
                public static int expire(ServerPlayerEntity player) {
                    return Integer.parseInt(getSuspended(player).get("expire").toString());
                }
                public static String target(ServerPlayerEntity player) {
                    return (String) getSuspended(player).get("target");
                }
            }
        }
    }
    public static class set {
        public static class hud {
            public static void set(ServerPlayerEntity player, Map<String,Object> hud) {
                Map<String,Object> map = getMap(player);
                map.put("hud",hud);
                writeMap(player,map);
                playerMap.put(player,removeUnnecessary(map));
            }
            private static void setSetting(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.hud.get(player);
                data.put("setting", setting);
                set(player, data);
            }
            public static void setModule(ServerPlayerEntity player, Map<String,Object> module) {
                Map<String,Object> data = get.hud.get(player);
                data.put("module", module);
                set(player, data);
            }
            public static void order(ServerPlayerEntity player, String order) {
                Map<String,Object> data = get.hud.get(player);
                data.put("order", order);
                set(player, data);
            }
            public static void state(ServerPlayerEntity player, boolean b) {
                Map<String,Object> data = get.hud.get(player);
                data.put("enabled", b);
                set(player, data);
            }
            public static void primary(ServerPlayerEntity player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("primary", color);
                set(player, data);
            }
            public static void secondary(ServerPlayerEntity player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("secondary", color);
                set(player, data);
            }
            public static class setting {
                public static void time24h(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.hud.getSettings(player);
                    data.put("time24h", b);
                    setSetting(player, data);
                }
            }
            public static class module {
                public static void byName(ServerPlayerEntity player,String moduleName,boolean b) {
                    Map<String,Object> data = get.hud.getModule(player);
                    data.put(moduleName, b);
                    setModule(player, data);
                }
            }
        }
        public static class dest {
            public static void set(ServerPlayerEntity player, Map<String,Object> dest) {
                Map<String,Object> map = getMap(player);
                map.put("destination",dest);
                writeMap(player,map);
                playerMap.put(player,map);
            }
            private static void setSetting(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("setting", setting);
                set(player, data);
            }
            private static void setParticleSetting(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.getSetting(player,false);
                data.put("particles", setting);
                setSetting(player, data);
            }
            private static void setTrack(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", setting);
                set(player, data);
            }
            private static void setSuspended(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("suspended", setting);
                set(player, data);
            }
            public static void setDest(ServerPlayerEntity player, String xyz) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("xyz", xyz);
                set(player, data);
            }
            public static void setLastdeath(ServerPlayerEntity player, String lastdeath) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("lastdeath", lastdeath);
                set(player, data);
            }
            public static void setTrackNull(ServerPlayerEntity player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", null);
                set(player, data);
            }
            public static void setSuspendedNull(ServerPlayerEntity player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("suspended", null);
                set(player, data);
            }
            public static void setSaved(ServerPlayerEntity player, ArrayList<String> saved) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("saved", saved);
                set(player, data);
            }
            public static class setting {
                public static void autoclear(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,true);
                    data.put("autoclear", b);
                    setSetting(player, data);
                }
                public static void autoclearrad(ServerPlayerEntity player, int b) {
                    Map<String,Object> data = get.dest.getSetting(player,true);
                    data.put("autoclearradius", b);
                    setSetting(player, data);
                }
                public static void ylevel(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,true);
                    data.put("ylevel", b);
                    setSetting(player, data);
                }
                public static void send(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("send", b);
                    setSetting(player, data);
                }
                public static void track(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("track", b);
                    setSetting(player, data);
                }
                public static class particles {
                    public static void line(ServerPlayerEntity player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("line", b);
                        setParticleSetting(player, data);
                    }
                    public static void linecolor(ServerPlayerEntity player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("linecolor", b);
                        setParticleSetting(player, data);
                    }
                    public static void dest(ServerPlayerEntity player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("dest", b);
                        setParticleSetting(player, data);
                    }
                    public static void destcolor(ServerPlayerEntity player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("destcolor", b);
                        setParticleSetting(player, data);
                    }
                }
            }
            public static class track {
                public static void id(ServerPlayerEntity player, String b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("id", b);
                    setTrack(player, data);
                }
                public static void expire(ServerPlayerEntity player, int b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("expire", b);
                    setTrack(player, data);
                }
                public static void target(ServerPlayerEntity player, String b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("target", b);
                    setTrack(player, data);
                }
            }
            public static class suspended {
                public static void expire(ServerPlayerEntity player, int b) {
                    Map<String,Object> data = get.dest.getSuspended(player);
                    data.put("expire", b);
                    setSuspended(player, data);
                }
                public static void target(ServerPlayerEntity player, String b) {
                    Map<String,Object> data = get.dest.getSuspended(player);
                    data.put("target", b);
                    setSuspended(player, data);
                }
            }
        }
    }
}
