package org.rsbot.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptManifest {
	String[] authors();

	String category() default "Other";

	String description() default "";

	String email() default "";

	String name();

	String notes() default "";

	String summary() default "";

	double version() default 1.0;

	String website() default "";
}
