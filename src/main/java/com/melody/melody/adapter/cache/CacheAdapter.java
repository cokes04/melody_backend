package com.melody.melody.adapter.cache;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CacheAdapter {

  @AliasFor(annotation = Component.class)
  String value() default "";

}
