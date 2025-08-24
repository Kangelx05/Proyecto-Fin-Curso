package api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final api.security.TokenAuthenticationInterceptor tokenAuthenticationInterceptor;

    public WebConfig(api.security.TokenAuthenticationInterceptor tokenAuthenticationInterceptor) {
        this.tokenAuthenticationInterceptor = tokenAuthenticationInterceptor;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://192.168.1.34:5173")
                // Permit all standard HTTP methods including OPTIONS
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Explicitly allow the headers used in our API, including Authorization for JWTs
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                // Expose Authorization header if needed by clients (optional)
                .exposedHeaders("Authorization")
                // Whether or not user credentials (cookies, auth headers) are allowed
                .allowCredentials(true);

    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(tokenAuthenticationInterceptor);
    }
}