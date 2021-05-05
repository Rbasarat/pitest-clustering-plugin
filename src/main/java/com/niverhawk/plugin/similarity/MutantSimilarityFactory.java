package com.niverhawk.plugin.similarity;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

import java.io.FileWriter;
import java.io.IOException;

public class MutantSimilarityFactory implements MutationInterceptorFactory {
    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters params) {
        System.out.println("only one time please.");
        try {
            FileWriter csvWriter = new FileWriter("distance.csv", false);
            csvWriter.append("id");
            csvWriter.append(",");
            csvWriter.append("distance");
            csvWriter.append("\n");
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new MutantSimilarityInterceptor();
    }

    @Override
    public Feature provides() {
        return Feature.named("SIMILARITY")
                .withDescription("Calculates mutants Leventshtein distance from original")
                .withOnByDefault(false);
    }

    @Override
    public String description() {
        return "Mutant similarity plugin";
    }
}

