package com.niverhawk.plugin.clustering;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;


public class MutantClusteringInterceptor implements MutationInterceptor {

    private final ClassByteArraySource source;
    private String OriginalClassBytesAsString;

    public MutantClusteringInterceptor(ClassByteArraySource source) {
        this.source = source;
    }

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) {
        this.OriginalClassBytesAsString = clazz.toString();

    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
        return mutations;
    }

    @Override
    public void end() {
    }


}
