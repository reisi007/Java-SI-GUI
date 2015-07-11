package at.reisisoft.sigui.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates, that this method / construcor runs on the UI thread and needs to be called via Platform.runLater
 *
 * @author Florian Reisinger
 * @since 11.07.2015
 */
@Target(ElementType.METHOD)
@Documented
public @interface RunsOnJavaFXThread {
}
