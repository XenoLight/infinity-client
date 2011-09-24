package org.rsbot.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * A ScriptManifest equilavent which allows quick debug script writing. Takes no
 * arguments (uses classname as script name) and puts script to top of script
 * selector meaning faster code testing!
 * 
 * @author Waterwolf
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DevManifest {

}
