package pt.uc.ga.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebServerApplication {


    @Bean
    public ServletRegistrationBean<teste> testeBean() {
        ServletRegistrationBean<teste> bean = new ServletRegistrationBean<>(new teste(), "/thymeleafServlet/*");
        bean.setLoadOnStartup(1);
        return bean;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebServerApplication.class, args);
    }

}
