package one.oth3r.directionhud.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.utils.Loc;
import one.oth3r.directionhud.utils.Utl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class PlayerData {
    public static Map<ServerPlayerEntity,Map<String,Object>> playerMap = new HashMap<>();
    public static Map<ServerPlayerEntity,Map<String,String>> oneTimeMap = new HashMap<>();
    public static File getFile(ServerPlayerEntity player) {
        if (config.online) return new File(DirectionHUD.playerData+player.getUuidAsString()+".json");
        else return new File(DirectionHUD.playerData+player.getName().getString()+".json");
    }
    public static Map<String, Object> fileToMap(ServerPlayerEntity player) {
        File file = getFile(player);
        if (!file.exists()) return getDefaults(player);
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
            return gson.fromJson(reader,new TypeToken<Map<String, Object>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    public static void mapToFile(ServerPlayerEntity player, Map<String, Object> map) {
        try (FileWriter writer = new FileWriter(getFile(player))){
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
            writer.write(gson.toJson(addExpires(player,map)));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (map.get("version").equals(1.3)) {
            // dest logic, add new tracking
            map.put("version",1.4);
            map.remove("lastdeath");
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            Map<String,Object> hud = (Map<String, Object>) map.get("hud");
            Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
            hudModule.put("tracking",hudModule.get("compass"));
            hudModule.put("compass",null);
            hud.put("module",hudModule);
            //ORDER FIX
            String order = (String) hud.get("order");
            hud.put("order",order.replace("compass","tracking"));
            map.put("hud",hud);
            //NEW TRACKING & XYZ FIX
            dest.put("tracking",null);
            String xyz = (String) dest.get("xyz");
            if (xyz.equals("f")) dest.put("dest","null");
            else if (xyz.split(" ").length == 1) {
                dest.put("dest", "null");
                dest.put("tracking",xyz);
            } else {
                String[] sp = xyz.split(" ");
                Loc loc = new Loc(xyz);
                if (sp[1].equals("n")) loc = new Loc(sp[0]+" "+sp[2]);
                dest.put("dest",loc.getLocC());
            }
            dest.remove("xyz");
            //REMOVE SUSPENDED
            Map<String,Object> suspended = (Map<String, Object>) dest.get("suspended");
            if (suspended != null) {
                dest.put("tracking",suspended.get("target"));
            }
            dest.remove("suspended");
            //SAVED FIX
            ArrayList<String> saved = (ArrayList<String>) dest.get("saved");
            List<List<String>> savedN = new ArrayList<>();
            for (String s: saved) {
                String[] split = s.split(" ");
                String[] coordS = split[1].split("_");
                Loc loc = new Loc(Utl.tryInt(coordS[0]),Utl.tryInt(coordS[1]),Utl.tryInt(coordS[2]),split[2]);
                savedN.add(saved.indexOf(s),Arrays.asList(split[0],loc.getLocC(),split[3]));
            }
            System.out.println(savedN);
            dest.put("saved",savedN);
            //LASTDEATH FIX
            ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
            for (String s:lastdeath) {
                String[] split = s.split("\\|");
                lastdeath.set(lastdeath.indexOf(s),new Loc(split[1],split[0]).getLocC());
            }
            dest.put("lastdeath",lastdeath);
            //ADD NEW PARTICLES & AUTOCONVERT
            Map<String,Object> setting = (Map<String, Object>) dest.get("setting");
            Map<String,Object> particles = (Map<String, Object>) setting.get("particles");
            particles.put("tracking",config.defaults.DESTTrackingParticles);
            particles.put("trackingcolor",config.defaults.DESTTrackingParticleColor);
            setting.put("autoconvert",config.defaults.DESTAutoConvert);
            setting.put("particles",particles);
            dest.put("setting",setting);
            map.put("destination",dest);
        }
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
        dSet.remove("send");
        dSet.remove("lastdeath");
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
        if (cdest.get("track")!=null) {
            Map<String,Object> track = (Map<String, Object>) cdest.get("track");
            if (Utl.player.getFromIdentifier((String) track.get("target")) == null) {
                mdest.put("track", null);
            } else mdest.put("track",cdest.get("track"));
        } else if (mdest.get("track") != null) mdest.put("track", null);
        map.put("destination",mdest);
        return map;
    }
    public static void addPlayer(ServerPlayerEntity player) {
        Map<String, Object> map = updater(player, fileToMap(player));
        mapToFile(player, map);
        playerMap.put(player,removeUnnecessary(map));
        oneTimeMap.put(player,new HashMap<>());
    }
    public static void removePlayer(ServerPlayerEntity player) {
        mapToFile(player, fileToMap(player));
        playerMap.remove(player);
        oneTimeMap.remove(player);
    }
    public static String getOneTime(ServerPlayerEntity player, String key) {
        return oneTimeMap.get(player).get(key);
    }
    public static void setOneTime(ServerPlayerEntity player, String key, String value) {
        Map<String,String> map = oneTimeMap.get(player);
        map.put(key,value);
        oneTimeMap.put(player,map);
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
        destination.put("dest", "null");
        destination.put("setting", defaults.destSetting());
        destination.put("saved", new ArrayList<String>());
        destination.put("lastdeath", new ArrayList<String>());
        destination.put("tracking", null);
        destination.put("track", null);
        //base
        map.put("version", 1.4);
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
            hudModule.put("tracking", config.HUDTracking);
            hudModule.put("time", config.HUDTime);
            hudModule.put("weather", config.HUDWeather);
            return hudModule;
        }
        public static Map<String,Object> destSetting() {
            Map<String,Object> destSetting = new HashMap<>();
            destSetting.put("autoclear", config.DESTAutoClear);
            destSetting.put("autoclearradius", config.DESTAutoClearRad);
            destSetting.put("autoconvert", config.DESTAutoConvert);
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
            destParticles.put("tracking", config.DESTTrackingParticles);
            destParticles.put("trackingcolor", config.DESTTrackingParticleColor);
            return destParticles;
        }
    }
    @SuppressWarnings("unchecked")
    public static class get {
        public static Map<String,Object> fromMap(ServerPlayerEntity player) {
            return playerMap.get(player);
        }
        public static class hud {
            private static Map<String,Object> get(ServerPlayerEntity player) {
                return (Map<String, Object>) fromMap(player).get("hud");
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
                public static boolean tracking(ServerPlayerEntity player) {
                    return (boolean) getModule(player).get("tracking");
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
                if (map) return (Map<String,Object>) fromMap(player).get("destination");
                return (Map<String, Object>) fileToMap(player).get("destination");
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
            public static ArrayList<String> getLastdeaths(ServerPlayerEntity player) {
                return (ArrayList<String>) get(player,false).get("lastdeath");
            }
            public static boolean getTrackPending(ServerPlayerEntity player) {
                return get(player,true).get("track") != null;
            }
            public static Loc getDest(ServerPlayerEntity player) {
                return new Loc((String) get(player,true).get("dest"));
            }
            public static String getTracking(ServerPlayerEntity player) {
                return (String) get(player,true).get("tracking");
            }
            public static List<List<String>> getSaved(ServerPlayerEntity player) {
                return (List<List<String>>) get(player,false).get("saved");
            }
            public static class setting {
                public static boolean autoclear(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, true).get("autoclear");
                }
                public static int autoclearrad(ServerPlayerEntity player) {
                    return ((Long) getSetting(player, true).get("autoclearradius")).intValue();
                }
                public static boolean autoconvert(ServerPlayerEntity player) {
                    return (boolean) getSetting(player, true).get("autoconvert");
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
                    return (boolean) getSetting(player, false).get("lastdeath");
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
                    public static boolean tracking(ServerPlayerEntity player) {
                        return (boolean) getParticleSetting(player).get("tracking");
                    }
                    public static String trackingcolor(ServerPlayerEntity player) {
                        return (String) getParticleSetting(player).get("trackingcolor");
                    }
                }
            }
            public static class track {
                public static String id(ServerPlayerEntity player) {
                    return (String) getTrack(player).get("id");
                }
                public static int expire(ServerPlayerEntity player) {
                    return ((Long) getTrack(player).get("expire")).intValue();
                }
                public static String target(ServerPlayerEntity player) {
                    return (String) getTrack(player).get("target");
                }
            }
        }
    }
    public static class set {
        public static class hud {
            public static void set(ServerPlayerEntity player, Map<String,Object> hud) {
                Map<String,Object> map = fileToMap(player);
                map.put("hud",hud);
                mapToFile(player,map);
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
                Map<String,Object> map = fileToMap(player);
                map.put("destination",dest);
                mapToFile(player,map);
                playerMap.put(player,map);
            }
            public static void setM(ServerPlayerEntity player, Map<String,Object> dest) {
                Map<String,Object> map = get.fromMap(player);
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
            public static void setDest(ServerPlayerEntity player, Loc loc) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("dest", loc.getLocC());
                set(player, data);
            }
            public static void setTracking(ServerPlayerEntity player, String s) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("tracking", s);
                set(player, data);
            }
            public static void setTrackNull(ServerPlayerEntity player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", null);
                setM(player, data);
            }
            public static void setLastdeaths(ServerPlayerEntity player, ArrayList<String> lastdeath) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("lastdeath", lastdeath);
                set(player, data);
            }
            public static void setSaved(ServerPlayerEntity player, List<List<String>> saved) {
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
                public static void autoconvert(ServerPlayerEntity player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoconvert", b);
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
                    public static void tracking(ServerPlayerEntity player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("tracking", b);
                        setParticleSetting(player, data);
                    }
                    public static void trackingcolor(ServerPlayerEntity player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("trackingcolor", b);
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
                public static void expire(ServerPlayerEntity player, long b) {
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
        }
    }
}
