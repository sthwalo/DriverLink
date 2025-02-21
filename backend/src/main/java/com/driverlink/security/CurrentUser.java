package com.driverlink.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.lang.annotation.*;

/**
 * Annotation to get the current authenticated user's ID.
 * This annotation can be used on controller method parameters to inject the current user's ID.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : id")
public @interface CurrentUser {
}
