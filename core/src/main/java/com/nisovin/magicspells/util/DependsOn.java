package com.nisovin.magicspells.util;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Value may be an array of plugin names the annotated class might check
 * to see if they are enabled before allowing further processing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DependsOn {

	String[] value();

}
