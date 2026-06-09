package org.ptcc.internals.Rules;

import org.ptcc.internals.Rule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImplementationScanner {
    private static final Reflections reflections = new Reflections("org.ptcc.internals.Rules");
    private static final Set<Class<? extends Rule>> implementations = reflections.getSubTypesOf(Rule.class);

    public static List<Rule> getRuleInstances() {
        List<Rule> instances = new ArrayList<>();

        for (Class<? extends Rule> ruleClass : implementations) {
            try {

                if (Modifier.isAbstract(ruleClass.getModifiers())) {
                    continue;
                }

                Constructor<?> ctor = ruleClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                instances.add((Rule) ctor.newInstance());
            } catch (Exception e) {
                System.err.println("Failed to instantiate: " + ruleClass.getName());
            }
        }
        return instances;
    }

    private ImplementationScanner() {}
}
