package com.wiz.ignite;

import java.io.File;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterState;
import com.wiz.constants.ConfigContants;
import com.wiz.logs.LogFactory;
import com.wiz.threads.FutureThread;
import io.vertx.core.Future;

public class IgniteService {
    private static final LogFactory LOGGER = LogFactory.getLogger(IgniteService.class);

    private static volatile IgniteService instance;

    public static IgniteService getInstance() {
        if (instance == null) {
            synchronized (IgniteService.class) {
                if (instance == null) {
                    instance = new IgniteService();
                }
            }
        }

        return instance;
    }

    private static final String IGNITE_PATH = "ignite.xml";

    private Ignite ignite;

    public void initServiceBlocking() {
        LOGGER.info("Starting {}.", this.getClass().getSimpleName());
        this.ignite = Ignition.start(ConfigContants.CONFIG_FOLDER + File.separator + IGNITE_PATH);
        ignite.cluster().state(ClusterState.ACTIVE);

        String nodeId = ignite.cluster().localNode().id().toString();
        LOGGER.info("Ignite instance ID={}", nodeId);
        LOGGER.info("Starting {} successful.", this.getClass().getSimpleName());
    }

    public Future<Void> initilizeCacheAsync() {
        return FutureThread.handle(() -> {
            CacheFactory cacheFactory = CacheFactory.getInstance();
            cacheFactory.addCachesToMap().compose(rs -> preloadCacheAsync(cacheFactory));
        });
    }

    public Future<Boolean> preloadCacheAsync(CacheFactory cacheFactory) {
        return FutureThread.handle(() -> {
            return true;
        });
    }
}
