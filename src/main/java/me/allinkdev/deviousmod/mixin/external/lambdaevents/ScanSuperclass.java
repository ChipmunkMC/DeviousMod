package me.allinkdev.deviousmod.mixin.external.lambdaevents;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.allinkdev.deviousmod.util.ObjectUtil;
import net.lenni0451.lambdaevents.utils.EventUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

@Mixin(value = EventUtils.class, remap = false)
public class ScanSuperclass {
    private static Set<Method> getMethodsClassStackAware(final Class<?> clazz) {
        final Set<Method> methodSet = new ObjectArraySet<>();
        if (clazz == null) return methodSet;
        final Class<?> superClass = clazz.getSuperclass();
        if (!ObjectUtil.nullSafeEquals(clazz, superClass)) methodSet.addAll(getMethodsClassStackAware(superClass));
        methodSet.addAll(Arrays.stream(clazz.getDeclaredMethods()).toList());
        return methodSet;
    }

    @Redirect(method = "getMethods", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getDeclaredMethods()[Ljava/lang/reflect/Method;"))
    private static Method[] getMethods$getDeclaredMethods(final Class<?> clazz) {
        return getMethodsClassStackAware(clazz).toArray(Method[]::new);
    }
}
