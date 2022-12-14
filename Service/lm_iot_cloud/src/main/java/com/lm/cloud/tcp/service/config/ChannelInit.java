package com.lm.cloud.tcp.service.config;

import com.lm.cloud.tcp.service.MessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * Netty 通道初始化
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
public class ChannelInit extends ChannelInitializer<NioSocketChannel> {

    private final MessageHandler messageHandler;

    // 把解码器变成单例 不然每个连接都新建一个对象
    private StringDecoder stringDecoder = new StringDecoder();
    private StringEncoder stringEncoder = new StringEncoder();

    // 注意JSON不解码器不能够变成单例模式

    @Override
    protected void initChannel(NioSocketChannel channel) {
//        System.out.println(this);
//        System.out.println(stringDecoder);
//        System.out.println(stringEncoder);
//        System.out.println("\n");
        // 10秒后无上报数据就提出服务器 需要发送#skip
        channel.pipeline()
                // 心跳时间
                .addLast("idle", new IdleStateHandler(10, 10, 10, TimeUnit.SECONDS))
                // 添加字符串编码器
                .addLast(stringEncoder)
                // 添加解码器
                .addLast(stringDecoder)
                .addLast(new JsonObjectDecoder())
                // 添加消息处理器
                .addLast("messageHandler", messageHandler);
    }

}

