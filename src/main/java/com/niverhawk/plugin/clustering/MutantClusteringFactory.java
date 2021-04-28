package com.niverhawk.plugin.clustering.clustering;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

public class MutantClusteringFactory implements MutationInterceptorFactory {


    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters params) {
        return new MutantClusteringInterceptor();
    }

    @Override
    public Feature provides() {
        return Feature.named("CLUSTER")
                .withDescription("Cluster mutants by levenshtein distance")
                .withOnByDefault(false);
    }

    @Override
    public String description() {
        return "Mutant clustering plugin";
    }
}
