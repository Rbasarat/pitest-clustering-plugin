package com.niverhawk.plugin.clustering;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MutantClusteringFactory implements MutationInterceptorFactory {

    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters params) {

        FileSystem fileSystem = FileSystems.getDefault();
        final String outDir = params.data().getReportDir();
        final Path classDir = fileSystem.getPath(outDir);
        Path clusterDir = classDir.resolve("clustering");
        String file = clusterDir.resolve("cluster.csv").toAbsolutePath().toString();
        Set<String> mutants = parseClusteredMutants(file);

        return new MutantClusteringInterceptor(mutants);
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

    private Set<String> parseClusteredMutants(String filePath) {

        Set<String> result = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] id = line.split(",");
                result.add(id[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
