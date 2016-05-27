package org.onosproject.sdnwise.controller.impl;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.onosproject.sdnwise.controller.driver.SDNWiseAgent;
import org.onosproject.sdnwise.controller.driver.SDNWiseNodeDriver;
import org.onosproject.sdnwise.drivers.SDNWiseSensorNodeImpl;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.onlab.util.Tools.namedThreads;

/**
 * Created by aca on 2/19/15.
 */
public class Controller {
    protected static final Logger log = LoggerFactory.getLogger(Controller.class);

    private ChannelGroup cg;

    protected int sdnwisePort = 9999;
    protected int workerThreads = 0;

    protected long systemStartTime;

    private SDNWiseAgent agent;

    private NioServerSocketChannelFactory execFactory;

    // TODO: Check buffer size for SDN-Wise
    protected static final int SEND_BUFFER_SIZE = 4 * 1024 * 1024;
    protected static final int RCV_BUFFER_SIZE = 10240;

    public long getSystemStartTime() {
        return this.systemStartTime;
    }

    public void run() {

        try {
            final ServerBootstrap bootstrap = createServerBootStrap();

            bootstrap.setOption("reuseAddress", true);
            bootstrap.setOption("child.keepAlive", true);
//            bootstrap.setOption("child.tcpNoDelay", false);
            bootstrap.setOption("child.sendBufferSize", Controller.SEND_BUFFER_SIZE);
            bootstrap.setOption("child.receiveBufferSize", Controller.RCV_BUFFER_SIZE);

            ChannelPipelineFactory pfact =
                    new SDNWisePipelineFactory(this, null);
            bootstrap.setPipelineFactory(pfact);
            InetSocketAddress sa = new InetSocketAddress(sdnwisePort);
            cg = new DefaultChannelGroup();
            cg.add(bootstrap.bind(sa));

            log.info("Listening for sensor node connections on {}", sa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private ServerBootstrap createServerBootStrap() {

        if (workerThreads == 0) {
            execFactory =  new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(namedThreads("Controller-boss-%d")),
                    Executors.newCachedThreadPool(namedThreads("Controller-worker-%d")));
//            execFactory = new OioServerSocketChannelFactory(
//                    Executors.newCachedThreadPool(namedThreads("Controller-boss-%d")),
//                    Executors.newCachedThreadPool(namedThreads("Controller-worker-%d")));
            return new ServerBootstrap(execFactory);
        } else {
            execFactory = new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(namedThreads("Controller-boss-%d")),
                    Executors.newCachedThreadPool(namedThreads("Controller-worker-%d")), workerThreads);
//            execFactory = new OioServerSocketChannelFactory(
//                    Executors.newCachedThreadPool(namedThreads("Controller-boss-%d")),
//                    Executors.newCachedThreadPool(namedThreads("Controller-worker-%d")), workerThreads);
            return new ServerBootstrap(execFactory);
        }
    }

    public void start(SDNWiseAgent ag) {
        log.info("Starting SDNWise IO");
        this.agent = ag;
//        this.init(new HashMap<String, String>());
        this.run();
    }


    public void stop() {
        log.info("Stopping SDNWise IO");
        execFactory.shutdown();
        cg.close();
    }

    public SDNWiseNodeDriver getDriver(SDNWiseNodeId nodeId, Channel channel) {
        SDNWiseNodeDriver sdnWiseNodeDriver = new SDNWiseSensorNodeImpl(nodeId, channel);
        sdnWiseNodeDriver.setAgent(agent);

        return sdnWiseNodeDriver;
    }
}
