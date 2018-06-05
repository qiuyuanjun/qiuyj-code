package com.qiuyj.commons.validate.annotation;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
public class CompositeAnnotationInstance extends AnnotationInstance {

  private List<Annotation> annotationList;

  private Annotation compositeAnnotation;

  public CompositeAnnotationInstance(List<Annotation> compositeAnnotation) {
    this.annotationList = compositeAnnotation;
  }

  public CompositeAnnotationInstance(Annotation annotation) {
    this.annotationList = Collections.singletonList(annotation);
  }

  @Override
  public Annotation getAnnotation() {
    if (Objects.isNull(compositeAnnotation)) {
      compositeAnnotation = createCompositeAnnotation();
    }
    return compositeAnnotation;
  }

  private CompositeAnnotationImpl createCompositeAnnotation() {
    return new CompositeAnnotationImpl(annotationList);
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface CompositeAnnotation {
  }

  public static class CompositeAnnotationImpl implements CompositeAnnotation {

    private final List<Annotation> annotationList;

    private CompositeAnnotationImpl(List<Annotation> annotationList) {
      this.annotationList = annotationList;
    }

    @Override
    public Class<CompositeAnnotation> annotationType() {
      return CompositeAnnotation.class;
    }

    public List<Annotation> compositeAnnotation() {
      return annotationList;
    }
  }

}
