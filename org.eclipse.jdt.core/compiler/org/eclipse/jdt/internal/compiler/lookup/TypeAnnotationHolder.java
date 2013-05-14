/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

public class TypeAnnotationHolder {
	TypeAnnotationBinding[] typeAnnotations;
	
static TypeAnnotationHolder storeAnnotations(TypeAnnotationBinding[] typeAnnotations) {
	return new TypeAnnotationHolder().setTypeAnnotations(typeAnnotations);
}

//static TypeAnnotationHolder storeAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
//	if (parameterAnnotations != null) {
//		boolean isEmpty = true;
//		for (int i = parameterAnnotations.length; isEmpty && --i >= 0;)
//			if (parameterAnnotations[i] != null && parameterAnnotations[i].length > 0)
//				isEmpty = false;
//		if (isEmpty)
//			parameterAnnotations = null; // does not have any
//	}
//
//	if (defaultValue != null)
//		return new TypeAnnotationMethodHolder(annotations, parameterAnnotations, defaultValue, optionalEnv);
//	if (parameterAnnotations != null)
//		return new MethodHolder(annotations, parameterAnnotations);
//	return new TypeAnnotationHolder().setAnnotations(annotations);
//}

TypeAnnotationBinding[] getTypeAnnotations() {
	return this.typeAnnotations;
}
//Object getDefaultValue() {
//	return null;
//}
//public AnnotationBinding[][] getParameterAnnotations() {
//	return null;
//}
//AnnotationBinding[] getParameterAnnotations(int paramIndex) {
//	return Binding.NO_ANNOTATIONS;
//}
TypeAnnotationHolder setTypeAnnotations(TypeAnnotationBinding[] typeAnnotations) {
	if (typeAnnotations == null || typeAnnotations.length == 0)
		return null; // no longer needed

	this.typeAnnotations = typeAnnotations;
	return this;
}
//
//static class MethodHolder extends TypeAnnotationHolder {
//	AnnotationBinding[][] parameterAnnotations; // is null if empty
//
//MethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations) {
//	super();
//	setAnnotations(annotations);
//	this.parameterAnnotations = parameterAnnotations;
//}
//public AnnotationBinding[][] getParameterAnnotations() {
//	return this.parameterAnnotations;
//}
//AnnotationBinding[] getParameterAnnotations(int paramIndex) {
//	AnnotationBinding[] result = this.parameterAnnotations == null ? null : this.parameterAnnotations[paramIndex];
//	return result == null ? Binding.NO_ANNOTATIONS : result;
//}
//TypeAnnotationHolder setAnnotations(AnnotationBinding[] annotations) {
//	this.annotations = annotations == null || annotations.length == 0 ? Binding.NO_ANNOTATIONS : annotations;
//	return this;
//}
//}
//
//static class AnnotationMethodHolder extends MethodHolder {
//	Object defaultValue;
//	LookupEnvironment env;
//
//AnnotationMethodHolder(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
//	super(annotations, parameterAnnotations);
//	this.defaultValue = defaultValue;
//	this.env = optionalEnv;
//}
//Object getDefaultValue() {
//	if (this.defaultValue instanceof UnresolvedReferenceBinding) {
//		if (this.env == null)
//			throw new IllegalStateException();
//		this.defaultValue = ((UnresolvedReferenceBinding) this.defaultValue).resolve(this.env, false);
//	}
//	return this.defaultValue;
//}
//}

public TypeAnnotationBinding[] getTypeAnnotationsOnReturnType() {
	return null;
}
}
