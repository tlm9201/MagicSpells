package com.nisovin.magicspells.util;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.intellij.lang.annotations.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Name {

	@Pattern("[a-z0-9_]+")
	String value();

}
