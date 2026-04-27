package org.ptcc.internals.Rules;

import org.ptcc.internals.Rule;
import org.reflections.Reflections;

import java.util.Set;

public class ImplementationScanner {
    public static Reflections reflections = new Reflections("org.ptcc.internals.Rules");
    public static Set<Class<? extends Rule>> implementations = reflections.getSubTypesOf(Rule.class);

    public static Set<Class<? extends Rule>> getImplementations() {
        return implementations;
    }
    private ImplementationScanner() {}
}
