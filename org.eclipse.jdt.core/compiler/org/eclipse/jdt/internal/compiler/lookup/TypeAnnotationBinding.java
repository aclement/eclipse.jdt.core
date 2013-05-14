package org.eclipse.jdt.internal.compiler.lookup;


public class TypeAnnotationBinding {
	AnnotationBinding annotation;
	
	private int targetType;
	
	private int[] typePath;
	
	private int info1;
	
	private int info2;
	
	
	public TypeAnnotationBinding(int targetType, int[] typePath, int info1, int info2,
			AnnotationBinding annotation) {
		this.targetType = targetType;
		this.typePath = typePath;
		this.info1 = info1;
		this.info2 = info2;
		this.annotation = annotation;
	}

	public AnnotationBinding getAnnotation() {
		return this.annotation;
	}

	public int[] getTypePath() {
		return this.typePath;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("TAB: ").append(annotation.toString());
		buf.append(" targetType=").append(this.targetType);
		buf.append(" ").append(info1).append(":").append(info2);
		if (this.typePath != null && this.typePath.length != 0) {
			buf.append(" typePath=");
			for (int i = 0, max = this.typePath.length; i < max; i += 2) {
				if (i > 0) {
					buf.append(' ');
				}
				buf.append('[').append(this.typePath[i]).append(':').append(typePath[i+1]).append(']');
			}
		}
		return buf.toString();
	}
}
