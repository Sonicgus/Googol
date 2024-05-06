package pt.uc.ga.webserver;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.web.IWebExchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;

@WebServlet(name = "thymeleaf", urlPatterns = "*.html")
public class ThymeleafServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private SpringResourceTemplateResolver resolver;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        resolver = new SpringResourceTemplateResolver();

        System.out.println("+--------------------------+");
        System.out.println(this.getServletContext());
        System.out.println(this.getServletContext().getRealPath("index.html"));
        System.out.println("+--------------------------+");

        //resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("/templates/");
        resolver.setCacheable(true);
        resolver.setCacheTTLMs(60000L);
        resolver.setCharacterEncoding("utf-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doService(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doService(request, response);
    }

    protected void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(resolver.getCharacterEncoding());

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        WebContext ctx = new WebContext((IWebExchange) this.getServletContext());

        ctx.setVariable("name", "friendly student!!!!!");
        ctx.setVariable("thename", "Jonas");
        ctx.setVariable("completeurl", "http://localhost:8080/thymeleafServlet/hellofromservlet.html");

        String templateName = getTemplateName(request);
        String result = engine.process(templateName, ctx);

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(result);
        } finally {
            out.close();
        }
    }

    protected String getTemplateName(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }

        return requestPath.substring(contextPath.length());
    }
}
