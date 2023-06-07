package one.oth3r.directionhud;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import one.oth3r.directionhud.files.config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.text.Text;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.List;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
//    @Override
//    public ConfigScreenFactory<?> getModConfigScreenFactory() {
//        return parent -> {
//            var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("DirectionHUD Defaults"));
//            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//            ConfigCategory hud = builder.getOrCreateCategory(CUtl.lang("config.hud"));
//            ConfigCategory hudM = builder.getOrCreateCategory(CUtl.lang("config.hud_toggles"));
//            ConfigCategory dest = builder.getOrCreateCategory(CUtl.lang("config.dest"));
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.enabled"), config.HUDEnabled)
//                    .setDefaultValue(config.defaults.HUDEnabled)
//                    .setSaveConsumer(option -> config.HUDEnabled = option)
//                    .build());
//            hud.addEntry(entryBuilder.startStrList(CUtl.lang("config.hud.order"), List.of(config.HUDOrder.split(" ")))
//                    .setDefaultValue(List.of(config.defaults.HUDOrder.split(" ")))
//                    .setTooltip(CUtl.lang("config.hud.order.hover")
//                            .append("\n").append(CUtl.lang("config.hud.order.hover_2"))
//                            .append("\n").append(CUtl.lang("config.hud.order.hover_3")))
//                    .setSaveConsumer(option -> config.HUDOrder = String.join(" ", option))
//                    .build());
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.clock"), config.HUD24HR)
//                    .setDefaultValue(config.defaults.HUD24HR)
//                    .setSaveConsumer(option -> config.HUD24HR = option)
//                    .build());
//            hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.primary")), config.HUDPrimaryColor)
//                    .setTooltip(CUtl.lang("config.colors")
//                            .append("\n").append(CUtl.lang("config.colors_2")))
//                    .setDefaultValue(config.defaults.HUDPrimaryColor)
//                    .setSaveConsumer(option -> config.HUDPrimaryColor = option)
//                    .build());
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.primary")), config.HUDPrimaryBold)
//                    .setDefaultValue(config.defaults.HUDPrimaryBold)
//                    .setSaveConsumer(option -> config.HUDPrimaryBold = option)
//                    .build());
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.primary")), config.HUDPrimaryItalics)
//                    .setDefaultValue(config.defaults.HUDPrimaryItalics)
//                    .setSaveConsumer(option -> config.HUDPrimaryItalics = option)
//                    .build());
//            hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.secondary")), config.HUDSecondaryColor)
//                    .setTooltip(CUtl.lang("config.colors")
//                            .append("\n").append(CUtl.lang("config.colors_2")))
//                    .setDefaultValue(config.defaults.HUDSecondaryColor)
//                    .setSaveConsumer(option -> config.HUDSecondaryColor = option)
//                    .build());
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.secondary")), config.HUDSecondaryBold)
//                    .setDefaultValue(config.defaults.HUDSecondaryBold)
//                    .setSaveConsumer(option -> config.HUDSecondaryBold = option)
//                    .build());
//            hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.secondary")), config.HUDSecondaryItalics)
//                    .setDefaultValue(config.defaults.HUDSecondaryItalics)
//                    .setSaveConsumer(option -> config.HUDSecondaryItalics = option)
//                    .build());
//
//
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Coordinates"), config.HUDCoordinates)
//                    .setDefaultValue(config.defaults.HUDCoordinates)
//                    .setSaveConsumer(option -> config.HUDCoordinates = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Distance"), config.HUDDistance)
//                    .setDefaultValue(config.defaults.HUDDistance)
//                    .setSaveConsumer(option -> config.HUDDistance = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Compass"), config.HUDCompass)
//                    .setDefaultValue(config.defaults.HUDCompass)
//                    .setSaveConsumer(option -> config.HUDCompass = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Destination"), config.HUDDestination)
//                    .setDefaultValue(config.defaults.HUDDestination)
//                    .setSaveConsumer(option -> config.HUDDestination = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Direction"), config.HUDDirection)
//                    .setDefaultValue(config.defaults.HUDDirection)
//                    .setSaveConsumer(option -> config.HUDDirection = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Time"), config.HUDTime)
//                    .setDefaultValue(config.defaults.HUDTime)
//                    .setSaveConsumer(option -> config.HUDTime = option)
//                    .build());
//            hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Weather"), config.HUDWeather)
//                    .setDefaultValue(config.defaults.HUDWeather)
//                    .setSaveConsumer(option -> config.HUDWeather = option)
//                    .build());
//
//            dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("AutoClear"), config.DESTAutoClear)
//                    .setDefaultValue(config.defaults.DESTAutoClear)
//                    .setSaveConsumer(option -> config.DESTAutoClear = option)
//                    .build());
//            dest.addEntry(entryBuilder.startIntSlider(CUtl.lang("config.dest.radius","AutoClear"), 2, 2 ,15)
//                    .setDefaultValue(config.defaults.DESTAutoClearRad)
//                    .setSaveConsumer(option -> config.DESTAutoClearRad = option)
//                    .build());
//            dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("YLevel"), config.DESTYLevel)
//                    .setDefaultValue(config.defaults.DESTYLevel)
//                    .setSaveConsumer(option -> config.DESTYLevel = option)
//                    .build());
//            dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Line"), config.DESTLineParticles)
//                    .setDefaultValue(config.defaults.DESTLineParticles)
//                    .setSaveConsumer(option -> config.DESTLineParticles = option)
//                    .build());
//            dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle_color","Line"), config.DESTLineParticleColor)
//                    .setTooltip(CUtl.lang("config.colors")
//                            .append("\n").append(CUtl.lang("config.colors_2")))
//                    .setDefaultValue(config.defaults.DESTLineParticleColor)
//                    .setSaveConsumer(option -> config.DESTLineParticleColor = option)
//                    .build());
//            dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticles)
//                    .setDefaultValue(config.defaults.DESTDestParticles)
//                    .setSaveConsumer(option -> config.DESTDestParticles = option)
//                    .build());
//            dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticleColor)
//                    .setTooltip(CUtl.lang("config.colors")
//                            .append("\n").append(CUtl.lang("config.colors_2")))
//                    .setDefaultValue(config.defaults.DESTDestParticleColor)
//                    .setSaveConsumer(option -> config.DESTDestParticleColor = option)
//                    .build());
//            dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Send"), config.DESTSend)
//                    .setDefaultValue(config.defaults.DESTSend)
//                    .setSaveConsumer(option -> config.DESTSend = option)
//                    .build());
//            dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Track"), config.DESTTrack)
//                    .setDefaultValue(config.defaults.DESTTrack)
//                    .setSaveConsumer(option -> config.DESTTrack = option)
//                    .build());
//
//            return builder.setSavingRunnable(() -> {
//                try {
//                    config.save();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                config.load();
//            }).build();
//        };
//    }

            //todo make this lang compatible
//    public static Screen getConfigScreenByCloth(Screens parent) {
//        var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("DirectionHUD Defaults"));
//        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//        ConfigCategory hud = builder.getOrCreateCategory(CUtl.lang("config.hud"));
//        ConfigCategory hudM = builder.getOrCreateCategory(CUtl.lang("config.hud_toggles"));
//        ConfigCategory dest = builder.getOrCreateCategory(CUtl.lang("config.dest"));
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.enabled"), config.HUDEnabled)
//                .setDefaultValue(config.defaults.HUDEnabled)
//                .setSaveConsumer(option -> config.HUDEnabled = option)
//                .build());
//        hud.addEntry(entryBuilder.startStrList(CUtl.lang("config.hud.order"), List.of(config.HUDOrder.split(" ")))
//                .setDefaultValue(List.of(config.defaults.HUDOrder.split(" ")))
//                .setTooltip(CUtl.lang("config.hud.order.hover")
//                        .append("\n").append(CUtl.lang("config.hud.order.hover_2"))
//                        .append("\n").append(CUtl.lang("config.hud.order.hover_3")))
//                .setSaveConsumer(option -> config.HUDOrder = String.join(" ", option))
//                .build());
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.clock"), config.HUD24HR)
//                .setDefaultValue(config.defaults.HUD24HR)
//                .setSaveConsumer(option -> config.HUD24HR = option)
//                .build());
//        hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.primary")), config.HUDPrimaryColor)
//                .setTooltip(CUtl.lang("config.colors")
//                        .append("\n").append(CUtl.lang("config.colors_2")))
//                .setDefaultValue(config.defaults.HUDPrimaryColor)
//                .setSaveConsumer(option -> config.HUDPrimaryColor = option)
//                .build());
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.primary")), config.HUDPrimaryBold)
//                .setDefaultValue(config.defaults.HUDPrimaryBold)
//                .setSaveConsumer(option -> config.HUDPrimaryBold = option)
//                .build());
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.primary")), config.HUDPrimaryItalics)
//                .setDefaultValue(config.defaults.HUDPrimaryItalics)
//                .setSaveConsumer(option -> config.HUDPrimaryItalics = option)
//                .build());
//        hud.addEntry(entryBuilder.startStrField(CUtl.lang("config.hud.color",CUtl.lang("config.hud.secondary")), config.HUDSecondaryColor)
//                .setTooltip(CUtl.lang("config.colors")
//                        .append("\n").append(CUtl.lang("config.colors_2")))
//                .setDefaultValue(config.defaults.HUDSecondaryColor)
//                .setSaveConsumer(option -> config.HUDSecondaryColor = option)
//                .build());
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.bold",CUtl.lang("config.hud.secondary")), config.HUDSecondaryBold)
//                .setDefaultValue(config.defaults.HUDSecondaryBold)
//                .setSaveConsumer(option -> config.HUDSecondaryBold = option)
//                .build());
//        hud.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.hud.italics",CUtl.lang("config.hud.secondary")), config.HUDSecondaryItalics)
//                .setDefaultValue(config.defaults.HUDSecondaryItalics)
//                .setSaveConsumer(option -> config.HUDSecondaryItalics = option)
//                .build());
//
//
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Coordinates"), config.HUDCoordinates)
//                .setDefaultValue(config.defaults.HUDCoordinates)
//                .setSaveConsumer(option -> config.HUDCoordinates = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Distance"), config.HUDDistance)
//                .setDefaultValue(config.defaults.HUDDistance)
//                .setSaveConsumer(option -> config.HUDDistance = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Compass"), config.HUDCompass)
//                .setDefaultValue(config.defaults.HUDCompass)
//                .setSaveConsumer(option -> config.HUDCompass = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Destination"), config.HUDDestination)
//                .setDefaultValue(config.defaults.HUDDestination)
//                .setSaveConsumer(option -> config.HUDDestination = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Direction"), config.HUDDirection)
//                .setDefaultValue(config.defaults.HUDDirection)
//                .setSaveConsumer(option -> config.HUDDirection = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Time"), config.HUDTime)
//                .setDefaultValue(config.defaults.HUDTime)
//                .setSaveConsumer(option -> config.HUDTime = option)
//                .build());
//        hudM.addEntry(entryBuilder.startBooleanToggle(Text.literal("Weather"), config.HUDWeather)
//                .setDefaultValue(config.defaults.HUDWeather)
//                .setSaveConsumer(option -> config.HUDWeather = option)
//                .build());
//
//        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("AutoClear"), config.DESTAutoClear)
//                .setDefaultValue(config.defaults.DESTAutoClear)
//                .setSaveConsumer(option -> config.DESTAutoClear = option)
//                .build());
//        dest.addEntry(entryBuilder.startIntSlider(CUtl.lang("config.dest.radius","AutoClear"), 2, 2 ,15)
//                .setDefaultValue(config.defaults.DESTAutoClearRad)
//                .setSaveConsumer(option -> config.DESTAutoClearRad = option)
//                .build());
//        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("YLevel"), config.DESTYLevel)
//                .setDefaultValue(config.defaults.DESTYLevel)
//                .setSaveConsumer(option -> config.DESTYLevel = option)
//                .build());
//        dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Line"), config.DESTLineParticles)
//                .setDefaultValue(config.defaults.DESTLineParticles)
//                .setSaveConsumer(option -> config.DESTLineParticles = option)
//                .build());
//        dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle_color","Line"), config.DESTLineParticleColor)
//                .setTooltip(CUtl.lang("config.colors")
//                        .append("\n").append(CUtl.lang("config.colors_2")))
//                .setDefaultValue(config.defaults.DESTLineParticleColor)
//                .setSaveConsumer(option -> config.DESTLineParticleColor = option)
//                .build());
//        dest.addEntry(entryBuilder.startBooleanToggle(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticles)
//                .setDefaultValue(config.defaults.DESTDestParticles)
//                .setSaveConsumer(option -> config.DESTDestParticles = option)
//                .build());
//        dest.addEntry(entryBuilder.startStrField(CUtl.lang("config.dest.particle","Destination"), config.DESTDestParticleColor)
//                .setTooltip(CUtl.lang("config.colors")
//                        .append("\n").append(CUtl.lang("config.colors_2")))
//                .setDefaultValue(config.defaults.DESTDestParticleColor)
//                .setSaveConsumer(option -> config.DESTDestParticleColor = option)
//                .build());
//        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Send"), config.DESTSend)
//                .setDefaultValue(config.defaults.DESTSend)
//                .setSaveConsumer(option -> config.DESTSend = option)
//                .build());
//        dest.addEntry(entryBuilder.startBooleanToggle(Text.literal("Track"), config.DESTTrack)
//                .setDefaultValue(config.defaults.DESTTrack)
//                .setSaveConsumer(option -> config.DESTTrack = option)
//                .build());
//
//        return builder.setSavingRunnable(() -> {
//            try {
//                config.save();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            config.load();
//        }).build();
//    }
        return parent -> YetAnotherConfigLib.createBuilder().save(config::save)
                .title(Text.of("DirectionHUD"))
                .category(ConfigCategory.createBuilder()
                        .name(CUtl.tLang("config"))
                        .tooltip(CUtl.tLang("config_info"))
                        .option(Option.createBuilder(int.class)
                                .name(CUtl.tLang("config.max_xz"))
                                .tooltip(CUtl.tLang("config.max_xz.info"))
                                .binding(config.defaults.MAXxz, () -> config.MAXxz, n -> config.MAXxz = n)
                                .controller(IntegerFieldController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(CUtl.tLang("config.max_y"))
                                .tooltip(CUtl.tLang("config.max_y.info"))
                                .binding(config.defaults.MAXy, () -> config.MAXy, n -> config.MAXy = n)
                                .controller(IntegerFieldController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.dest_saving"))
                                .tooltip(CUtl.tLang("config.dest_saving.info"))
                                .binding(config.defaults.DESTSaving, () -> config.DESTSaving, n -> config.DESTSaving = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.social"))
                                .tooltip(CUtl.tLang("config.social.info"))
                                .binding(config.defaults.social, () -> config.social, n -> config.social = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(CUtl.tLang("config.max_saved"))
                                .tooltip(CUtl.tLang("config.max_saved.info"))
                                .binding(config.defaults.MAXSaved, () -> config.MAXSaved, n -> config.MAXSaved = n)
                                .controller(IntegerFieldController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.death_saving"))
                                .tooltip(CUtl.tLang("config.death_saving.info"))
                                .binding(config.defaults.deathsaving, () -> config.deathsaving, n -> config.deathsaving = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.hud_editing"))
                                .tooltip(CUtl.tLang("config.hud_editing.info"))
                                .binding(config.defaults.HUDEditing, () -> config.HUDEditing, n -> config.HUDEditing = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(CUtl.tLang("config.hud_refresh"))
                                .tooltip(CUtl.tLang("config.hud_refresh.info"))
                                .binding(config.defaults.HUDRefresh, () -> config.HUDRefresh, n -> config.HUDRefresh = n)
                                .controller(integerOption -> new IntegerSliderController(integerOption,1,20,1))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.online_mode"))
                                .tooltip(CUtl.tLang("config.online_mode.info"))
                                .binding(config.defaults.online, () -> config.online, n -> config.online = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(ListOption.createBuilder(String.class)
                                .name(CUtl.tLang("config.dimensions"))
                                .tooltip(CUtl.tLang("config.dimensions.info").append("\n")
                                        .append(CUtl.tLang("config.dimensions.info_2").setStyle(CUtl.C('c'))))
                                .binding(config.defaults.dimensions, () -> config.dimensions, n -> config.dimensions = n)
                                .controller(StringController::new)
                                .initial("")
                                .build())
                        .option(ListOption.createBuilder(String.class)
                                .name(CUtl.tLang("config.dimension_ratios"))
                                .tooltip(CUtl.tLang("config.dimension_ratios.info"))
                                .binding(config.defaults.dimensionRatios, () -> config.dimensionRatios, n -> config.dimensionRatios = n)
                                .controller(StringController::new)
                                .initial("")
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(CUtl.tLang("config.hud"))
                        .tooltip(CUtl.tLang("config.hud_info"))
                        .option(Option.createBuilder(boolean.class)
                                .name(CUtl.tLang("config.hud.enabled"))
                                .binding(config.defaults.HUDEnabled, () -> config.HUDEnabled, n -> config.HUDEnabled = n)
                                .controller(TickBoxController::new)
                                .build())
                        .option(ListOption.createBuilder(String.class)
                                .name(CUtl.tLang("config.hud.order"))
                                .binding(List.of(config.defaults.HUDOrder.split(" ")),
                                        () -> List.of(config.HUDOrder.split(" ")), n -> config.HUDOrder = String.join(" ", n))
                                .controller(StringController::new)
                                .initial("")
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("config.hud.module"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.coordinates"))
                                        .binding(config.defaults.HUDCoordinates, () -> config.HUDCoordinates, n -> config.HUDCoordinates = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.destination"))
                                        .binding(config.defaults.HUDDestination, () -> config.HUDDestination, n -> config.HUDDestination = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.distance"))
                                        .binding(config.defaults.HUDDistance, () -> config.HUDDistance, n -> config.HUDDistance = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.tracking"))
                                        .binding(config.defaults.HUDTracking, () -> config.HUDTracking, n -> config.HUDTracking = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.direction"))
                                        .binding(config.defaults.HUDDirection, () -> config.HUDDirection, n -> config.HUDDirection = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.time"))
                                        .binding(config.defaults.HUDTime, () -> config.HUDTime, n -> config.HUDTime = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("hud.module.weather"))
                                        .binding(config.defaults.HUDWeather, () -> config.HUDWeather, n -> config.HUDWeather = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("config.hud.color_pri"))
                                .option(Option.createBuilder(Color.class)
                                        .name(CUtl.tLang("config.hud.color"))
                                        .binding(Utl.color.toColor(config.defaults.HUDPrimaryColor),
                                                () -> Utl.color.toColor(config.HUDPrimaryColor),
                                                n -> config.HUDPrimaryColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.bold"))
                                        .binding(config.defaults.HUDPrimaryBold, () -> config.HUDPrimaryBold, n -> config.HUDPrimaryBold = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.italics"))
                                        .binding(config.defaults.HUDPrimaryItalics, () -> config.HUDPrimaryItalics, n -> config.HUDPrimaryItalics = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.rainbow"))
                                        .binding(config.defaults.HUDPrimaryRainbow, () -> config.HUDPrimaryRainbow, n -> config.HUDPrimaryRainbow = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("config.hud.color_sec"))
                                .option(Option.createBuilder(Color.class)
                                        .name(CUtl.tLang("config.hud.color"))
                                        .binding(Utl.color.toColor(config.defaults.HUDSecondaryColor),
                                                () -> Utl.color.toColor(config.HUDSecondaryColor),
                                                n -> config.HUDSecondaryColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.bold"))
                                        .binding(config.defaults.HUDSecondaryBold, () -> config.HUDSecondaryBold, n -> config.HUDSecondaryBold = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.italics"))
                                        .binding(config.defaults.HUDSecondaryItalics, () -> config.HUDSecondaryItalics, n -> config.HUDSecondaryItalics = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.hud.color.rainbow"))
                                        .binding(config.defaults.HUDSecondaryRainbow, () -> config.HUDSecondaryRainbow, n -> config.HUDSecondaryRainbow = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(CUtl.tLang("config.dest"))
                        .tooltip(CUtl.tLang("config.dest_info"))
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("dest.setting.destination"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.autoclear"))
                                        .binding(config.defaults.DESTAutoClear, () -> config.DESTAutoClear, n -> config.DESTAutoClear = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(CUtl.tLang("config.dest.autoclear_rad"))
                                        .binding(config.defaults.DESTAutoClearRad, () -> config.DESTAutoClearRad, n -> config.DESTAutoClearRad = n)
                                        .controller(option -> new IntegerSliderController(option, 1, 15, 1))
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.ylevel"))
                                        .binding(config.defaults.DESTYLevel, () -> config.DESTYLevel, n -> config.DESTYLevel = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.autoconvert"))
                                        .binding(config.defaults.DESTAutoConvert, () -> config.DESTAutoConvert, n -> config.DESTAutoConvert = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("dest.setting.particle"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.dest.particle.line"))
                                        .binding(config.defaults.DESTLineParticles, () -> config.DESTLineParticles, n -> config.DESTLineParticles = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(Color.class)
                                        .name(CUtl.tLang("config.dest.particle.line_c"))
                                        .binding(Utl.color.toColor(config.defaults.DESTLineParticleColor),
                                                () -> Utl.color.toColor(config.DESTLineParticleColor),
                                                n -> config.DESTLineParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.dest.particle.dest"))
                                        .binding(config.defaults.DESTDestParticles, () -> config.DESTDestParticles, n -> config.DESTDestParticles = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(Color.class)
                                        .name(CUtl.tLang("config.dest.particle.dest_c"))
                                        .binding(Utl.color.toColor(config.defaults.DESTDestParticleColor),
                                                () -> Utl.color.toColor(config.DESTDestParticleColor),
                                                n -> config.DESTDestParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("config.dest.particle.tracking"))
                                        .binding(config.defaults.DESTTrackingParticles, () -> config.DESTTrackingParticles, n -> config.DESTTrackingParticles = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(Color.class)
                                        .name(CUtl.tLang("config.dest.particle.tracking_c"))
                                        .binding(Utl.color.toColor(config.defaults.DESTTrackingParticleColor),
                                                () -> Utl.color.toColor(config.DESTTrackingParticleColor),
                                                n -> config.DESTTrackingParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.tLang("dest.setting.features"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.send"))
                                        .binding(config.defaults.DESTSend, () -> config.DESTSend, n -> config.DESTSend = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.track"))
                                        .binding(config.defaults.DESTTrack, () -> config.DESTTrack, n -> config.DESTTrack = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(CUtl.tLang("dest.setting.lastdeath"))
                                        .binding(config.defaults.DESTLastdeath, () -> config.DESTLastdeath, n -> config.DESTLastdeath = n)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .build().generateScreen(parent);
    }
}
