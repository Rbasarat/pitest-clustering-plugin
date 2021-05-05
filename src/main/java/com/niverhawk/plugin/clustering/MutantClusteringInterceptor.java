package com.niverhawk.plugin.clustering;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class MutantClusteringInterceptor implements MutationInterceptor {

    private final Set<String> mutants;

    public MutantClusteringInterceptor(Set<String> mutants) {
        this.mutants = mutants;
    }

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) {
    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
        final List<MutationDetails> indexable = new ArrayList<>(mutations);

        for (int i = 0; i != indexable.size(); i++) {
            final MutationDetails md = indexable.get(i);
            String details = md.getId().toString().replaceAll(",", "");
            if (!this.mutants.contains(details)) {
                mutants.remove(md);
            }else{
                System.out.println("found one");
            }
        }


        return mutations;
    }

    @Override
    public void end() {
    }


}
