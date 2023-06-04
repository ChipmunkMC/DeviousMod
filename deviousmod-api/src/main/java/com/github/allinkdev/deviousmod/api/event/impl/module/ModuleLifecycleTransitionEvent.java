package com.github.allinkdev.deviousmod.api.event.impl.module;

import com.github.allinkdev.deviousmod.api.event.impl.LifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;

/**
 * Represents a change in a {@link Module}'s lifecycle.
 */
public interface ModuleLifecycleTransitionEvent extends LifecycleTransitionEvent<ModuleLifecycle, Module> {

}