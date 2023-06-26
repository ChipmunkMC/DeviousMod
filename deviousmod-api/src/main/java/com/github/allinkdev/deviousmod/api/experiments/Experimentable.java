package com.github.allinkdev.deviousmod.api.experiments;

import java.util.Optional;

/**
 * Denotes a class that can have an {@link Experimental} annotation
 */
public interface Experimentable {
    /**
     * @return the experimentality status of the current instance's class
     */
    default Experimentality getExperimentality() {
        final Experimental annotation = this.getClass().getDeclaredAnnotation(Experimental.class);
        if (annotation == null) {
            return Experimentality.NONE;
        }

        return annotation.hide() ? Experimentality.HIDE : Experimentality.WARN;
    }

    /**
     * @return the description of the experiment, empty if there is none/there is no experiment active
     */
    default Optional<String> getExperimentDescription() {
        final Experimental annotation = this.getClass().getDeclaredAnnotation(Experimental.class);

        if (annotation == null) {
            return Optional.empty();
        }

        final String description = annotation.value();
        return description.isEmpty() ? Optional.empty() : Optional.of(description);
    }
}
