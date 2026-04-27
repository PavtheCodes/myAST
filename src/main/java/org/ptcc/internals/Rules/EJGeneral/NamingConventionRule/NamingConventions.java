package org.ptcc.internals.Rules.EJGeneral.NamingConventionRule;

import com.github.javaparser.ast.expr.SimpleName;

import java.util.regex.Pattern;

public enum NamingConventions {
    CAMEL_CASE("^[a-z][a-zA-Z0-9]*$"),
    PASCAL_CASE("^[A-Z][a-zA-Z0-9]*$"),
    UPPER_SNAKE("^[A-Z][A-Z0-9_]*$"),
    SNAKE_CASE("^[a-z][a-z0-9_]*$");

    private final Pattern pattern;

    NamingConventions(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean matches(String name) {
        return pattern.matcher(name).matches();
    }
    static NamingConventions detectConvention(String name) {
        if (PASCAL_CASE.matches(name)) return PASCAL_CASE;
        if (CAMEL_CASE.matches(name)) return CAMEL_CASE;
        if (UPPER_SNAKE.matches(name)) return UPPER_SNAKE;
        if (SNAKE_CASE.matches(name)) return SNAKE_CASE;
        return null; // Unknown convention
    }
}

/*
    Create a NamingConventions Object which you pass in a regex to. Matches method checks if the passed method matches
    the four naming conventions.
 */