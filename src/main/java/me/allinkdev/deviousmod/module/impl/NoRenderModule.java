package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import me.allinkdev.deviousmod.event.render.block.PreBeaconBeamRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
import me.allinkdev.deviousmod.event.render.glyph.PreGlyphRenderEvent;
import me.allinkdev.deviousmod.event.render.particle.PreParticleBatchRenderEvent;
import me.allinkdev.deviousmod.event.render.text.ObfuscatedGlyphRendererSelectEvent;
import me.allinkdev.deviousmod.event.render.world.PrePlanetaryBodyRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreSkyRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreStarRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreWeatherRenderEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.fabricmc.loader.api.FabricLoader;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.ItemEntity;

public final class NoRenderModule extends DModule {
    public NoRenderModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "NoRender";
    }

    @Override
    public String getDescription() {
        return "Disables rendering of certain game aspects.";
    }

    @Override
    public String getCategory() {
        return "Render";
    }

    @Override
    protected ModuleSettings.Builder getSettingsBuilder() {
        final ModuleSettings.Builder moduleSettings = super.getSettingsBuilder()
                .addField("obfuscation", "No Obfuscation", "Prevents obfuscated glyph rendering.", true)
                .addField("glyphs", "No Glyphs", "Disables glyph rendering.", false)
                .addField("entities", "No Entities", "Disables entity rendering.", false)
                .addField("particles", "No Particles", "Disables particle rendering.", true)
                .addField("blocks", "No Block Entities", "Disables block entity rendering.", true)
                .addField("uncullable", "No Uncullable Block Entities", "Disables rendering of uncullable block entities.", false)
                .addField("cut-out", "No Cut-out Blocks", "Disables rendering of the cut-out entity layer (non-full blocks).", true)
                .addField("cut-out_mipped", "No (Mipped) Cut-out Blocks", "Disables rendering of the mipped cut-out entity layer (grass block, iron bars, etc).", false)
                .addField("tripwire", "No Tripwire Rendering", "Disables rendering of the tripwire layer.", true)
                .addField("glint", "No Glint Rendering", "Disables rendering of the glint layers.", false)
                .addField("translucent", "No Translucent Block Rendering", "Disables rendering of the translucent layer.", false)
                .addField("solids", "No Solid Block Rendering", "Disables rendering of the solid block layer.", false)
                .addField("beacon_beam", "No Beacon Beam Rendering", "Disables rendering of the beacon beam.", true)
                .addField("items", "No Items", "Disables rendering of item entities.", false);

        if (!FabricLoader.getInstance().isModLoaded("sodium-extra")) {
            moduleSettings.addField("weather", "No Weather", "Disables rendering of the weather.", false)
                    .addField("sky", "No Sky", "Disables rendering of the sky.", false)
                    .addField("stars", "No Stars", "Disables rendering of the stars in the sky.", false)
                    .addField("sun_moon", "No Sun/Moon", "Disables rendering of the sun & moon.", false);
        }

        return moduleSettings;
    }

    private boolean cancelIfNecessary(final String settingName, final Cancellable cancellableEvent) {
        if (!this.settingEnabled(settingName)) return false;
        if (this.settings.getSetting(settingName, Boolean.class).getValue()) {
            cancellableEvent.cancel();
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPreEntitiesRender(final EntityRenderPipelineEvent event) {
        final boolean fullyCancelled = this.cancelIfNecessary("entities", event);

        if (fullyCancelled || !this.getSettings().getSetting("items", Boolean.class).getValue()) return;
        event.getEntityList().removeIf(ItemEntity.class::isInstance);
    }

    @EventHandler
    public void onPreParticleBatchRender(final PreParticleBatchRenderEvent event) {
        this.cancelIfNecessary("particles", event);
    }

    @EventHandler
    public void onPreBlockEntityRender(final PreBlockEntityRenderEvent event) {
        this.cancelIfNecessary("blocks", event);
    }

    @EventHandler
    public void onPreUncullableBlockEntityRender(final PreUncullableBlockEntityRenderEvent event) {
        this.cancelIfNecessary("uncullable", event);
    }

    @EventHandler
    public void onPreGlyphRender(final PreGlyphRenderEvent event) {
        this.cancelIfNecessary("glyphs", event);
    }

    @EventHandler
    public void onGlyphRendererSelect(final ObfuscatedGlyphRendererSelectEvent event) {
        this.cancelIfNecessary("obfuscation", event);
    }

    @EventHandler
    public void onBeaconBeamPreRender(final PreBeaconBeamRenderEvent event) {
        this.cancelIfNecessary("beacon_beam", event);
    }

    private boolean settingEnabled(final String key) {
        return this.settings.getKeys().contains(key);
    }

    @EventHandler
    public void onPrePlanetaryBodyRender(final PrePlanetaryBodyRenderEvent event) {
        this.cancelIfNecessary("sun_moon", event);
    }

    @EventHandler
    public void onPreWeatherRender(final PreWeatherRenderEvent event) {
        this.cancelIfNecessary("weather", event);
    }

    @EventHandler
    public void onPreSkyRender(final PreSkyRenderEvent event) {
        this.cancelIfNecessary("sky", event);
    }

    @EventHandler
    public void onStarsPreRender(final PreStarRenderEvent event) {
        this.cancelIfNecessary("stars", event);
    }

    @EventHandler
    public void onRenderLayer(final RenderLayerEvent event) {
        final RenderLayer renderLayer = event.getRenderLayer();

        if (renderLayer.equals(RenderLayer.getCutout())) {
            this.cancelIfNecessary("cut-out", event);
        } else if (renderLayer.equals(RenderLayer.getCutoutMipped())) {
            this.cancelIfNecessary("cut-out_mipped", event);
        } else if (renderLayer.equals(RenderLayer.getTripwire())) {
            this.cancelIfNecessary("tripwire", event);
        } else if (renderLayer.equals(RenderLayer.getTranslucent())
                || renderLayer.equals(RenderLayer.getTranslucentNoCrumbling())
                || renderLayer.equals(RenderLayer.getTranslucentMovingBlock())) {
            this.cancelIfNecessary("translucent", event);
        } else if (renderLayer.equals(RenderLayer.getSolid())) {
            this.cancelIfNecessary("solids", event);
        } else if (renderLayer.equals(RenderLayer.getGlint()) || renderLayer.equals(RenderLayer.getGlintTranslucent())
                || renderLayer.equals(RenderLayer.getArmorGlint()) || renderLayer.equals(RenderLayer.getDirectGlint())
                || renderLayer.equals(RenderLayer.getArmorEntityGlint()) || renderLayer.equals(RenderLayer.getDirectEntityGlint())) {
            this.cancelIfNecessary("glint", event);
        }
    }
}
