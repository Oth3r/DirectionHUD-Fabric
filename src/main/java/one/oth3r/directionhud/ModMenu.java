package one.oth3r.directionhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;

import java.util.List;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return ConfigBuilder.create()

                    .build();
        };
    }
    public static Screen getConfigScreenByCloth(Screen parent) {
        var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("DirectionHUD Defaults"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory hud = builder.getOrCreateCategory(CUtl.lang("config.hud"));
        ConfigCategory hudM = builder.getOrCreateCategory(CUtl.lang("config.hud_toggles"));
        ConfigCategory dest = builder.getOrCreateCategory(CUtl.lang("config.dest"));
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.enabled"), config.HUDEnabled)
                .setDefaultValue(config.defaults.HUDEnabled)
                .setSaveConsumer(option -> config.HUDEnabled = option)
                .build());
        hud.addEntry(entryBuilder.startStrList(CUtl.lang("config.hud.order"), List.of(config.HUDOrder.split(" ")))
                .setDefaultValue(List.of(config.defaults.HUDOrder.split(" ")))
                .setTooltip(CUtl.lang("config.hud.order.hover")
                        .append("\n").append(CUtl.lang("config.hud.order.hover_2"))
                        .append("\n").append(CUtl.lang("config.hud.order.hover_3")))
                .setSaveConsumer(option -> config.HUDOrder = String.join(" ", option))
                .build());
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.clock"), config.HUD24HR)
                .setDefaultValue(config.defaults.HUD24HR)
                .setSaveConsumer(option -> config.HUD24HR = option)
                .build());
        hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.primary")), config.HUDPrimaryColor)
                .setTooltip(CUtl.lang("config.colors")
                        .append("\n").append(CUtl.lang("config.colors_2")))
                .setDefaultValue(config.defaults.HUDPrimaryColor)
                .setSaveConsumer(option -> config.HUDPrimaryColor = option)
                .build());
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.primary")), config.HUDPrimaryBold)
                .setDefaultValue(config.defaults.HUDPrimaryBold)
                .setSaveConsumer(option -> config.HUDPrimaryBold = option)
                .build());
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.primary")), config.HUDPrimaryItalics)
                .setDefaultValue(config.defaults.HUDPrimaryItalics)
                .setSaveConsumer(option -> config.HUDPrimaryItalics = option)
                .build());
        hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.secondary")), config.HUDSecondaryColor)
                .setTooltip(CUtl.lang("config.colors")
                        .append("\n").append(CUtl.lang("config.colors_2")))
                .setDefaultValue(config.defaults.HUDSecondaryColor)
                .setSaveConsumer(option -> config.HUDSecondaryColor = option)
                .build());
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.secondary")), config.HUDSecondaryBold)
                .setDefaultValue(config.defaults.HUDSecondaryBold)
                .setSaveConsumer(option -> config.HUDSecondaryBold = option)
                .build());
        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.secondary")), config.HUDSecondaryItalics)
                .setDefaultValue(config.defaults.HUDSecondaryItalics)
                .setSaveConsumer(option -> config.HUDSecondaryItalics = option)
                .build());


        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Coordinates"), config.HUDCoordinates)
                .setDefaultValue(config.defaults.HUDCoordinates)
                .setSaveConsumer(option -> config.HUDCoordinates = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Distance"), config.HUDDistance)
                .setDefaultValue(config.defaults.HUDDistance)
                .setSaveConsumer(option -> config.HUDDistance = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Compass"), config.HUDCompass)
                .setDefaultValue(config.defaults.HUDCompass)
                .setSaveConsumer(option -> config.HUDCompass = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Destination"), config.HUDDestination)
                .setDefaultValue(config.defaults.HUDDestination)
                .setSaveConsumer(option -> config.HUDDestination = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Direction"), config.HUDDirection)
                .setDefaultValue(config.defaults.HUDDirection)
                .setSaveConsumer(option -> config.HUDDirection = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Time"), config.HUDTime)
                .setDefaultValue(config.defaults.HUDTime)
                .setSaveConsumer(option -> config.HUDTime = option)
                .build());
        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Weather"), config.HUDWeather)
                .setDefaultValue(config.defaults.HUDWeather)
                .setSaveConsumer(option -> config.HUDWeather = option)
                .build());

        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("AutoClear"), config.DESTAutoClear)
                .setDefaultValue(config.defaults.DESTAutoClear)
                .setSaveConsumer(option -> config.DESTAutoClear = option)
                .build());
        dest.addEntry(entryBuilder.startIntSlider(CUtl.lang("config.dest.radius","AutoClear"), 2, 2 ,15)
                .setDefaultValue(config.defaults.DESTAutoClearRad)
                .setSaveConsumer(option -> config.DESTAutoClearRad = option)
                .build());
        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("YLevel"), config.DESTYLevel)
                .setDefaultValue(config.defaults.DESTYLevel)
                .setSaveConsumer(option -> config.DESTYLevel = option)
                .build());
        dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Line"), config.DESTLineParticles)
                .setDefaultValue(config.defaults.DESTLineParticles)
                .setSaveConsumer(option -> config.DESTLineParticles = option)
                .build());
        dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle_color","Line"), config.DESTLineParticleColor)
                .setTooltip(CUtl.lang("config.colors")
                        .append("\n").append(CUtl.lang("config.colors_2")))
                .setDefaultValue(config.defaults.DESTLineParticleColor)
                .setSaveConsumer(option -> config.DESTLineParticleColor = option)
                .build());
        dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticles)
                .setDefaultValue(config.defaults.DESTDestParticles)
                .setSaveConsumer(option -> config.DESTDestParticles = option)
                .build());
        dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticleColor)
                .setTooltip(CUtl.lang("config.colors")
                        .append("\n").append(CUtl.lang("config.colors_2")))
                .setDefaultValue(config.defaults.DESTDestParticleColor)
                .setSaveConsumer(option -> config.DESTDestParticleColor = option)
                .build());
        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Send"), config.DESTSend)
                .setDefaultValue(config.defaults.DESTSend)
                .setSaveConsumer(option -> config.DESTSend = option)
                .build());
        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Track"), config.DESTTrack)
                .setDefaultValue(config.defaults.DESTTrack)
                .setSaveConsumer(option -> config.DESTTrack = option)
                .build());

        return builder.setSavingRunnable(() -> {
            try {
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
            config.load();
        }).build();
    }
}
