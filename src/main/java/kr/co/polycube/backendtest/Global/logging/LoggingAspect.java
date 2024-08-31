package kr.co.polycube.backendtest.Global.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final LogRepository LogRepository;

    @Before("execution(* kr.co.polycube.backendtest.Domain.user.PolyUserController.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Executing method: {}, with arguments: {}", methodName, Arrays.toString(args));

        Log log = new Log(methodName, Arrays.toString(args), "No result");
        LogRepository.save(log);

    }

    @AfterReturning(pointcut = "execution(* kr.co.polycube.backendtest.Domain.user.PolyUserController.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Method executed: {}, returned: {}", methodName, result);
        Log log = new Log(methodName, "No arguments", result.toString());
        LogRepository.save(log);
    }
}

