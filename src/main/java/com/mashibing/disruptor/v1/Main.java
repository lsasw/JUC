package com.mashibing.disruptor.v1;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
// 测试类 测试Disruptor 的使用 disruptor 是一个事件驱动框架
public class Main {
    public static void main(String[] args) {
    // Executor executor = Executors.newCachedThreadPool();

    // 创建一个LongEvent对象的工厂
    LongEventFactory factory = new LongEventFactory();

    // 必须是2的幂次方，以优化性能
    int ringBufferSize = 1024;

    // 初始化Disruptor实例，使用默认线程工厂
    Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ringBufferSize, Executors.defaultThreadFactory());

    // 设置事件处理器
    disruptor.handleEventsWith(new LongEventHandler());

    // 启动Disruptor
    disruptor.start();

    // 获取RingBuffer实例
    RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

    // 创建生产者实例
    LongEventProducer producer = new LongEventProducer(disruptor.getRingBuffer());

    // 创建一个ByteBuffer，用于写入数据
    ByteBuffer bb = ByteBuffer.allocate(8);

    // 循环写入数据到RingBuffer中
    for(long l = 0; l<100; l++) {
        // 将数据写入ByteBuffer
        bb.putLong(0, l);

        // 使用生产者将数据写入RingBuffer
        producer.onData(bb);

        try {
            // 模拟数据生产速度，每写入一个数据后暂停100毫秒
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 关闭Disruptor
    disruptor.shutdown();
}

}
