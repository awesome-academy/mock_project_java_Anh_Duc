package asterisk.sun.booking_tours.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(asterisk.sun.booking_tours.common.aspect.Loggable)")
    public void loggableMethodPointcut() {
    }

    @Pointcut("execution(* asterisk.sun.booking_tours.admin.controllers..*.*(..))")
    public void controllerMethodPointcut() {
    }

    @Pointcut("execution(* asterisk.sun.booking_tours.admin.services..*.*(..))")
    public void serviceMethodPointcut() {
    }

    @Around("loggableMethodPointcut()")
    public Object logAroundLoggableMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Loggable loggable = method.getAnnotation(Loggable.class);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.info("==> Entering method: {}.{}", className, methodName);

        if (loggable.logParams() && joinPoint.getArgs().length > 0) {
            logger.info("    Parameters: {}", Arrays.toString(joinPoint.getArgs()));
        }

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();

            if (loggable.logExecutionTime()) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("    Execution time: {} ms", executionTime);
            }

            if (loggable.logResult()) {
                logger.info("    Result: {}", result);
            }

            logger.info("<== Exiting method: {}.{}", className, methodName);

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("!!! Exception in method: {}.{} after {} ms", className, methodName, executionTime);
            logger.error("    Exception: {}", throwable.getMessage());
            throw throwable;
        }
    }

    @Around("controllerMethodPointcut()")
    public Object logAroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.info(">>> Controller - {}.{} - Request received", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("<<< Controller - {}.{} - Response sent ({} ms)", className, methodName, executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("!!! Controller - {}.{} - Exception occurred after {} ms: {}",
                    className, methodName, executionTime, throwable.getMessage());
            throw throwable;
        }
    }

    @Around("serviceMethodPointcut()")
    public Object logAroundServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.debug(">> Service - {}.{} - Started", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.debug("<< Service - {}.{} - Completed ({} ms)", className, methodName, executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("!! Service - {}.{} - Exception after {} ms: {}",
                    className, methodName, executionTime, throwable.getMessage());
            throw throwable;
        }
    }
}
