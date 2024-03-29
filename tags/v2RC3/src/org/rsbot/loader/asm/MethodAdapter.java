/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2007 INRIA, France Telecom
 * All rights reserved.
 */
package org.rsbot.loader.asm;

/**
 * An empty {@link MethodVisitor} that delegates to another
 * {@link MethodVisitor}. This class can be used as a super class to quickly
 * implement usefull method adapter classes, just by overriding the necessary
 * methods.
 * 
 * @author Eric Bruneton
 */
public class MethodAdapter implements MethodVisitor {

	/**
	 * The {@link MethodVisitor} to which this adapter delegates calls.
	 */
	protected MethodVisitor mv;

	/**
	 * Constructs a new {@link MethodAdapter} object.
	 * 
	 * @param mv
	 *            the code visitor to which this adapter must delegate calls.
	 */
	public MethodAdapter(final MethodVisitor mv) {
		this.mv = mv;
	}

	@Override
	public AnnotationVisitor visitAnnotation(final String desc,
			final boolean visible) {
		return mv.visitAnnotation(desc, visible);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return mv.visitAnnotationDefault();
	}

	@Override
	public void visitAttribute(final Attribute attr) {
		mv.visitAttribute(attr);
	}

	@Override
	public void visitCode() {
		mv.visitCode();
	}

	@Override
	public void visitEnd() {
		mv.visitEnd();
	}

	@Override
	public void visitFieldInsn(final int opcode, final String owner,
			final String name, final String desc) {
		mv.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitFrame(final int type, final int nLocal,
			final Object[] local, final int nStack, final Object[] stack) {
		mv.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitIincInsn(final int var, final int increment) {
		mv.visitIincInsn(var, increment);
	}

	@Override
	public void visitInsn(final int opcode) {
		mv.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		mv.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		mv.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLabel(final Label label) {
		mv.visitLabel(label);
	}

	@Override
	public void visitLdcInsn(final Object cst) {
		mv.visitLdcInsn(cst);
	}

	@Override
	public void visitLineNumber(final int line, final Label start) {
		mv.visitLineNumber(line, start);
	}

	@Override
	public void visitLocalVariable(final String name, final String desc,
			final String signature, final Label start, final Label end,
			final int index) {
		mv.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
			final Label[] labels) {
		mv.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMaxs(final int maxStack, final int maxLocals) {
		mv.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitMultiANewArrayInsn(final String desc, final int dims) {
		mv.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(final int parameter,
			final String desc, final boolean visible) {
		return mv.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public void visitTableSwitchInsn(final int min, final int max,
			final Label dflt, final Label[] labels) {
		mv.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitTryCatchBlock(final Label start, final Label end,
			final Label handler, final String type) {
		mv.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		mv.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitVarInsn(final int opcode, final int var) {
		mv.visitVarInsn(opcode, var);
	}
}
