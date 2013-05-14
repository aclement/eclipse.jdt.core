package org.eclipse.jdt.internal.compiler.lookup;



//int TYPE_PATH_DEEPER_IN_ARRAY = 0;
//int TYPE_PATH_DEEPER_IN_INNER_TYPE = 1;
//int TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND = 2;
//int TYPE_PATH_TYPE_ARGUMENT_INDEX = 3;


// A type annotation context hides the complexities of navigating a type path from the consumer
public class AnnotatedTypeCarrier {
	TypeBinding type;
	int[] typePathForThisType;
	AnnotationBinding[] annotations;
	AnnotationBinding[][] annotationsOnDimensions;
	// These will be a mishmash of typepaths
	TypeAnnotationBinding[] remainingTypeAnnotations;
	private int remainingTypeAnnotationsCount;
	private int typedepth; 

	private int depth; // depth into type path

	/**
	 * @return true if the supplied typeAnnotation type path starts with the specified matchTypePath
	 */
	private static boolean doesTypePathMatch(TypeAnnotationBinding typeAnnotation, int[] matchTypePath) {
		int[] typePath = typeAnnotation.getTypePath();
		int len = typePath.length;
		int matchlen = matchTypePath.length;
		if (matchlen > len) {
			return false;
		}
		for (int i = 0; i < matchlen; i++) {
			if (matchTypePath[i] != typePath[i]) {
				return false;
			}
		}
		return true;
	}
	
	private static void subsetTypeAnnotationsMatchingTypePath(TypeAnnotationBinding[] typeAnnotations, int[] matchTypePath, boolean[] valid) {
		for (int i = 0, max = typeAnnotations.length; i < max; i++) {
			valid[i] = doesTypePathMatch(typeAnnotations[i], matchTypePath);
		}		
	}
	
	public static AnnotatedTypeCarrier getAnnotatedTypeCarrier(TypeBinding type, TypeAnnotationBinding[] typeAnnotations) {		
		int typedepth = 0;
		ReferenceBinding enclosingType = type.enclosingType();
		while (enclosingType != null) { typedepth++; enclosingType = enclosingType.enclosingType(); }

		int[] typePathForThisType = new int[typedepth*2];
		for (int i = 0; i < typedepth; i++) {
			typePathForThisType[i*2] = 1;
		}

		// Type annotations that apply to us must have a type path with enough entries to match the typedepth
		int typeAnnotationsLength = typeAnnotations.length;
		System.out.println("typedepth = "+typedepth+" typeAnnotationsLength="+typeAnnotationsLength);
		boolean[] validAtThisLevel = new boolean[typeAnnotationsLength];
		subsetTypeAnnotationsMatchingTypePath(typeAnnotations, typePathForThisType, validAtThisLevel);
		
		System.out.println("Dump");
		for (int i = 0; i < typeAnnotationsLength; i++) {
			System.out.println(typeAnnotations[i] + " is valid?"+validAtThisLevel[i]);
		}
		
		int typePathIndex = typePathForThisType.length;
				
		TypeAnnotationBinding[] remainingTypeAnnotations = new TypeAnnotationBinding[typeAnnotationsLength];
		System.arraycopy(typeAnnotations, 0, remainingTypeAnnotations, 0, typeAnnotationsLength);
		int remainingTypeAnnotationsCount = typeAnnotationsLength;
		AnnotationBinding[] annotations = null;
		AnnotationBinding[][] annotationsOnDimensions = null;
		if (type.isArrayType()) {
			// Type path will tell us if they are annotations on dimensions or on the actual type
			for (int i = 0, max = typeAnnotationsLength; i < max; i++) {
				TypeAnnotationBinding typeAnnotation = typeAnnotations[i];
				int[] typepath = typeAnnotation.getTypePath();
				int typepathLength = typepath.length;
				
				// If the type path for the annotation is the same as our type path length, it is an annotation on
				// the first dimension
				if (typePathIndex == typepathLength) {
					// this is on the first dimension
					if (annotationsOnDimensions == null) {
						annotationsOnDimensions = new AnnotationBinding[type.dimensions()][];
					}
					if (annotationsOnDimensions[0] == null) {
						annotationsOnDimensions[0] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
					} else {
						AnnotationBinding[] tmp = new AnnotationBinding[annotationsOnDimensions[0].length+1];
						System.arraycopy(annotationsOnDimensions[0], 0, tmp, 0, annotationsOnDimensions[0].length);
						tmp[annotationsOnDimensions[0].length-1] = typeAnnotation.getAnnotation();
					}
				} else {
					boolean affectsAtThisTypeLevel = true;
					int depthInThisArrayType = 0;
					for (int j = 0, maxj = typepathLength; j < maxj; j += 2) {
						if (typepath[j] == 0) {
							depthInThisArrayType++;
						} else {
							affectsAtThisTypeLevel = false;
						}
					}
					if (affectsAtThisTypeLevel) {
						if (depthInThisArrayType == type.dimensions()) {
							// it is the primary set of annotations
							if (annotations == null) {
								annotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
							} else {
								AnnotationBinding[] tmp = new AnnotationBinding[annotations.length+1];
								System.arraycopy(annotations, 0, tmp, 0, annotations.length);
								tmp[annotations.length-1] = typeAnnotation.getAnnotation();
							}
						} else {
							// it is annotations on dimensions
							if (annotationsOnDimensions == null) {
								annotationsOnDimensions = new AnnotationBinding[type.dimensions()][];
							}
							if (annotationsOnDimensions[depthInThisArrayType] == null) {
								annotationsOnDimensions[depthInThisArrayType] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
							} else {
								AnnotationBinding[] tmp = new AnnotationBinding[annotationsOnDimensions[depthInThisArrayType].length+1];
								System.arraycopy(annotationsOnDimensions[depthInThisArrayType], 0, tmp, 0, annotationsOnDimensions[depthInThisArrayType].length);
								tmp[annotationsOnDimensions[depthInThisArrayType].length-1] = typeAnnotation.getAnnotation();
							}
						}
					}
				}
			}
			
		} else {
			for (int i = 0, max = typeAnnotationsLength; i < max; i++) {
				TypeAnnotationBinding typeAnnotation = typeAnnotations[i];
				int[] typepath = typeAnnotation.getTypePath();
				if (typepath == null || typepath.length == 0) {
					if (annotations == null) {
						annotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
						
						// TODO Let's create a separate array for the remainders rather than hacking the first one to bits
						typeAnnotations[i] = null; // this one has been served up, can ignore
						remainingTypeAnnotationsCount--;
					} else {
						// expand array
					}
				}
			}
		}
		return new AnnotatedTypeCarrier(type, typedepth, typePathForThisType, annotations, annotationsOnDimensions, remainingTypeAnnotations, remainingTypeAnnotationsCount, 0);
	}
	
	private AnnotatedTypeCarrier(TypeBinding type, int typedepth, int[] typePathForThisType, AnnotationBinding[] annotations, AnnotationBinding[][] annotationsOnDimensions, TypeAnnotationBinding[] remainingTypeAnnotations, int remainingTypeAnnotationsCount, int depth) {
		this.type = type;
		this.typePathForThisType = typePathForThisType;
		this.annotations = annotations;
		this.annotationsOnDimensions = annotationsOnDimensions;
		this.remainingTypeAnnotations = remainingTypeAnnotations;
		this.remainingTypeAnnotationsCount = remainingTypeAnnotationsCount;
		this.depth = depth;
		this.typedepth = typedepth;
	}
	
	public AnnotatedTypeCarrier getAnnotatedTypeParameter(int typeParameterIndex) {
		if (!(this.type instanceof ParameterizedTypeBinding)) {
			return null;
		}
		// TODO [ASC] cache computed results in case asked again?
		ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)this.type;
		if (typeParameterIndex >= ptb.arguments.length) {
			return null;
		}
		TypeBinding newType = ptb.arguments[typeParameterIndex];
		AnnotationBinding[] newRelevantAnnotations = null;
		AnnotationBinding[][] newAnnotationsOnDimensions = null;
		TypeAnnotationBinding[] newRemainingTypeAnnotations = new TypeAnnotationBinding[this.remainingTypeAnnotations.length];
		System.arraycopy(this.remainingTypeAnnotations, 0, newRemainingTypeAnnotations, 0, this.remainingTypeAnnotations.length);
		int newRemainingTypeAnnotationsCount = this.remainingTypeAnnotationsCount;
		
		for (int i = 0, max = newRemainingTypeAnnotations.length; i < max; i++) {
			TypeAnnotationBinding typeAnnotation = newRemainingTypeAnnotations[i];
			if (typeAnnotation != null) {
				int[] typepath = typeAnnotation.getTypePath();
				// To be relevant on the specified type parameter it must be [3,typeParameterIndex] next, if it is *at least* that then we need to keep it around
				boolean affectsAtThisTypeLevel = true;
				if (typepath.length >= this.depth && typepath[this.depth]==3 && typepath[this.depth+1]==typeParameterIndex) {
					if (newType.isArrayType()) {
						if (typepath.length == this.depth + 2) {
							// this is on the first dimension
							if (newAnnotationsOnDimensions == null) {
								newAnnotationsOnDimensions = new AnnotationBinding[newType.dimensions()][];
							}
							if (newAnnotationsOnDimensions[0] == null) {
								newAnnotationsOnDimensions[0] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
							} else {
								AnnotationBinding[] arraySegment = newAnnotationsOnDimensions[0];
								AnnotationBinding[] tmp = new AnnotationBinding[arraySegment.length+1];
								System.arraycopy(arraySegment, 0, tmp, 0, arraySegment.length);
								tmp[arraySegment.length-1] = typeAnnotation.getAnnotation();
								newAnnotationsOnDimensions[0] = tmp;
							}
						} else {
							int depthInThisArrayType = 0;
							for (int j = this.depth + 2, maxj = typepath.length; j < maxj; j += 2) {
								if (typepath[j] == 0) {
									depthInThisArrayType++;
								} else {
									affectsAtThisTypeLevel = false;
								}
							}
							if (affectsAtThisTypeLevel) {
								if (depthInThisArrayType == newType.dimensions()) {
									// it is the primary set of annotations
									if (newRelevantAnnotations == null) {
										newRelevantAnnotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
									} else {
										AnnotationBinding[] tmp = new AnnotationBinding[newRelevantAnnotations.length+1];
										System.arraycopy(newRelevantAnnotations, 0, tmp, 0, newRelevantAnnotations.length);
										tmp[newRelevantAnnotations.length-1] = typeAnnotation.getAnnotation();
										newRelevantAnnotations = tmp;
									}
								} else {
									// it is annotations on dimensions
									if (newAnnotationsOnDimensions == null) {
										newAnnotationsOnDimensions = new AnnotationBinding[newType.dimensions()][];
									}
									if (newAnnotationsOnDimensions[depthInThisArrayType] == null) {
										newAnnotationsOnDimensions[depthInThisArrayType] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
									} else {
										AnnotationBinding[] tmp = new AnnotationBinding[newAnnotationsOnDimensions[depthInThisArrayType].length+1];
										System.arraycopy(newAnnotationsOnDimensions[depthInThisArrayType], 0, tmp, 0, newAnnotationsOnDimensions[depthInThisArrayType].length);
										tmp[newAnnotationsOnDimensions[depthInThisArrayType].length-1] = typeAnnotation.getAnnotation();
										newAnnotationsOnDimensions[depthInThisArrayType] = tmp;
									}
								}
							}
						}					
					} else if (typepath.length == this.depth + 2) {
						if (newRelevantAnnotations == null) {
							newRelevantAnnotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
						} else {
							AnnotationBinding[] tmp = new AnnotationBinding[newRelevantAnnotations.length+1];
							System.arraycopy(newRelevantAnnotations, 0, tmp, 0, newRelevantAnnotations.length);
							newRelevantAnnotations[newRelevantAnnotations.length-1] = typeAnnotation.getAnnotation();
						}
					}
				}
				// If that is the end of the typepath then we're done
				if (this.depth + 2 == typepath.length || affectsAtThisTypeLevel) {
					newRemainingTypeAnnotations[i] = null; // this one has been served up, can ignore
					newRemainingTypeAnnotationsCount--;
				}
			}
		}
		return new AnnotatedTypeCarrier(newType, -1, null, newRelevantAnnotations, newAnnotationsOnDimensions, newRemainingTypeAnnotations, newRemainingTypeAnnotationsCount, this.depth+2);
	}
	
	AnnotationBinding[][] getAnnotationsOnDimensions() {
		return null;
	}
	
	AnnotationBinding[] getAnnotations() {
		return this.annotations;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("type:").append(this.type.debugName()); //$NON-NLS-1$
		buffer.append(" annotations: "); //$NON-NLS-1$
		if (this.annotations == null) {
			buffer.append("none"); //$NON-NLS-1$
		} else {
			for (int i = 0, max = this.annotations.length; i < max; i++) {
				if (i > 0) {
					buffer.append(' ');
				}
				buffer.append(this.annotations[i]);
			}
		}
		if (this.annotationsOnDimensions != null) {
			buffer.append(" annotationsOnDimensions:"); //$NON-NLS-1$
			for (int i = 0, max = this.annotationsOnDimensions.length; i < max; i++) {
				buffer.append('[');
				AnnotationBinding[] annotationsOnDims = this.annotationsOnDimensions[i];
				if (annotationsOnDims != null) {
					for (int j = 0, maxj = annotationsOnDims.length; j < maxj; j++) {
						if (j > 0) {
							buffer.append(' ');
						}
						buffer.append(annotationsOnDims[j]);
					}
				}
				buffer.append(']');
			}
		}
		if (this.remainingTypeAnnotationsCount != 0) {
			buffer.append(" remainingTypeAnnotations: "); //$NON-NLS-1$
			for (int i = 0, max = this.remainingTypeAnnotations.length; i < max; i++) {
				if (this.remainingTypeAnnotations[i] == null) continue;
				if (i > 0) {
					buffer.append(' ');
				}
				buffer.append(this.remainingTypeAnnotations[i]);
			}
		}
		return buffer.toString();
	}

	public TypeBinding getType() {
		return this.type;
	}
	
	public AnnotatedTypeCarrier getAnnotatedEnclosingType() {
		if (this.typedepth == 0) {
			return null;
		}
		int newtypedepth = this.typedepth - 1;
		ReferenceBinding enclosingType = this.type.enclosingType();
		// assert enclosingType != null
		ReferenceBinding newType = type.enclosingType();

		// Compute typepath for this level
		int[] newTypePathForThisType = new int[this.typePathForThisType.length-2];
		System.arraycopy(this.typePathForThisType, 0, newTypePathForThisType, 0, this.typePathForThisType.length-2);

		int typeAnnotationsLength = this.remainingTypeAnnotations.length;
		boolean[] subset = new boolean[typeAnnotationsLength];
		subsetTypeAnnotationsMatchingTypePath(this.remainingTypeAnnotations, newTypePathForThisType, subset);
		
		AnnotationBinding[] newRelevantAnnotations = null;
		AnnotationBinding[][] newAnnotationsOnDimensions = null;
		
		// need to sort out the array related ones
		for (int i = 0, max = this.remainingTypeAnnotations.length; i < max; i++) {
			if (!subset[i]) {
				continue;
			}
			TypeAnnotationBinding typeAnnotation = this.remainingTypeAnnotations[i];
			if (typeAnnotation != null) {
				int[] typepath = typeAnnotation.getTypePath();
				// To be relevant on the specified type parameter it must be [3,typeParameterIndex] next, if it is *at least* that then we need to keep it around
				boolean affectsAtThisTypeLevel = true;
				if (typepath.length >= this.depth && typepath[this.depth]==1) {
					if (newType.isArrayType()) {
						if (typepath.length == this.depth + 2) {
							// this is on the first dimension
							if (newAnnotationsOnDimensions == null) {
								newAnnotationsOnDimensions = new AnnotationBinding[newType.dimensions()][];
							}
							if (newAnnotationsOnDimensions[0] == null) {
								newAnnotationsOnDimensions[0] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
							} else {
								AnnotationBinding[] arraySegment = newAnnotationsOnDimensions[0];
								AnnotationBinding[] tmp = new AnnotationBinding[arraySegment.length+1];
								System.arraycopy(arraySegment, 0, tmp, 0, arraySegment.length);
								tmp[arraySegment.length-1] = typeAnnotation.getAnnotation();
								newAnnotationsOnDimensions[0] = tmp;
							}
						} else {
							int depthInThisArrayType = 0;
							for (int j = this.depth + 2, maxj = typepath.length; j < maxj; j += 2) {
								if (typepath[j] == 0) {
									depthInThisArrayType++;
								} else {
									affectsAtThisTypeLevel = false;
								}
							}
							if (affectsAtThisTypeLevel) {
								if (depthInThisArrayType == newType.dimensions()) {
									// it is the primary set of annotations
									if (newRelevantAnnotations == null) {
										newRelevantAnnotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
									} else {
										AnnotationBinding[] tmp = new AnnotationBinding[newRelevantAnnotations.length+1];
										System.arraycopy(newRelevantAnnotations, 0, tmp, 0, newRelevantAnnotations.length);
										tmp[newRelevantAnnotations.length-1] = typeAnnotation.getAnnotation();
										newRelevantAnnotations = tmp;
									}
								} else {
									// it is annotations on dimensions
									if (newAnnotationsOnDimensions == null) {
										newAnnotationsOnDimensions = new AnnotationBinding[newType.dimensions()][];
									}
									if (newAnnotationsOnDimensions[depthInThisArrayType] == null) {
										newAnnotationsOnDimensions[depthInThisArrayType] = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
									} else {
										AnnotationBinding[] tmp = new AnnotationBinding[newAnnotationsOnDimensions[depthInThisArrayType].length+1];
										System.arraycopy(newAnnotationsOnDimensions[depthInThisArrayType], 0, tmp, 0, newAnnotationsOnDimensions[depthInThisArrayType].length);
										tmp[newAnnotationsOnDimensions[depthInThisArrayType].length-1] = typeAnnotation.getAnnotation();
										newAnnotationsOnDimensions[depthInThisArrayType] = tmp;
									}
								}
							}
						}					
					} else if (typepath.length == this.depth + 2) {
						if (newRelevantAnnotations == null) {
							newRelevantAnnotations = new AnnotationBinding[]{typeAnnotation.getAnnotation()};
						} else {
							AnnotationBinding[] tmp = new AnnotationBinding[newRelevantAnnotations.length+1];
							System.arraycopy(newRelevantAnnotations, 0, tmp, 0, newRelevantAnnotations.length);
							newRelevantAnnotations[newRelevantAnnotations.length-1] = typeAnnotation.getAnnotation();
						}
					}
				}
				// If that is the end of the typepath then we're done
//				if (this.depth + 2 == typepath.length || affectsAtThisTypeLevel) {
//					newRemainingTypeAnnotations[i] = null; // this one has been served up, can ignore
//					newRemainingTypeAnnotationsCount--;
//				}
			}
		}
			
		
		return new AnnotatedTypeCarrier(newType, this.typedepth, newTypePathForThisType, newRelevantAnnotations, newAnnotationsOnDimensions, this.remainingTypeAnnotations, this.remainingTypeAnnotationsCount, this.depth+2);
	}

}