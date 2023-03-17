package one.oth3r.directionhud.files;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.utils.Utl;
import net.minecraft.server.network.ServerPlayerEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public static File playerdata(ServerPlayerEntity player) {
        return new File(DirectionHUD.playerData+player.getUuidAsString()+".json");
    }
    @SuppressWarnings("unchecked")
    public static class defaults {
        public static JSONObject hudSetting() {
            JSONObject hudsetting = new JSONObject();
            hudsetting.put("time24h", config.HUD24HR);
            return hudsetting;
        }
        public static JSONObject hudModule() {
            JSONObject hudmodule = new JSONObject();
            hudmodule.put("coordinates", config.HUDCoordinates);
            hudmodule.put("distance", config.HUDDistance);
            hudmodule.put("destination", config.HUDDestination);
            hudmodule.put("direction", config.HUDDirection);
            hudmodule.put("compass", config.HUDCompass);
            hudmodule.put("time", config.HUDTime);
            hudmodule.put("weather", config.HUDWeather);
            return hudmodule;
        }
        public static JSONObject destSetting() {
            JSONObject destsetting = new JSONObject();
            destsetting.put("autoclear", config.DESTAutoClear);
            destsetting.put("autoclearradius", config.DESTAutoClearRad);
            destsetting.put("ylevel", config.DESTYLevel);
            destsetting.put("send", config.DESTSend);
            destsetting.put("track", config.DESTTrack);
            destsetting.put("particles", destSettingParticles());
            return destsetting;
        }
        public static JSONObject destSettingParticles() {
            JSONObject destsettingP = new JSONObject();
            destsettingP.put("line", config.DESTLineParticles);
            destsettingP.put("linecolor", config.DESTLineParticleColor);
            destsettingP.put("dest", config.DESTDestParticles);
            destsettingP.put("destcolor", config.DESTDestParticleColor);
            return destsettingP;
        }
    }

    @SuppressWarnings("unchecked")
    public static void addPlayer(ServerPlayerEntity player) {
        if (playerExist(player)) {
            update(player);
            return;
        }

        JSONObject hud = new JSONObject();
        hud.put("enabled", config.HUDEnabled);
        hud.put("setting", defaults.hudSetting());
        hud.put("module", defaults.hudModule());
        hud.put("order", config.HUDOrder);
        hud.put("primary", HUD.color.defaultFormat(1));
        hud.put("secondary", HUD.color.defaultFormat(2));

        JSONObject destination = new JSONObject();
        destination.put("xyz", "f");
        destination.put("setting", defaults.destSetting());
        destination.put("lastdeath", "f f f");
        destination.put("track", null);
        destination.put("suspended", null);

        JSONObject data = new JSONObject();
        data.put("version", 1.0);
        data.put("name", Utl.player.name(player));
        data.put("hud", hud);
        data.put("destination", destination);

        writeData(player, data);
    }
    @SuppressWarnings("unchecked")
    public static void update(ServerPlayerEntity player) {
        JSONObject data = getData(player);
        data.put("name", Utl.player.name(player));
        writeData(player, data);
    }

    private static void writeData(ServerPlayerEntity player, JSONObject write) {
        try (FileWriter file = new FileWriter(playerdata(player))) {
            file.write(write.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getData(ServerPlayerEntity player) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(playerdata(player))) {
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            addPlayer(player);
            try (FileReader reader = new FileReader(playerdata(player))) {
                return (JSONObject) jsonParser.parse(reader);
            } catch (IOException | ParseException ignored) {}
        }
        return new JSONObject();
    }

    private static boolean playerExist(ServerPlayerEntity player) {
        try (FileReader ignored = new FileReader(playerdata(player))) {
            return true;
        } catch (IOException e) {return false;}
    }

    public static class get {
        public static class hud {
            private static JSONObject get(ServerPlayerEntity player) {
                return (JSONObject) getData(player).get("hud");
            }
            private static JSONObject getSetting(ServerPlayerEntity player) {
                return (JSONObject) get(player).get("setting");
            }
            private static JSONObject getModule(ServerPlayerEntity player) {
                return (JSONObject) get(player).get("module");
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
            private static JSONObject get(ServerPlayerEntity player) {
                return (JSONObject) getData(player).get("destination");
            }
            private static JSONObject getSetting(ServerPlayerEntity player) {
                return (JSONObject) get(player).get("setting");
            }
            private static JSONObject getParticleSetting(ServerPlayerEntity player) {
                return (JSONObject) dest.getSetting(player).get("particles");
            }
            private static JSONObject getTrack(ServerPlayerEntity player) {
                if (get(player).get("track") == null) return new JSONObject();
                return (JSONObject) get(player).get("track");
            }
            private static JSONObject getSuspended(ServerPlayerEntity player) {
                if (get(player).get("suspended") == null) return new JSONObject();
                return (JSONObject) get(player).get("suspended");
            }
            public static String getLastdeath(ServerPlayerEntity player) {
                return get(player).get("lastdeath").toString();
            }
            public static boolean getTrackingPending(ServerPlayerEntity player) {
                return get(player).get("track") != null;
            }
            public static boolean getSuspendedState(ServerPlayerEntity player) {
                return get(player).get("suspended") != null;
            }
            public static String getDest(ServerPlayerEntity player) {
                return get(player).get("xyz").toString();
            }
            public static ArrayList<String> getSaved(ServerPlayerEntity player) {
                ArrayList<String> listdata = new ArrayList<>();
                JSONArray jArray = (JSONArray)get(player).get("saved");
                if (jArray != null) {
                    for (Object o : jArray) {
                        listdata.add(o.toString());
                    }
                }
                return listdata;
            }
            public static class setting {
                public static boolean autoclear(ServerPlayerEntity player) {
                    return (boolean) getSetting(player).get("autoclear");
                }
                public static int autoclearrad(ServerPlayerEntity player) {
                    return Integer.parseInt(getSetting(player).get("autoclearradius").toString());
                }
                public static boolean ylevel(ServerPlayerEntity player) {
                    return (boolean) getSetting(player).get("ylevel");
                }
                public static boolean send(ServerPlayerEntity player) {
                    return (boolean) getSetting(player).get("send");
                }
                public static boolean track(ServerPlayerEntity player) {
                    return (boolean) getSetting(player).get("track");
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
    @SuppressWarnings("unchecked")
    public static class set {
        public static class hud {
            public static void set(ServerPlayerEntity player, JSONObject hud) {
                JSONObject data = getData(player);
                data.put("hud", hud);
                writeData(player, data);
            }
            private static void setSetting(ServerPlayerEntity player, JSONObject setting) {
                JSONObject data = get.hud.get(player);
                data.put("setting", setting);
                set(player, data);
            }
            public static void setModule(ServerPlayerEntity player, JSONObject module) {
                JSONObject data = get.hud.get(player);
                data.put("module", module);
                set(player, data);
            }
            public static void order(ServerPlayerEntity player, String order) {
                JSONObject data = get.hud.get(player);
                data.put("order", order);
                set(player, data);
            }
            public static void state(ServerPlayerEntity player, boolean b) {
                JSONObject data = get.hud.get(player);
                data.put("enabled", b);
                set(player, data);
            }
            public static void primary(ServerPlayerEntity player, String color) {
                JSONObject data = get.hud.get(player);
                data.put("primary", color);
                set(player, data);
            }
            public static void secondary(ServerPlayerEntity player, String color) {
                JSONObject data = get.hud.get(player);
                data.put("secondary", color);
                set(player, data);
            }
            public static class setting {
                public static void time24h(ServerPlayerEntity player, boolean b) {
                    JSONObject data = get.hud.getSetting(player);
                    data.put("time24h", b);
                    setSetting(player, data);
                }
            }
            public static class module {
                public static void byName(ServerPlayerEntity player,String moduleName ,boolean b) {
                    JSONObject data = get.hud.getModule(player);
                    data.put(moduleName, b);
                    setModule(player, data);
                }
//                public static void coordinates(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("coordinates", b);
//                    setModule(player, data);
//                }
//                public static void distance(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("distance", b);
//                    setModule(player, data);
//                }
//                public static void compass(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("compass", b);
//                    setModule(player, data);
//                }
//                public static void destination(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("destination", b);
//                    setModule(player, data);
//                }
//                public static void direction(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("direction", b);
//                    setModule(player, data);
//                }
//                public static void time(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("time", b);
//                    setModule(player, data);
//                }
//                public static void weather(ServerPlayerEntity player, boolean b) {
//                    JSONObject data = get.hud.getModule(player);
//                    data.put("weather", b);
//                    setModule(player, data);
//                }
            }
        }
        public static class dest {
            public static void set(ServerPlayerEntity player, JSONObject dest) {
                JSONObject data = getData(player);
                data.put("destination", dest);
                writeData(player, data);
            }
            private static void setSetting(ServerPlayerEntity player, JSONObject setting) {
                JSONObject data = get.dest.get(player);
                data.put("setting", setting);
                set(player, data);
            }
            private static void setParticleSetting(ServerPlayerEntity player, JSONObject setting) {
                JSONObject data = get.dest.getSetting(player);
                data.put("particles", setting);
                setSetting(player, data);
            }
            private static void setTrack(ServerPlayerEntity player, JSONObject setting) {
                JSONObject data = get.dest.get(player);
                data.put("track", setting);
                set(player, data);
            }
            private static void setSuspended(ServerPlayerEntity player, JSONObject setting) {
                JSONObject data = get.dest.get(player);
                data.put("suspended", setting);
                set(player, data);
            }
            public static void setDest(ServerPlayerEntity player, String xyz) {
                JSONObject data = get.dest.get(player);
                data.put("xyz", xyz);
                set(player, data);
            }
            public static void setLastdeath(ServerPlayerEntity player, String lastdeath) {
                JSONObject data = get.dest.get(player);
                data.put("lastdeath", lastdeath);
                set(player, data);
            }
            public static void setTrackNull(ServerPlayerEntity player) {
                JSONObject data = get.dest.get(player);
                data.put("track", null);
                set(player, data);
            }
            public static void setSuspendedNull(ServerPlayerEntity player) {
                JSONObject data = get.dest.get(player);
                data.put("suspended", null);
                set(player, data);
            }
            public static void setSaved(ServerPlayerEntity player, List<String> saved) {
                JSONObject data = get.dest.get(player);
                data.put("saved", saved);
                set(player, data);
            }
            public static class setting {
                public static void autoclear(ServerPlayerEntity player, boolean b) {
                    JSONObject data = get.dest.getSetting(player);
                    data.put("autoclear", b);
                    setSetting(player, data);
                }
                public static void autoclearrad(ServerPlayerEntity player, int b) {
                    JSONObject data = get.dest.getSetting(player);
                    data.put("autoclearradius", b);
                    setSetting(player, data);
                }
                public static void ylevel(ServerPlayerEntity player, boolean b) {
                    JSONObject data = get.dest.getSetting(player);
                    data.put("ylevel", b);
                    setSetting(player, data);
                }
                public static void send(ServerPlayerEntity player, boolean b) {
                    JSONObject data = get.dest.getSetting(player);
                    data.put("send", b);
                    setSetting(player, data);
                }
                public static void track(ServerPlayerEntity player, boolean b) {
                    JSONObject data = get.dest.getSetting(player);
                    data.put("track", b);
                    setSetting(player, data);
                }
                public static class particles {
                    public static void line(ServerPlayerEntity player, boolean b) {
                        JSONObject data = get.dest.getParticleSetting(player);
                        data.put("line", b);
                        setParticleSetting(player, data);
                    }
                    public static void linecolor(ServerPlayerEntity player, String b) {
                        JSONObject data = get.dest.getParticleSetting(player);
                        data.put("linecolor", b);
                        setParticleSetting(player, data);
                    }
                    public static void dest(ServerPlayerEntity player, boolean b) {
                        JSONObject data = get.dest.getParticleSetting(player);
                        data.put("dest", b);
                        setParticleSetting(player, data);
                    }
                    public static void destcolor(ServerPlayerEntity player, String b) {
                        JSONObject data = get.dest.getParticleSetting(player);
                        data.put("destcolor", b);
                        setParticleSetting(player, data);
                    }
                }
            }
            public static class track {
                public static void id(ServerPlayerEntity player, String b) {
                    JSONObject data = get.dest.getTrack(player);
                    data.put("id", b);
                    setTrack(player, data);
                }
                public static void expire(ServerPlayerEntity player, int b) {
                    JSONObject data = get.dest.getTrack(player);
                    data.put("expire", b);
                    setTrack(player, data);
                }
                public static void target(ServerPlayerEntity player, String b) {
                    JSONObject data = get.dest.getTrack(player);
                    data.put("target", b);
                    setTrack(player, data);
                }
            }
            public static class suspended {
                public static void expire(ServerPlayerEntity player, int b) {
                    JSONObject data = get.dest.getSuspended(player);
                    data.put("expire", b);
                    setSuspended(player, data);
                }
                public static void target(ServerPlayerEntity player, String b) {
                    JSONObject data = get.dest.getSuspended(player);
                    data.put("target", b);
                    setSuspended(player, data);
                }
            }
        }
    }
}
