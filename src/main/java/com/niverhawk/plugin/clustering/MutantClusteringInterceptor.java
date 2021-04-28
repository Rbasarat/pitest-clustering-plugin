package com.niverhawk.plugin.clustering;

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

            int distance = calculateLevenshteinDistance(getMethodNameInByteCode(md), this.OriginalClassBytesAsString, getByteCodeAsString(mutant.getBytes()));

            long endTime = System.currentTimeMillis();
            long duration = ((endTime - startTime));

            if (distance == 0) {
                System.err.println("ERROR: distance is 0");
            } else if (distance > 100) {
                System.out.println("Bingo!");
            }

            System.out.println("duration: " + duration + " distancce: " + distance);
            this.TotalTime += duration;
        }

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
                buffer)), ClassReader.EXPAND_FRAMES);
        return buffer.toString();

    }

    private Integer calculateLevenshteinDistance(String methodName, String originalClass, String mutantClass) {
        String originalMethod = getMethodInByteCode(originalClass, methodName);
        String mutantMethod = getMethodInByteCode(mutantClass, methodName);
        if (originalMethod.equals("") || mutantMethod.equals("")) return 0;
        LevenshteinDistance distance = new LevenshteinDistance();
        return distance.apply(originalMethod, mutantMethod);
    }

    private String getMethodNameInByteCode(MutationDetails mutantDetails) {
        return mutantDetails.getId().getLocation().getMethodName().name() + mutantDetails.getId().getLocation().getMethodDesc();
    }

    private String getMethodInByteCode(String clazz, String methodName) {
        StringBuffer buf = new StringBuffer(clazz);
        int start = buf.indexOf(methodName);
        int end = buf.indexOf("\n\n", start);
        if (start != -1 && end != -1) {
            return buf.substring(start, end);
        }
        return "";
    }
}
