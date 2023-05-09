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
        if (config.online) return new File(DirectionHUD.playerData+player.getUuidAsString()+".json");
        else return new File(DirectionHUD.playerData+player.getName().getString()+".json");
    }
    private static Map<String, Object> parseObject(String jsonString) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        int depth = 0;
        String key = null;
        while (i < jsonString.length()) {
            char c = jsonString.charAt(i);
            if (c == '"') {
                int j = i + 1;
                while (j < jsonString.length() && jsonString.charAt(j) != '"') {
                    j++;
                }
                key = jsonString.substring(i + 1, j);
                i = j + 1;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else if (c == ':') {
                while (Character.isWhitespace(jsonString.charAt(i+1))) {
                    i++;
                }
                Map.Entry<Object, Integer> valuePair = parseValue(jsonString.substring(i + 1));
                Object value = valuePair.getKey();
                i += valuePair.getValue()+1;
                map.put(key, value);
                key = null;
            } else if (c == '{') {
                depth++;
                i++;
            } else if (c == '}') {
                depth--;
                i++;
                if (depth == 0) {
                    break;
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
        ArrayList<Object> array = new ArrayList<>();
        int i = 1;
        while (i < jsonString.length()) {
            char c = jsonString.charAt(i);
            if (c == ',') {
                i++;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else if (c == ']') {
                break;
            } else {
                while (Character.isWhitespace(jsonString.charAt(i+1))) {
                    i++;
                }
                Map.Entry<Object, Integer> valuePair = parseValue(jsonString.substring(i));
                Object value = valuePair.getKey();
                i += valuePair.getValue();
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
            writer.write(mapToJSONString(addExpires(player,map)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addPlayer(ServerPlayerEntity player) {
        Map<String, Object> map = updater(player, getMap(player));
        writeMap(player, map);
        playerMap.put(player,removeUnnecessary(map));
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> updater(ServerPlayerEntity player, Map<String,Object> map) {
        map.put("name",Utl.player.name(player));
        if (map.get("version").equals(1.0)) {
            map.put("version",1.1);
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
            dSet.put("lastdeath",config.DESTLastdeath);
            dest.put("setting",dSet);
            map.put("destination",dest);
        }
        if (map.get("version").equals(1.1)) {
            map.put("version",1.2);
            Map<String,Object> hud = (Map<String, Object>) map.get("hud");
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            String death = (String) dest.get("lastdeath");
            String[] deaths = death.split(" ");
            ArrayList<String> newDeaths = new ArrayList<>();
            for (int i = 0;i<deaths.length;i++) {
                if (deaths[i].equals("f")) continue;
                String xyz = deaths[i].replace("_"," ");
                if (i == 0) newDeaths.add("minecraft.overworld|"+xyz);
                if (i == 1) newDeaths.add("minecraft.the_nether|"+xyz);
                if (i == 2) newDeaths.add("minecraft.the_end|"+xyz);
            }
            String pri = (String) hud.get("primary");
            String sec = (String) hud.get("secondary");
            String[] priS = pri.split("-");
            String[] secS = sec.split("-");
            if (priS[0].equals("rainbow")) pri = "white-"+priS[1]+"-"+priS[2]+"-true";
            else pri = pri+"-false";
            if (secS[0].equals("rainbow")) sec = "white-"+secS[1]+"-"+secS[2]+"-true";
            else sec = sec+"-false";
            hud.put("primary",pri);
            hud.put("secondary",sec);
            dest.put("lastdeath",newDeaths);
            map.put("destination",dest);
            map.put("hud",hud);
        }
        if (map.get("version").equals(1.2)) {
            map.put("version",1.3);
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            dest.computeIfAbsent("saved", k -> new ArrayList<String>());
            if (((ArrayList<String>) dest.get("saved")).size() != 0) {
                ArrayList<String> saved = (ArrayList<String>) dest.get("saved");
                for (String s: saved) {
                    String[] split = s.split(" ");
                    switch (split[2]) {
                        case "overworld" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.overworld " + split[3]);
                        case "the_nether" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.the_nether " + split[3]);
                        case "the_end" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.the_end " + split[3]);
                    }
                }
                dest.put("saved",saved);
            }
            dest.computeIfAbsent("lastdeath", k -> new ArrayList<String>());
            ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
            for (String s:new ArrayList<>(lastdeath)) {
                if (s.startsWith("overworld|") || s.startsWith("the_nether|")) {
                    String prefix = s.startsWith("overworld|") ? "overworld|" : "the_nether|";
                    int count = 0;
                    for (String z : lastdeath) if (z.startsWith("minecraft." + prefix)) count++;
                    if (count == 0) lastdeath.set(lastdeath.indexOf(s),"minecraft."+s);
                    else lastdeath.remove(s);
                }
            }
            dest.put("lastdeath",lastdeath);
            map.put("dest",dest);
        }
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
        dSet.remove("send");
        dest.remove("saved");
        dest.remove("lastdeath");
        dest.put("setting",dSet);
        map.put("destination",dest);
        map.remove("name");
        //removes map.name, map.destination.saved, map.destination.setting.send, map.destination.lastdeath
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> addExpires(ServerPlayerEntity player, Map<String,Object> map) {
        //since the counters are stored in the map, when the file gets saved, it updates the file.
        Map<String,Object> cache = playerMap.get(player);
        if (cache == null) return map;
        Map<String,Object> cdest = (Map<String, Object>) cache.get("destination");
        Map<String,Object> mdest = (Map<String, Object>) map.get("destination");
        if (cdest.get("track")!=null) mdest.put("track",cdest.get("track"));
        else if (mdest.get("track") != null) mdest.put("track", null);
        if (cdest.get("suspended")!=null) mdest.put("suspended",cdest.get("suspended"));
        else if (mdest.get("suspended")!=null) mdest.put("suspended",null);
        map.put("destination",mdest);
        return map;
    }
    public static void removePlayer(ServerPlayerEntity player) {
        writeMap(player,getMap(player));
        playerMap.remove(player);
    }

    public static Map<String,Object> getDefaults(ServerPlayerEntity player) {
        Map<String,Object> map = new HashMap<>();
        //hud
        Map<String,Object> hud = new HashMap<>();
        hud.put("enabled", config.HUDEnabled);
        hud.put("setting", defaults.hudSetting());
        hud.put("module", defaults.hudModule());
        hud.put("order", config.HUDOrder);
        hud.put("primary", HUD.color.defaultFormat(1));
        hud.put("secondary", HUD.color.defaultFormat(2));
        //dest
        Map<String,Object> destination = new HashMap<>();
        destination.put("xyz", "f");
        destination.put("setting", defaults.destSetting());
        destination.put("saved", new ArrayList<String>());
        destination.put("lastdeath", new ArrayList<String>());
        destination.put("track", null);
        destination.put("suspended", null);
        //base
        map.put("version", 1.3);
        map.put("name", Utl.player.name(player));
        map.put("hud", hud);
        map.put("destination", destination);
        return map;
    }
    public static class defaults {
        public static Map<String,Object> hudSetting() {
            Map<String,Object> hudSetting = new HashMap<>();
            hudSetting.put("time24h", config.HUD24HR);
            return hudSetting;
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
            Map<String,Object> destSetting = new HashMap<>();
            destSetting.put("autoclear", config.DESTAutoClear);
            destSetting.put("autoclearradius", config.DESTAutoClearRad);
            destSetting.put("ylevel", config.DESTYLevel);
            destSetting.put("send", config.DESTSend);
            destSetting.put("track", config.DESTTrack);
            destSetting.put("lastdeath", config.DESTLastdeath);
            destSetting.put("particles", destParticles());
            return destSetting;
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
    public static Map<String,Object> getFromMap(ServerPlayerEntity player) {
        return playerMap.get(player);
    }
    @SuppressWarnings("unchecked")
    public static class get {
        public static class hud {
            private static Map<String,Object> get(ServerPlayerEntity player) {
                return (Map<String, Object>) getFromMap(player).get("hud");
            }
            public static Map<String,Object> getSetting(ServerPlayerEntity player) {
                return (Map<String,Object>) get(player).get("setting");
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
            public static class setting {
                public static boolean time24h(ServerPlayerEntity player) {
                    return (boolean) getSetting(player).get("time24h");
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
                return (Map<String, Object>) getMap(player).get("destination");
            }
            private static Map<String,Object> getSetting(ServerPlayerEntity player, boolean map) {
                return (Map<String,Object>) get(player,map).get("setting");
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
            public static ArrayList<String> getLastdeaths(ServerPlayerEntity player) {
                return (ArrayList<String>) get(player,false).get("lastdeath");
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
                return (ArrayList<String>) get(player,false).get("saved");
            }
            public static class setting {
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
                    return (boolean) getSetting(player, true).get("track");
                }
                public static boolean lastdeath(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, true).get("lastdeath");
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
                    Map<String,Object> data = get.hud.getSetting(player);
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
            public static void setM(ServerPlayerEntity player, Map<String,Object> dest) {
                Map<String,Object> map = getFromMap(player);
                map.put("destination",dest);
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
                setM(player, data);
            }
            private static void setSuspended(ServerPlayerEntity player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("suspended", setting);
                setM(player, data);
            }
            public static void setDest(ServerPlayerEntity player, String xyz) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("xyz", xyz);
                set(player, data);
            }
            public static void setTrackNull(ServerPlayerEntity player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", null);
                setM(player, data);
            }
            public static void setSuspendedNull(ServerPlayerEntity player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("suspended", null);
                setM(player, data);
            }
            public static void setLastdeaths(ServerPlayerEntity player, ArrayList<String> lastdeath) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("lastdeath", lastdeath);
                set(player, data);
            }
            public static void setSaved(ServerPlayerEntity player, ArrayList<String> saved) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("saved", saved);
                set(player, data);
            }
            public static class setting {
                public static void autoclear(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoclear", b);
                    setSetting(player, data);
                }
                public static void autoclearrad(ServerPlayerEntity player, int b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoclearradius", b);
                    setSetting(player, data);
                }
                public static void ylevel(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
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
                public static void lastdeath(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("lastdeath", b);
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
