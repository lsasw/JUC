// ... existing code ...

package com.mashibing.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Disruptor示例：演示基本的消息传递
 */
public class DisruptorExample {
    
    /**
     * LongEvent类表示在Disruptor中传递的数据
     */
    public static class LongEvent {
        private long value;

        public void set(long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * LongEventFactory用于创建LongEvent实例
     */
    public static class LongEventFactory implements EventFactory<LongEvent> {
        @Override
        public LongEvent newInstance() {
            return new LongEvent();
        }
    }

    /**
     * LongEventHandler处理Disruptor中的事件
     */
    public static class LongEventHandler implements EventHandler<LongEvent> {
        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
            System.out.println("Received: " + event);
        }
    }

    /**
     * LongEventProducer向Disruptor发布事件
     */
    public static class LongEventProducer {
        private final RingBuffer<LongEvent> ringBuffer;

        public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        /**
         * 将数据放入RingBuffer
         * @param bb 包含要发送的数据的ByteBuffer
         */
        public void onData(ByteBuffer bb) {
            long sequence = ringBuffer.next();  // 获取下一个槽位
            try {
                LongEvent event = ringBuffer.get(sequence); // 获取槽位上的事件对象
                event.set(bb.getLong(0)); // 填充事件数据
            } finally {
                ringBuffer.publish(sequence); // 发布事件
            }
        }
    }

    /**
     * 主方法启动Disruptor示例
     * @param args 命令行参数
     */
    public static void main(String[] args) throws InterruptedException {
        // 创建Disruptor实例
        int bufferSize = 1024;  // 必须是2的幂次方
        Executor executor = Executors.newCachedThreadPool();
        
        Disruptor<LongEvent> disruptor = new Disruptor<>(
            new LongEventFactory(), 
            bufferSize, 
            DaemonThreadFactory.INSTANCE
        );
        
        // 连接处理器
        disruptor.handleEventsWith(new LongEventHandler());
        
        // 启动Disruptor
        disruptor.start();
        
        // 获取RingBuffer
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducer producer = new LongEventProducer(ringBuffer);
        
        // 创建缓冲区并发送数据
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long i = 0; i < 10; i++) {
            bb.putLong(0, i);
            producer.onData(bb);
            Thread.sleep(500);
        }
        
        // 关闭Disruptor
        disruptor.shutdown();
    }
}
