/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper S Moller - Contributions for
 *							Bug 405066 - [1.8][compiler][codegen] Implement code generation infrastructure for JSR335             
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.codegen;

public interface AttributeNamesConstants {
	final char[] SyntheticName = "Synthetic".toCharArray(); //$NON-NLS-1$
	final char[] ConstantValueName = "ConstantValue".toCharArray(); //$NON-NLS-1$
	final char[] LineNumberTableName = "LineNumberTable".toCharArray(); //$NON-NLS-1$
	final char[] LocalVariableTableName = "LocalVariableTable".toCharArray(); //$NON-NLS-1$
	final char[] InnerClassName = "InnerClasses".toCharArray(); //$NON-NLS-1$
	final char[] CodeName = "Code".toCharArray(); //$NON-NLS-1$
	final char[] ExceptionsName = "Exceptions".toCharArray(); //$NON-NLS-1$
	final char[] SourceName = "SourceFile".toCharArray(); //$NON-NLS-1$
	final char[] DeprecatedName = "Deprecated".toCharArray(); //$NON-NLS-1$
	final char[] SignatureName = "Signature".toCharArray(); //$NON-NLS-1$
	final char[] LocalVariableTypeTableName = "LocalVariableTypeTable".toCharArray(); //$NON-NLS-1$
	final char[] EnclosingMethodName = "EnclosingMethod".toCharArray(); //$NON-NLS-1$
	final char[] AnnotationDefaultName = "AnnotationDefault".toCharArray(); //$NON-NLS-1$
	final char[] RuntimeInvisibleAnnotationsName = "RuntimeInvisibleAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] RuntimeVisibleAnnotationsName = "RuntimeVisibleAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] RuntimeInvisibleParameterAnnotationsName = "RuntimeInvisibleParameterAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] RuntimeVisibleParameterAnnotationsName = "RuntimeVisibleParameterAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] StackMapTableName = "StackMapTable".toCharArray(); //$NON-NLS-1$
	final char[] InconsistentHierarchy = "InconsistentHierarchy".toCharArray(); //$NON-NLS-1$
	final char[] VarargsName = "Varargs".toCharArray(); //$NON-NLS-1$
	final char[] StackMapName = "StackMap".toCharArray(); //$NON-NLS-1$
	final char[] MissingTypesName = "MissingTypes".toCharArray(); //$NON-NLS-1$
	// jsr308
	final char[] RuntimeVisibleTypeAnnotationsName = "RuntimeVisibleTypeAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] RuntimeInvisibleTypeAnnotationsName = "RuntimeInvisibleTypeAnnotations".toCharArray(); //$NON-NLS-1$
	final char[] BootstrapMethodsName = "BootstrapMethods".toCharArray(); //$NON-NLS-1$
}
