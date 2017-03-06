package com.apache.catalina;

/**
 * A <b>Cluster</b> works as a Cluster client/server for the local host
 * Different implementations can be used to support different
 * ways to communicate within the Cluster. A Cluster implementation is
 * responsible for setting up a way to communicate within the Cluster
 * and also supply "ClientApplication" with <code>ClusterSender</code>
 * used when sending information in the Cluster and
 * <code>ClusterInfo</code> used for receiving information in the Cluster
 *
 * Created by xjk on 3/6/17.
 */
public interface Cluster {

    /**
     * Return the name of the cluster that this Server is currently
     * configured to operate within
     *
     * @return The name of the cluster associated with this server
     */
    String getClusterName();

    /**
     * Set the name of the cluster to join, if no cluster with
     * this name is present create one.
     * @param clusterName The clustername ot join
     */
    void setClusterName(String clusterName);

    /**
     * Set the Container associated with our Cluster
     * @param container
     */
    void setContainer(Container container);

    /**
     * Get the Container associated with our Cluster
     * @return
     */
    Container getContainer();

}
