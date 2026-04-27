package org.ptcc.internals.Rules.EJObjects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.ptcc.internals.Collections.Violation;
import org.ptcc.internals.Rule;

import javax.lang.model.type.TypeMirror;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Work In Progress,
 * Rule cannot be created with pure AST searching, check Checker Framework for more information
 * @see <a href="https://checkerframework.org/api/org/checkerframework/framework/type/TypeHierarchy.html">
 */
public class EJRule6 implements Rule {
    private AnnotatedTypeMirror mirror;

    public EJRule6 (AnnotatedTypeMirror mirror) {
        this.mirror = mirror;
    }

    @Override
    public void check(Node node, List<Violation> violations) {
        List<FieldDeclaration> fields = node.findAll(FieldDeclaration.class);
        fields.stream()
                .forEach(f -> System.out.println());

        }
    }
    //public boolean isWrapperType() {
        //TypeMirror type = mirror.getUnderlyingType();


// Avoid creating unnecessary objects, ex:
// String name = new String("Anden");
/*
boolean primitives = fields.stream()
                        .anyMatch(f -> {
                            Class<?> type = f.getClass();
                            return type.equals(Integer.class) ||
                                    type.equals(Byte.class) ||
                                    type.equals(Double.class) ||
                                    type.equals(Boolean.class) ||
                                    type.equals(BigDecimal.class) ||
                                    type.equals(Character.class) ||
                                    type.equals(Float.class) ||
                                    type.equals(Long.class);
                        });
 */