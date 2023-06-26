package com.github.allinkdev.deviousmod.api.experiments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks the class as experimental, and hides it in production.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Experimental {
    /**
     * @return the experiment comment
     */
    String value() default "";

    /**
     * @return whether the class' instances should be completely hidden, or just present with a warning
     */
    boolean hide() default true;
}
