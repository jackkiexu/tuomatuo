package com.lami.tuomatuo.mq.zookeeper.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Most simple HostProvider, resolve only on instantiation
 * Created by xjk on 3/22/17.
 */
public class StaticHostProvider implements HostProvider{

    private static final Logger LOG = LoggerFactory.getLogger(StaticHostProvider.class);

    private final List<InetSocketAddress> serverAddresses = new ArrayList<>();

    private int lastIndex = -1;

    private int currentIndex = -1;


    /**
     * Constructs a SimpleHostSet.
     *
     * @param serverAddresses
     *            possibly unresolved ZooKeeper server addresses
     * @throws UnknownHostException
     * @throws IllegalArgumentException
     *             if serverAddresses is empty or resolves to an empty list
     */
    public StaticHostProvider(Collection<InetSocketAddress> serverAddresses)
            throws UnknownHostException {
        for (InetSocketAddress address : serverAddresses) {
            InetAddress ia = address.getAddress();
            InetAddress resolvedAddresses[] = InetAddress.getAllByName((ia!=null) ? ia.getHostAddress():
                    address.getHostName());
            for (InetAddress resolvedAddress : resolvedAddresses) {
                // If hostName is null but the address is not, we can tell that
                // the hostName is an literal IP address. Then we can set the host string as the hostname
                // safely to avoid reverse DNS lookup.
                // As far as i know, the only way to check if the hostName is null is use toString().
                // Both the two implementations of InetAddress are final class, so we can trust the return value of
                // the toString() method.
                if (resolvedAddress.toString().startsWith("/")
                        && resolvedAddress.getAddress() != null) {
                    this.serverAddresses.add(
                            new InetSocketAddress(InetAddress.getByAddress(
                                    address.getHostName(),
                                    resolvedAddress.getAddress()),
                                    address.getPort()));
                } else {
                    this.serverAddresses.add(new InetSocketAddress(resolvedAddress.getHostAddress(), address.getPort()));
                }
            }
        }

        if (this.serverAddresses.isEmpty()) {
            throw new IllegalArgumentException(
                    "A HostProvider may not be empty!");
        }
        Collections.shuffle(this.serverAddresses);
    }

    @Override
    public int size() {
        return serverAddresses.size();
    }

    @Override
    public InetSocketAddress next(long spinDelay) {
        ++currentIndex;
        if(currentIndex == serverAddresses.size()){
            currentIndex = 0;
        }
        if(currentIndex == lastIndex && spinDelay > 0){
            try {
                Thread.sleep(spinDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(lastIndex == -1){
            // We don't want to sleep on the first ever connect attempt
            lastIndex = 0;
        }
        return serverAddresses.get(currentIndex);
    }

    @Override
    public void onConnected() {
        lastIndex = currentIndex;
    }
}
