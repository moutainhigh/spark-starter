package info.spark.starter.websocket.pojo;

import info.spark.starter.websocket.annotation.BeforeHandshake;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:08
 * @since 2022.1.1
 */
public class Session {

    /** Channel */
    private final Channel channel;

    /**
     * Session
     *
     * @param channel channel
     * @since 2022.1.1
     */
    Session(Channel channel) {
        this.channel = channel;
    }

    /**
     * set subprotocols on {@link BeforeHandshake}
     *
     * @param subprotocols subprotocols
     * @since 2022.1.1
     */
    public void setSubprotocols(String subprotocols) {
        setAttribute("subprotocols", subprotocols);
    }

    /**
     * Sets attribute *
     *
     * @param <T>   parameter
     * @param name  name
     * @param value value
     * @since 2022.1.1
     */
    public <T> void setAttribute(String name, T value) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        channel.attr(sessionIdKey).set(value);
    }

    /**
     * Send text
     *
     * @param message message
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendText(String message) {
        return channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /**
     * Send text
     *
     * @param byteBuf byte buf
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendText(ByteBuf byteBuf) {
        return channel.writeAndFlush(new TextWebSocketFrame(byteBuf));
    }

    /**
     * Send text
     *
     * @param byteBuffer byte buffer
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendText(ByteBuffer byteBuffer) {
        ByteBuf buffer = channel.alloc().buffer(byteBuffer.remaining());
        buffer.writeBytes(byteBuffer);
        return channel.writeAndFlush(new TextWebSocketFrame(buffer));
    }

    /**
     * Send text
     *
     * @param textWebSocketFrame text web socket frame
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendText(TextWebSocketFrame textWebSocketFrame) {
        return channel.writeAndFlush(textWebSocketFrame);
    }

    /**
     * Send binary
     *
     * @param bytes bytes
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendBinary(byte[] bytes) {
        ByteBuf buffer = channel.alloc().buffer(bytes.length);
        return channel.writeAndFlush(new BinaryWebSocketFrame(buffer.writeBytes(bytes)));
    }

    /**
     * Send binary
     *
     * @param byteBuf byte buf
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendBinary(ByteBuf byteBuf) {
        return channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    /**
     * Send binary
     *
     * @param byteBuffer byte buffer
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendBinary(ByteBuffer byteBuffer) {
        ByteBuf buffer = channel.alloc().buffer(byteBuffer.remaining());
        buffer.writeBytes(byteBuffer);
        return channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    /**
     * Send binary
     *
     * @param binaryWebSocketFrame binary web socket frame
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture sendBinary(BinaryWebSocketFrame binaryWebSocketFrame) {
        return channel.writeAndFlush(binaryWebSocketFrame);
    }

    /**
     * Gets attribute *
     *
     * @param <T>  parameter
     * @param name name
     * @return the attribute
     * @since 2022.1.1
     */
    public <T> T getAttribute(String name) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        return channel.attr(sessionIdKey).get();
    }

    /**
     * Channel
     *
     * @return the channel
     * @since 2022.1.1
     */
    public Channel channel() {
        return channel;
    }

    /**
     * Returns the globally unique identifier of this {@link Channel}.
     *
     * @return the channel id
     * @since 2022.1.1
     */
    public ChannelId id() {
        return channel.id();
    }

    /**
     * Returns the configuration of this channel.
     *
     * @return the channel config
     * @since 2022.1.1
     */
    public ChannelConfig config() {
        return channel.config();
    }

    /**
     * Returns {@code true} if the {@link Channel} is open and may get active later
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * Returns {@code true} if the {@link Channel} is registered with an {@link EventLoop}.
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isRegistered() {
        return channel.isRegistered();
    }

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isActive() {
        return channel.isActive();
    }

    /**
     * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
     *
     * @return the channel metadata
     * @since 2022.1.1
     */
    public ChannelMetadata metadata() {
        return channel.metadata();
    }

    /**
     * Returns the local address where this channel is bound to.  The returned
     * {@link SocketAddress} is supposed to be down-cast into more concrete
     * type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the local address of this channel. {@code null} if this channel is not bound.
     * @since 2022.1.1
     */
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    /**
     * Returns the remote address where this channel is connected to.  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the remote address of this channel. {@code null} if this channel is not connected. If this channel is not connected but it
     *     can receive messages from arbitrary remote addresses (e.g. {@link DatagramChannel}, use {@link DatagramPacket#recipient()} to
     *     determine the origination of the received message as this method will return {@code null}.
     * @since 2022.1.1
     */
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    /**
     * Returns the {@link ChannelFuture} which will be notified when this
     * channel is closed.  This method always returns the same future instance.
     *
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isWritable() {
        return channel.isWritable();
    }

    /**
     * Get how many bytes can be written until {@link #isWritable()} returns {@code false}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code false} then 0.
     *
     * @return the long
     * @since 2022.1.1
     */
    public long bytesBeforeUnwritable() {
        return channel.bytesBeforeUnwritable();
    }

    /**
     * Get how many bytes must be drained from underlying buffers until {@link #isWritable()} returns {@code true}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code true} then 0.
     *
     * @return the long
     * @since 2022.1.1
     */
    public long bytesBeforeWritable() {
        return channel.bytesBeforeWritable();
    }

    /**
     * Returns an <em>internal-use-only</em> object that provides unsafe operations.
     *
     * @return the channel . unsafe
     * @since 2022.1.1
     */
    public Channel.Unsafe unsafe() {
        return channel.unsafe();
    }

    /**
     * Return the assigned {@link ChannelPipeline}.
     *
     * @return the channel pipeline
     * @since 2022.1.1
     */
    public ChannelPipeline pipeline() {
        return channel.pipeline();
    }

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     *
     * @return the byte buf allocator
     * @since 2022.1.1
     */
    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    /**
     * Read
     *
     * @return the channel
     * @since 2022.1.1
     */
    public Channel read() {
        return channel.read();
    }

    /**
     * Flush
     *
     * @return the channel
     * @since 2022.1.1
     */
    public Channel flush() {
        return channel.flush();
    }

    /**
     * Close
     *
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture close() {
        return channel.close();
    }

    /**
     * Close
     *
     * @param promise promise
     * @return the channel future
     * @since 2022.1.1
     */
    public ChannelFuture close(ChannelPromise promise) {
        return channel.close(promise);
    }

}
