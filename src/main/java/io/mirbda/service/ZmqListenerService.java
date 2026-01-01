package io.mirbda.service;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;
import io.mirbda.config.CacheConfig;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.ZmqProperty;
import java.util.concurrent.ScheduledFuture;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Service
@RequiredArgsConstructor
public class ZmqListenerService implements InitializingBean, DisposableBean {
    @Override
    public void afterPropertiesSet() {
        if (zmqProperty == null
                || zmqProperty.hashblockUrl() == null || zmqProperty.hashblockUrl().isBlank()
                || zmqProperty.hashtxUrl()    == null || zmqProperty.hashtxUrl().isBlank()) {
            return;
        }

        context = new ZContext();
        running = true;
        Thread.ofVirtual().name("zmq-listener").start(this::listen);
    }

    @Override public void destroy() {
        running = false;

        if (context != null) {
            context.close();
        }

        scheduler.shutdownNow();
    }

    private void listen() {
        final ZMQ.Socket hashblockSocket = context.createSocket(SocketType.SUB);
        hashblockSocket.connect(zmqProperty.hashblockUrl());
        hashblockSocket.subscribe("hashblock");

        final ZMQ.Socket hashtxSocket = context.createSocket(SocketType.SUB);
        hashtxSocket.connect(zmqProperty.hashtxUrl());
        hashtxSocket.subscribe("hashtx");

        final ZMQ.Poller poller = context.createPoller(2);
        poller.register(hashblockSocket, ZMQ.Poller.POLLIN);
        poller.register(hashtxSocket,    ZMQ.Poller.POLLIN);

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                if (poller.poll(1_000) < 0) {
                    break;
                }

                if (poller.pollin(0)) {
                    onHashblock(hashblockSocket);
                }

                if (poller.pollin(1)) {
                    onHashtx(hashtxSocket);
                }

            } catch (final Exception _) { }
        }
    }

    private void onHashblock(final ZMQ.Socket socket) {
        socket.recvStr();
        socket.recv();
        socket.recv();

        evictAll(CacheConfig.BLOCKCHAIN);
        evictAll(CacheConfig.MINING);
        evictAll(CacheConfig.UTXO);
        evictAll(CacheConfig.TRANSACTION);
    }

    private void onHashtx(final ZMQ.Socket socket) {
        socket.recvStr();
        socket.recv();
        socket.recv();

        scheduleMempoolEviction();
    }

    private synchronized void scheduleMempoolEviction() {
        if (pendingMempoolEviction != null) {
            pendingMempoolEviction.cancel(false);
        }

        pendingMempoolEviction = scheduler.schedule(() ->
                evictAll(CacheConfig.MEMPOOL),
                MEMPOOL_DEBOUNCE_SECONDS, TimeUnit.SECONDS);
    }

    private void evictAll(final String cacheName) {
        final var cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            cache.clear();
        }
    }

    private static final int MEMPOOL_DEBOUNCE_SECONDS = 2;

    private volatile ZContext context;
    private final ZmqProperty zmqProperty;
    private final CacheManager cacheManager;
    private volatile boolean running = false;
    private volatile ScheduledFuture<?> pendingMempoolEviction;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().name("zmq-debounce").factory());
}
