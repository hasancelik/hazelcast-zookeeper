package com.hazelcast.zookeeper;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class HazelcastIntegrationTest extends HazelcastTestSupport {

    private TestingServer zkTestServer;

    @Before
    public void setUp() throws Exception {
        zkTestServer = new TestingServer();
    }

    @After
    public void tearDown() throws IOException {
        zkTestServer.close();
    }

    @Test
    public void testIntegration() {
        String zookeeperURL = zkTestServer.getConnectString();
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.setProperty("hazelcast.discovery.enabled", "true");

        System.out.println(zookeeperURL);

        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
        discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), zookeeperURL);
        config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1= instanceFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = instanceFactory.newHazelcastInstance(config);
//        HazelcastInstance instance1 = createHazelcastInstance(config);
//        HazelcastInstance instance2 = createHazelcastInstance(config);

        int instance1Size = instance1.getCluster().getMembers().size();
        assertEquals(2, instance1Size);
        int instance2Size = instance2.getCluster().getMembers().size();
        assertEquals(2, instance2Size);
    }
}
