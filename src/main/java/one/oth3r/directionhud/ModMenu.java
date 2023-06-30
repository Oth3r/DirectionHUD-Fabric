package one.oth3r.directionhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.util.List;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("DirectionHUD"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory configCategory = builder.getOrCreateCategory(CUtl.tLang("config"));
            ConfigCategory hudCategory = builder.getOrCreateCategory(CUtl.tLang("config.hud"));
            ConfigCategory destCategory = builder.getOrCreateCategory(CUtl.tLang("config.dest"));

            configCategory.setDescription(new MutableText[]{CUtl.tLang("config_info")});
            configCategory.addEntry(entryBuilder.startIntField(CUtl.tLang("config.max_xz"), config.MAXxz)
                    .setTooltip(CUtl.tLang("config.max_xz.info"))
                    .setDefaultValue(config.defaults.MAXxz)
                    .setSaveConsumer(option -> config.MAXxz = option)
                    .build());
            configCategory.addEntry(entryBuilder.startIntField(CUtl.tLang("config.max_y"), config.MAXy)
                    .setTooltip(CUtl.tLang("config.max_y.info"))
                    .setDefaultValue(config.defaults.MAXy)
                    .setSaveConsumer(option -> config.MAXy = option)
                    .build());
            configCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.dest_saving"), config.DESTSaving)
                    .setTooltip(CUtl.tLang("config.dest_saving.info"))
                    .setDefaultValue(config.defaults.DESTSaving)
                    .setSaveConsumer(option -> config.DESTSaving = option)
                    .build());
            configCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.social"), config.social)
                    .setTooltip(CUtl.tLang("config.social.info"))
                    .setDefaultValue(config.defaults.social)
                    .setSaveConsumer(option -> config.social = option)
                    .build());
            configCategory.addEntry(entryBuilder.startIntField(CUtl.tLang("config.max_saved"), config.MAXSaved)
                    .setTooltip(CUtl.tLang("config.max_saved.info"))
                    .setDefaultValue(config.defaults.MAXSaved)
                    .setSaveConsumer(option -> config.MAXSaved = option)
                    .build());
            configCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.death_saving"), config.deathsaving)
                    .setTooltip(CUtl.tLang("config.death_saving.info"))
                    .setDefaultValue(config.defaults.deathsaving)
                    .setSaveConsumer(option -> config.deathsaving = option)
                    .build());
            configCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud_editing"), config.HUDEditing)
                    .setTooltip(CUtl.tLang("config.hud_editing.info"))
                    .setDefaultValue(config.defaults.HUDEditing)
                    .setSaveConsumer(option -> config.HUDEditing = option)
                    .build());
            configCategory.addEntry(entryBuilder.startIntSlider(CUtl.tLang("config.hud_refresh"), config.HUDRefresh, 1, 20)
                    .setTooltip(CUtl.tLang("config.hud_refresh.info"))
                    .setDefaultValue(config.defaults.HUDRefresh)
                    .setSaveConsumer(option -> config.HUDRefresh = option)
                    .build());
            configCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.online_mode"), config.online)
                    .setTooltip(CUtl.tLang("config.online_mode.info"))
                    .setDefaultValue(config.defaults.online)
                    .setSaveConsumer(option -> config.online = option)
                    .build());
            configCategory.addEntry(entryBuilder.startStrList(CUtl.tLang("config.dimensions"), config.dimensions)
                    .setTooltip(CUtl.tLang("config.dimensions.info").append("\n")
                            .append(CUtl.tLang("config.dimensions.info_2").setStyle(CUtl.C('c'))))
                    .setDefaultValue(config.defaults.dimensions)
                    .setSaveConsumer(option -> config.dimensions = option)
                    .build());
            configCategory.addEntry(entryBuilder.startStrList(CUtl.tLang("config.dimension_ratios"), config.dimensionRatios)
                    .setTooltip(CUtl.tLang("config.dimension_ratios.info"))
                    .setDefaultValue(config.defaults.dimensionRatios)
                    .setSaveConsumer(option -> config.dimensionRatios = option)
                    .build());

            hudCategory.setDescription(new MutableText[]{CUtl.tLang("config.hud_info")});
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.enabled"), config.HUDEnabled)
                    .setTooltip(CUtl.tLang("config.hud.enabled.description"))
                    .setDefaultValue(config.defaults.HUDEnabled)
                    .setSaveConsumer(option -> config.HUDEnabled = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startStrList(CUtl.tLang("config.hud.order"), List.of(config.HUDOrder.split(" ")))
                    .setDefaultValue(List.of(config.defaults.HUDOrder.split(" ")))
                    .setTooltip(CUtl.tLang("config.hud.order.hover"))
                    .setSaveConsumer(option -> config.HUDOrder = String.join(" ", option))
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.coordinates"), config.HUDCoordinates)
                    .setDefaultValue(config.defaults.HUDCoordinates)
                    .setSaveConsumer(option -> config.HUDCoordinates = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.destination"), config.HUDDestination)
                    .setDefaultValue(config.defaults.HUDDestination)
                    .setSaveConsumer(option -> config.HUDDestination = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.distance"), config.HUDDistance)
                    .setDefaultValue(config.defaults.HUDDistance)
                    .setSaveConsumer(option -> config.HUDDistance = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.tracking"), config.HUDTracking)
                    .setDefaultValue(config.defaults.HUDTracking)
                    .setSaveConsumer(option -> config.HUDTracking = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.direction"), config.HUDDirection)
                    .setDefaultValue(config.defaults.HUDDirection)
                    .setSaveConsumer(option -> config.HUDDirection = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.time"), config.HUDTime)
                    .setDefaultValue(config.defaults.HUDTime)
                    .setSaveConsumer(option -> config.HUDTime = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("hud.module.weather"), config.HUDWeather)
                    .setDefaultValue(config.defaults.HUDWeather)
                    .setSaveConsumer(option -> config.HUDWeather = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.setting.24hr"), config.HUD24HR)
                    .setDefaultValue(config.defaults.HUD24HR)
                    .setSaveConsumer(option -> config.HUD24HR = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startColorField(CUtl.tLang("config.hud.color"), Utl.color.toColor(config.HUDPrimaryColor).getRGB()&0x00FFFFFF)
                    .setDefaultValue(Utl.color.toColor(config.defaults.HUDPrimaryColor).getRGB()&0x00FFFFFF)
                    .setSaveConsumer(option -> {
                        int red = (option >> 16) & 0xFF;
                        int green = (option >> 8) & 0xFF;
                        int blue = option & 0xFF;
                        config.HUDPrimaryColor = String.format("#%02X%02X%02X", red, green, blue);
                    }).build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.bold"), config.HUDPrimaryBold)
                    .setDefaultValue(config.defaults.HUDPrimaryBold)
                    .setSaveConsumer(option -> config.HUDPrimaryBold = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.italics"), config.HUDPrimaryItalics)
                    .setDefaultValue(config.defaults.HUDPrimaryItalics)
                    .setSaveConsumer(option -> config.HUDPrimaryItalics = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.rainbow"), config.HUDPrimaryRainbow)
                    .setDefaultValue(config.defaults.HUDPrimaryRainbow)
                    .setSaveConsumer(option -> config.HUDPrimaryRainbow = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startColorField(CUtl.tLang("config.hud.color"), Utl.color.toColor(config.HUDSecondaryColor).getRGB()&0x00FFFFFF)
                    .setDefaultValue(Utl.color.toColor(config.defaults.HUDSecondaryColor).getRGB()&0x00FFFFFF)
                    .setSaveConsumer(option -> {
                        int red = (option >> 16) & 0xFF;
                        int green = (option >> 8) & 0xFF;
                        int blue = option & 0xFF;
                        config.HUDSecondaryColor = String.format("#%02X%02X%02X", red, green, blue);
                    }).build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.bold"), config.HUDSecondaryBold)
                    .setDefaultValue(config.defaults.HUDSecondaryBold)
                    .setSaveConsumer(option -> config.HUDSecondaryBold = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.italics"), config.HUDSecondaryItalics)
                    .setDefaultValue(config.defaults.HUDSecondaryItalics)
                    .setSaveConsumer(option -> config.HUDSecondaryItalics = option)
                    .build());
            hudCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.hud.color.rainbow"), config.HUDSecondaryRainbow)
                    .setDefaultValue(config.defaults.HUDSecondaryRainbow)
                    .setSaveConsumer(option -> config.HUDSecondaryRainbow = option)
                    .build());

            destCategory.setDescription(new MutableText[]{CUtl.tLang("config.dest_info")});
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.autoclear"), config.DESTAutoClear)
                    .setDefaultValue(config.defaults.DESTAutoClear)
                    .setSaveConsumer(option -> config.DESTAutoClear = option)
                    .build());
            destCategory.addEntry(entryBuilder.startIntSlider(CUtl.tLang("config.dest.autoclear_rad"), config.DESTAutoClearRad,1,15)
                    .setDefaultValue(config.defaults.DESTAutoClearRad)
                    .setSaveConsumer(option -> config.DESTAutoClearRad = option)
                    .build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.ylevel"), config.DESTYLevel)
                    .setDefaultValue(config.defaults.DESTYLevel)
                    .setSaveConsumer(option -> config.DESTYLevel = option)
                    .build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.autoconvert"), config.DESTAutoConvert)
                    .setDefaultValue(config.defaults.DESTAutoConvert)
                    .setSaveConsumer(option -> config.DESTAutoConvert = option)
                    .build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.dest.particle.line"), config.DESTLineParticles)
                    .setDefaultValue(config.defaults.DESTLineParticles)
                    .setSaveConsumer(option -> config.DESTLineParticles = option)
                    .build());
            destCategory.addEntry(entryBuilder.startColorField(CUtl.tLang("config.dest.particle.line_c"), Utl.color.toColor(config.DESTLineParticleColor).getRGB()&0x00FFFFFF)
                    .setDefaultValue(Utl.color.toColor(config.defaults.DESTLineParticleColor).getRGB()&0x00FFFFFF)
                    .setSaveConsumer(option -> {
                        int red = (option >> 16) & 0xFF;
                        int green = (option >> 8) & 0xFF;
                        int blue = option & 0xFF;
                        config.DESTLineParticleColor = String.format("#%02X%02X%02X", red, green, blue);
                    }).build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.dest.particle.dest"), config.DESTDestParticles)
                    .setDefaultValue(config.defaults.DESTDestParticles)
                    .setSaveConsumer(option -> config.DESTDestParticles = option)
                    .build());
            destCategory.addEntry(entryBuilder.startColorField(CUtl.tLang("config.dest.particle.dest_c"), Utl.color.toColor(config.DESTDestParticleColor).getRGB()&0x00FFFFFF)
                    .setDefaultValue(Utl.color.toColor(config.defaults.DESTDestParticleColor).getRGB()&0x00FFFFFF)
                    .setSaveConsumer(option -> {
                        int red = (option >> 16) & 0xFF;
                        int green = (option >> 8) & 0xFF;
                        int blue = option & 0xFF;
                        config.DESTDestParticleColor = String.format("#%02X%02X%02X", red, green, blue);
                    }).build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("config.dest.particle.tracking"), config.DESTTrackingParticles)
                    .setDefaultValue(config.defaults.DESTTrackingParticles)
                    .setSaveConsumer(option -> config.DESTTrackingParticles = option)
                    .build());
            destCategory.addEntry(entryBuilder.startColorField(CUtl.tLang("config.dest.particle.tracking_c"), Utl.color.toColor(config.DESTTrackingParticleColor).getRGB()&0x00FFFFFF)
                    .setDefaultValue(Utl.color.toColor(config.defaults.DESTTrackingParticleColor).getRGB()&0x00FFFFFF)
                    .setSaveConsumer(option -> {
                        int red = (option >> 16) & 0xFF;
                        int green = (option >> 8) & 0xFF;
                        int blue = option & 0xFF;
                        config.DESTTrackingParticleColor = String.format("#%02X%02X%02X", red, green, blue);
                    }).build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.send"), config.DESTSend)
                    .setDefaultValue(config.defaults.DESTSend)
                    .setSaveConsumer(option -> config.DESTSend = option)
                    .build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.track"), config.DESTTrack)
                    .setDefaultValue(config.defaults.DESTTrack)
                    .setSaveConsumer(option -> config.DESTTrack = option)
                    .build());
            destCategory.addEntry(entryBuilder.startBooleanToggle(CUtl.tLang("dest.setting.lastdeath"), config.DESTLastdeath)
                    .setDefaultValue(config.defaults.DESTLastdeath)
                    .setSaveConsumer(option -> config.DESTLastdeath = option)
                    .build());
            return builder.setSavingRunnable(() -> {
                try {
                    config.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                config.load();
            }).build();
        };
    }
}
