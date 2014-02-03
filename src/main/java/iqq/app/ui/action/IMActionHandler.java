package iqq.app.ui.action;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-4-1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IMActionHandler {
	String name() default "";

	String enabledProperty() default "";

	String selectedProperty() default "";

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface Parameter {
		String value() default "";
	}
}
