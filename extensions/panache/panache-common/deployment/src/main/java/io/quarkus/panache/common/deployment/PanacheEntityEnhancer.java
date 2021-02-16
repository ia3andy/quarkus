package io.quarkus.panache.common.deployment;

import java.util.List;
import java.util.function.BiFunction;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.objectweb.asm.ClassVisitor;

public abstract class PanacheEntityEnhancer
        implements BiFunction<String, ClassVisitor, ClassVisitor> {

    protected MetamodelInfo modelInfo;
    protected final IndexView indexView;
    protected final List<PanacheMethodCustomizer> methodCustomizers;

    public PanacheEntityEnhancer(IndexView index, List<PanacheMethodCustomizer> methodCustomizers) {
        this.indexView = index;
        this.methodCustomizers = methodCustomizers;
    }

    @Override
    public abstract ClassVisitor apply(String className, ClassVisitor outputClassVisitor);

    public abstract void collectFields(ClassInfo classInfo);

    public MetamodelInfo getModelInfo() {
        return modelInfo;
    }
}
