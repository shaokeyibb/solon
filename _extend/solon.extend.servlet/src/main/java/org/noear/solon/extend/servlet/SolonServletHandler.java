package org.noear.solon.extend.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 * */
public class SolonServletHandler extends HttpServlet {

    protected void preHandle(Context ctx) {

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SolonServletContext ctx = new SolonServletContext(request, response);
        ctx.contentType("text/plain;charset=UTF-8");

        preHandle(ctx);

        Solon.global().tryHandle(ctx);

        if (ctx.getHandled() == false || ctx.status() == 404) {
            response.setStatus(404);
        }
    }
}
