package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import com.github.allinkdev.deviousmod.api.experiments.Experimental;
import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.event.render.glyph.PreGlyphRenderEvent;
import me.allinkdev.deviousmod.event.render.particle.PreParticleBatchRenderEvent;
import me.allinkdev.deviousmod.event.render.text.ObfuscatedGlyphRendererSelectEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.module.DModuleSettings;

@Experimental(value = "Recently added. Use with caution.", hide = false)
public class NoRenderModule extends DModule {
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
    protected DModuleSettings.Builder getSettingsBuilder() {
        return super.getSettingsBuilder()
                .addField("entities", "No Entities", "Disables entity rendering.", false)
                .addField("particles", "No Particles", "Disables particle rendering.", true)
                .addField("blocks", "No Block Entities", "Disables block entity rendering.", true)
                .addField("glyphs", "No Glyphs", "Disables glyph rendering.", false)
                .addField("uncullable", "No Uncullable Block Entities", "Disables rendering of uncullable block entities.", false)
                .addField("laggy", "No Laggy Blocks", "Do not render known-laggy blocks.", true)
                .addField("obfuscation", "No Obfuscation", "Prevents obfuscated glyph rendering.", true);
    }

    private void cancelIfNecessary(final String settingName, final Cancellable cancellableEvent) {
        if (this.settings.getSetting(settingName, Boolean.class).getValue()) {
            cancellableEvent.cancel();
        }
    }

    @Subscribe
    private void onPreEntitiesRender(final EntityRenderPipelineEvent event) {
        this.cancelIfNecessary("entities", event);
    }

    @Subscribe
    private void onPreParticleBatchRender(final PreParticleBatchRenderEvent event) {
        this.cancelIfNecessary("particles", event);
    }

    @Subscribe
    public void onPreBlockEntityRender(final PreBlockEntityRenderEvent event) {
        this.cancelIfNecessary("blocks", event);
    }

    @Subscribe
    public void onPreUncullableBlockEntityRender(final PreUncullableBlockEntityRenderEvent event) {
        this.cancelIfNecessary("uncullable", event);
    }

    @Subscribe
    public void onPreGlyphRender(final PreGlyphRenderEvent event) {
        this.cancelIfNecessary("glyphs", event);
    }

    @Subscribe
    public void onGlyphRendererSelect(final ObfuscatedGlyphRendererSelectEvent event) {
        this.cancelIfNecessary("obfuscation", event);
    }
}
