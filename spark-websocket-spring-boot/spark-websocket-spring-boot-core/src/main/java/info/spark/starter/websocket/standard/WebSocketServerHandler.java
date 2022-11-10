package info.spark.starter.websocket.standard;

import info.spark.starter.websocket.pojo.PojoEndpointServer;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:04
 * @since 2022.1.1
 */
@Slf4j
class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /** Pojo endpoint server */
    private final PojoEndpointServer pojoEndpointServer;

    /**
     * Web socket server handler
     *
     * @param pojoEndpointServer pojo endpoint server
     * @since 2022.1.1
     */
    WebSocketServerHandler(PojoEndpointServer pojoEndpointServer) {
        this.pojoEndpointServer = pojoEndpointServer;
    }

    /**
     * Channel read 0
     *
     * @param ctx ctx
     * @param msg msg
     * @throws Exception exception
     * @since 2022.1.1
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        handleWebSocketFrame(ctx, msg);
    }

    /**
     * Handle web socket frame
     *
     * @param ctx   ctx
     * @param frame frame
     * @since 2022.1.1
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            pojoEndpointServer.doOnMessage(ctx.channel(), frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            pojoEndpointServer.doOnBinary(ctx.channel(), frame);
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            log.debug("frame is PongWebSocketFrame");
        }
    }

    /**
     * Channel inactive
     *
     * @param ctx ctx
     * @throws Exception exception
     * @since 2022.1.1
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        pojoEndpointServer.doOnClose(ctx.channel());
    }

    /**
     * User event triggered
     *
     * @param ctx ctx
     * @param evt evt
     * @throws Exception exception
     * @since 2022.1.1
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        pojoEndpointServer.doOnEvent(ctx.channel(), evt);
    }

    /**
     * Exception caught
     *
     * @param ctx   ctx
     * @param cause cause
     * @since 2022.1.1
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        pojoEndpointServer.doOnError(ctx.channel(), cause);
    }

}
