
package testingtools;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    int priority() default 0; // приоритет выполнения теста (если не указан по-умолчанию получает самый низкий приоритет)
}