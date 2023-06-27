package me.allinkdev.deviousmod.module.impl;

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

    @Subscribe
    private void onPreEntitiesRender(final EntityRenderPipelineEvent event) {
        if (this.settings.getSetting("entities", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    private void onPreParticleBatchRender(final PreParticleBatchRenderEvent event) {
        if (this.settings.getSetting("particles", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onPreBlockEntityRender(final PreBlockEntityRenderEvent event) {
        if (this.settings.getSetting("blocks", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onPreUncullableBlockEntityRender(final PreUncullableBlockEntityRenderEvent event) {
        if (this.settings.getSetting("uncullable", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onPreGlyphRender(final PreGlyphRenderEvent event) {
        if (this.settings.getSetting("glyphs", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onGlyphRendererSelect(final ObfuscatedGlyphRendererSelectEvent event) {
        if (this.settings.getSetting("obfuscation", Boolean.class).getValue()) {
            event.setCancelled(true);
        }
    }
}
