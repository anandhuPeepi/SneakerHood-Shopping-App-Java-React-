package PPs.ShoppingApp.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // 401 - not authenticated (missing/invalid token)
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        writeApiError(response, request, HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Authentication is required to access this resource.");
    }

    // 403 - authenticated but not allowed (wrong role)
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        writeApiError(response, request, HttpStatus.FORBIDDEN,
                "Forbidden",
                "You do not have permission to access this resource.");
    }

    private void writeApiError(HttpServletResponse response,
                               HttpServletRequest request,
                               HttpStatus status,
                               String error,
                               String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getRequestURI());
        body.put("fieldErrors", null); // keep your ApiError shape consistent

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
