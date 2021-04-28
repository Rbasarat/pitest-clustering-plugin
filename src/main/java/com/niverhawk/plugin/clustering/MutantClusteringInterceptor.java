package com.niverhawk.plugin.clustering.clustering;

import jdk.internal.org.jline.utils.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.pitest.bytecode.analysis.ClassTree;
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
        final MutationDetails md = indexable.get(0);
        final Mutant mutant = m.getMutation(md.getId());
        final ClassReader reader = new ClassReader(mutant.getBytes());
        final CharArrayWriter buffer = new CharArrayWriter();
        reader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(
                buffer)), ClassReader.EXPAND_FRAMES);
        String result = buffer.toString();
        Log.info(result);
        return mutations;
    }

    @Override
    public void end() {

    }
}
