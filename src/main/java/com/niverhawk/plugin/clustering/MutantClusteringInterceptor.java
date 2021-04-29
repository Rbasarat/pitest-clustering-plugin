package com.niverhawk.plugin.clustering;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MutantClusteringInterceptor implements MutationInterceptor {

    private final ClassByteArraySource source;
    private String OriginalClassBytesAsString;

    long TotalTime = 0;

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
        final List<MutationDetails> indexable = new ArrayList<>(mutations);

        for (int i = 0; i != indexable.size(); i++) {
            final MutationDetails md = indexable.get(i);
            final Mutant mutant = m.getMutation(md.getId());

            long startTime = System.currentTimeMillis();

            final String methodName = getMethodNameInByteCode(md);
            // Remove frame stuff from original class.
            String originalMethod = sanitizeByteCode(getMethodInByteCode(this.OriginalClassBytesAsString, methodName));
            String mutantMethod = getMethodInByteCode(getByteCodeAsString(mutant.getBytes()), methodName);

            // Panick! frames should not be in the mutated bytecode
            if(mutantMethod.contains("FRAME")){
                System.err.println("Found one");
            }

            int distance = calculateLevenshteinDistance(originalMethod, mutantMethod);
            // TODO: write distance and mutant to storage for processing
            long endTime = System.currentTimeMillis();
            long duration = ((endTime - startTime));

            // This should not happen
            if (distance == 0) {
                System.err.println("ERROR: distance is 0");
            }
            if(distance > 500){
                System.err.println("ERROR: bigstance");
            }
            System.out.println("duration: " + duration + " distancce: " + distance);
            this.TotalTime += duration;
        }
        // We do not want to execute mutants at this stage
        mutations.clear();
        return mutations;
    }

    @Override
    public void end() {
        System.out.println("total duration: " + this.TotalTime);
    }

    private String getByteCodeAsString(final byte[] source) {
        final ClassReader reader = new ClassReader(source);
        final CharArrayWriter buffer = new CharArrayWriter();
        reader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(
                buffer)), ClassReader.SKIP_FRAMES);
        return buffer.toString();

    }

    private Integer calculateLevenshteinDistance(String originalMethod, String mutantMethod) {
        if (originalMethod.equals("") || mutantMethod.equals("")) return 0;
        LevenshteinDistance distance = new LevenshteinDistance();
        return distance.apply(originalMethod, mutantMethod);
    }

    private String getMethodNameInByteCode(MutationDetails mutantDetails) {
        return mutantDetails.getId().getLocation().getMethodName().name() + mutantDetails.getId().getLocation().getMethodDesc();
    }

    private String getMethodInByteCode(String clazz, String methodName) {
        StringBuilder buf = new StringBuilder(clazz);
        int start = buf.indexOf(" " + methodName);
        int end = buf.indexOf("\n\n", start);
        if (end == -1) end = clazz.length();
        if (start != -1) {
            return buf.substring(start, end);
        }
        return "";
    }

    private String sanitizeByteCode(String originalMethod) {

        String[] orignalLines = originalMethod.split("\n");
        ArrayList<String> sanitizedLines = new ArrayList<>();
        for (String orignalLine : orignalLines) {
            if (!orignalLine.contains("FRAME ")) sanitizedLines.add(orignalLine);
        }

        return String.join("\n", sanitizedLines);
    }
}
