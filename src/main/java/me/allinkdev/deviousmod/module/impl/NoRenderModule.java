package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import com.github.allinkdev.deviousmod.api.experiments.Experimental;
import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
import me.allinkdev.deviousmod.event.render.glyph.PreGlyphRenderEvent;
import me.allinkdev.deviousmod.event.render.particle.PreParticleBatchRenderEvent;
import me.allinkdev.deviousmod.event.render.text.ObfuscatedGlyphRendererSelectEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.module.DModuleSettings;
import net.minecraft.client.render.RenderLayer;

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
                .addField("cut-out", "No Cut-out Blocks", "Disables rendering of the cut-out entity layer (non-full blocks).", true)
                .addField("cut-out_mipped", "No (Mipped) Cut-out Blocks", "Disables rendering of the mipped cut-out entity layer (grass block, iron bars, etc).", false)
                .addField("tripwire", "No Tripwire Rendering", "Disables rendering of the tripwire layer.", true)
                .addField("glint", "No Glint Rendering", "Disables rendering of the glint layers.", false)
                .addField("translucent", "No Translucent Block Rendering", "Disables rendering of the translucent layer.", false)
                .addField("solids", "No Solid Block Rendering", "Disables rendering of the solid block layer.", false)
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

    @Subscribe
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
