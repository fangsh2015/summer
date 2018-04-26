package com.xinqing.summer.mvc.http;

import com.xinqing.summer.mvc.Summers;
import com.xinqing.summer.mvc.http.handler.Handler;
import com.xinqing.summer.mvc.route.Route;
import com.xinqing.summer.mvc.route.Router;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http执行
 *
 * Created by xuan on 2018/4/17
 */
public class HttpExecution {

    private static final Logger LOG = LoggerFactory.getLogger(HttpExecution.class);

    private final Router router = Summers.summer().router();

    public void execute(ChannelHandlerContext ctx, FullHttpRequest req) {
        Request request = new DefaultRequest(req);
        Response response = new DefaultResponse(ctx, HttpUtil.isKeepAlive(req));

        // 初始化请求上下文
        RequestContext.setup(request);

        Handler handler;
        // 寻找到匹配的handler
        Route route = router.lookup(request.method(), request.path());

        if (route == null) {
            // not found
            handler = router.notFound();
        } else {
            handler = route.getHandler();
        }

        // do handle
        try {
            handler.handle(request, response);
        } catch (Exception e) {
            LOG.error("request handle error", e);
        }

        // 释放请求上下文
        RequestContext.cleanup();
    }

}
