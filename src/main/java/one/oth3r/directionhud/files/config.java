package one.oth3r.directionhud.files;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class config {
    public static boolean HUDEnabled = defaults.HUDEnabled;
    public static String HUDOrder = defaults.HUDOrder;
    public static boolean HUDCoordinates = defaults.HUDCoordinates;
    public static boolean HUDDistance = defaults.HUDDistance;
    public static boolean HUDCompass = defaults.HUDCompass;
    public static boolean HUDDestination = defaults.HUDDestination;
    public static boolean HUDDirection = defaults.HUDDirection;
    public static boolean HUDTime = defaults.HUDTime;
    public static boolean HUDWeather = defaults.HUDWeather;
    public static boolean HUD24HR = defaults.HUD24HR;
    public static String HUDPrimaryColor = defaults.HUDPrimaryColor;
    public static boolean HUDPrimaryBold = defaults.HUDPrimaryBold;
    public static boolean HUDPrimaryItalics = defaults.HUDPrimaryItalics;
    public static String HUDSecondaryColor = defaults.HUDSecondaryColor;
    public static boolean HUDSecondaryBold = defaults.HUDSecondaryBold;
    public static boolean HUDSecondaryItalics = defaults.HUDSecondaryItalics;
    public static boolean DESTAutoClear = defaults.DESTAutoClear;
    public static int DESTAutoClearRad = defaults.DESTAutoClearRad;
    public static boolean DESTYLevel = defaults.DESTYLevel;
    public static boolean DESTLineParticles = defaults.DESTLineParticles;
    public static String DESTLineParticleColor = defaults.DESTLineParticleColor;
    public static boolean DESTDestParticles = defaults.DESTDestParticles;
    public static String DESTDestParticleColor = defaults.DESTDestParticleColor;
    public static boolean DESTSend = defaults.DESTSend;
    public static boolean DESTTrack = defaults.DESTTrack;

    public static File configFile() {
        return new File(DirectionHUD.config+"DirectionHUD.properties");
    }
    public static void load() {
        try {
            if (!configFile().exists() || !configFile().canRead())
                save();
            var fileStream = new FileInputStream(configFile());
            var properties = new Properties();
            properties.load(fileStream);
            fileStream.close();
            //HUD
            HUDEnabled = Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> defaults.HUDEnabled+""));
            HUDOrder = HUD.order.fixOrder((String) properties.computeIfAbsent("order", a -> defaults.HUDOrder));
            HUD24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> defaults.HUD24HR+""));
            HUDPrimaryColor = Utl.color.fix((String) properties.computeIfAbsent("primary-color", a -> defaults.HUDPrimaryColor),true,defaults.HUDPrimaryColor);
            HUDPrimaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> defaults.HUDPrimaryBold+""));
            HUDPrimaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> defaults.HUDPrimaryItalics+""));
            HUDSecondaryColor = Utl.color.fix((String) properties.computeIfAbsent("secondary-color", a -> defaults.HUDSecondaryColor),true,defaults.HUDSecondaryColor);
            HUDSecondaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> defaults.HUDSecondaryBold+""));
            HUDSecondaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> defaults.HUDSecondaryItalics+""));
            //MODULES
            HUDCoordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("coordinates", a -> defaults.HUDCoordinates+""));
            HUDDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("distance", a -> defaults.HUDDistance+""));
            HUDCompass = Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> defaults.HUDCompass+""));
            HUDDestination = Boolean.parseBoolean((String) properties.computeIfAbsent("destination", a -> defaults.HUDDestination+""));
            HUDDirection = Boolean.parseBoolean((String) properties.computeIfAbsent("direction", a -> defaults.HUDDirection+""));
            HUDTime = Boolean.parseBoolean((String) properties.computeIfAbsent("time", a -> defaults.HUDTime+""));
            HUDWeather = Boolean.parseBoolean((String) properties.computeIfAbsent("weather", a -> defaults.HUDWeather+""));
            //DEST
            DESTAutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("autoclear", a -> defaults.DESTAutoClear+""));
            DESTAutoClearRad = Integer.parseInt((String) properties.computeIfAbsent("autoclear-radius", a -> defaults.DESTAutoClearRad+""));
            DESTYLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("y-level", a -> defaults.DESTYLevel+""));
            DESTLineParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("line-particles", a -> defaults.DESTLineParticles+""));
            DESTLineParticleColor = Utl.color.fix((String) properties.computeIfAbsent("line-particle-color", a -> defaults.DESTLineParticleColor),false,defaults.DESTLineParticleColor);
            DESTDestParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest-particles", a -> defaults.DESTDestParticles+""));
            DESTDestParticleColor = Utl.color.fix((String) properties.computeIfAbsent("dest-particle-color", a -> defaults.DESTDestParticleColor),false,defaults.DESTDestParticleColor);
            DESTSend = Boolean.parseBoolean((String) properties.computeIfAbsent("send", a -> defaults.DESTSend+""));
            DESTTrack = Boolean.parseBoolean((String) properties.computeIfAbsent("track", a -> defaults.DESTTrack+""));

            save();
        } catch (Exception f) {
            //read fail
            f.printStackTrace();
            HUDEnabled = defaults.HUDEnabled;
            HUDOrder = defaults.HUDOrder;
            HUDCoordinates = defaults.HUDCoordinates;
            HUDDistance = defaults.HUDDistance;
            HUDCompass = defaults.HUDCompass;
            HUDDestination = defaults.HUDDestination;
            HUDDirection = defaults.HUDDirection;
            HUDTime = defaults.HUDTime;
            HUDWeather = defaults.HUDWeather;
            HUD24HR = defaults.HUD24HR;
            HUDPrimaryColor = defaults.HUDPrimaryColor;
            HUDSecondaryColor = defaults.HUDSecondaryColor;
            DESTAutoClear = defaults.DESTAutoClear;
            DESTAutoClearRad = defaults.DESTAutoClearRad;
            DESTYLevel = defaults.DESTYLevel;
            DESTLineParticles = defaults.DESTLineParticles;
            DESTLineParticleColor = defaults.DESTLineParticleColor;
            DESTDestParticles = defaults.DESTDestParticles;
            DESTDestParticleColor = defaults.DESTDestParticleColor;
            DESTSend = defaults.DESTSend;
            DESTTrack = defaults.DESTTrack;
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void save() throws IOException {
        var file = new FileOutputStream(configFile(), false);
        file.write("# DirectionHUD Defaults".getBytes());
        file.write("\n\n# HUD".getBytes());
        file.write(("\nenabled="+HUDEnabled).getBytes());
        file.write(("\norder="+HUDOrder).getBytes());
        file.write(("\n# "+CUtl.lang("config.hud.order.hover_file").getString()).getBytes());
        file.write(("\n# "+CUtl.lang("config.hud.order.hover_2").getString()+" "+CUtl.lang("config.hud.order.hover_3").getString()).getBytes());
        file.write("\n# All modules DO NOT have to be written".getBytes());
        file.write(("\ntime24hr="+HUD24HR).getBytes());
        file.write(("\nprimary-color="+HUDPrimaryColor).getBytes());
        file.write(("\nprimary-bold="+HUDPrimaryBold).getBytes());
        file.write(("\nprimary-italics="+HUDPrimaryItalics).getBytes());
        file.write(("\nsecondary-color="+HUDSecondaryColor).getBytes());
        file.write(("\nsecondary-bold="+HUDSecondaryBold).getBytes());
        file.write(("\nsecondary-italics="+HUDSecondaryItalics).getBytes());
        file.write(("\n# "+CUtl.lang("config.colors").getString()+" "+CUtl.lang("config.colors_2")).getBytes());

        file.write("\n\n# Module State".getBytes());
        file.write(("\ncoordinates="+HUDCoordinates).getBytes());
        file.write(("\ndistance="+HUDDistance).getBytes());
        file.write(("\ncompass="+HUDCompass).getBytes());
        file.write(("\ndestination="+HUDDestination).getBytes());
        file.write(("\ndirection="+HUDDirection).getBytes());
        file.write(("\ntime="+HUDTime).getBytes());
        file.write(("\nweather="+HUDWeather).getBytes());

        file.write("\n\n# Destination".getBytes());
        file.write(("\nautoclear="+DESTAutoClear).getBytes());
        file.write(("\nautoclear-radius="+DESTAutoClearRad).getBytes());
        file.write(("\ny-level="+DESTYLevel).getBytes());
        file.write(("\nline-particles="+DESTLineParticles).getBytes());
        file.write(("\nline-particle-color="+DESTLineParticleColor).getBytes());
        file.write(("\ndest-particles="+DESTDestParticles).getBytes());
        file.write(("\ndest-particle-color="+DESTDestParticleColor).getBytes());
        file.write(("\nsend="+HUDDirection).getBytes());
        file.write(("\ntrack="+HUDTime).getBytes());
    }
    public static class defaults {
        public static boolean HUDEnabled = true;
        public static String HUDOrder = HUD.order.allModules();
        public static boolean HUDCoordinates = true;
        public static boolean HUDDistance = true;
        public static boolean HUDCompass = false;
        public static boolean HUDDestination = true;
        public static boolean HUDDirection = true;
        public static boolean HUDTime = true;
        public static boolean HUDWeather = false;
        public static boolean HUD24HR = false;
        public static String HUDPrimaryColor = CUtl.c.pri;
        public static boolean HUDPrimaryBold = false;
        public static boolean HUDPrimaryItalics = false;
        public static String HUDSecondaryColor = "white";
        public static boolean HUDSecondaryBold = false;
        public static boolean HUDSecondaryItalics = false;
        public static boolean DESTAutoClear = true;
        public static int DESTAutoClearRad = 2;
        public static boolean DESTYLevel = true;
        public static boolean DESTLineParticles = true;
        public static String DESTLineParticleColor = CUtl.c.sec;
        public static boolean DESTDestParticles = true;
        public static String DESTDestParticleColor = CUtl.c.pri;
        public static boolean DESTSend = true;
        public static boolean DESTTrack = true;
    }
}
