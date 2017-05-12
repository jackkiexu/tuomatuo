package com.lami.tuomatuo.mq.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CreateMode value determines how the znode is created on ZooKeeper
 * Created by xujiankang on 2017/3/19.
 */
public enum CreateMode {
    /**
     * The znode will not be automically deleted upon client's disconnect
     */
    PERSISTENT(0, false, false, false, false),

    /**
     * The znode will not be automically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number
     */
    PERSISTENT_SEQUENTIAL(2, false, true, false, false),

    /**
     * The znode will be deleted upon the client's disconnect
     */
    EPHEMERAL(1, true, false, false, false),

    /**
     * The znode will be deleted upon the client's disconnect, and its name
     * will be appended with a monotonically increasing number
     */
    EPHEMERAL_SEQUENTIAL(3, true, true, false, false),

    /**
     * The znode will be a container ndoe. Container
     * nodes are special purpose nodes useful for recipes such as leader, lock,
     * etc. When the last child of a container is deleted, the container becomes
     * a cadidate to be deleted by the server at some point in the future.
     * Given this property, you should be prepared to get NoNodeException
     * when creating children inside of this container node
     */
    CONTAINER(4, false, false, true, false),

    /**
     * The znode will not be automatically deleted upon client's disconnect.
     * However if the znode has not been modified within the given TTL, it
     * will be deleted once it has no children
     */
    PERSISTENT_WITH_TTL(5, false, false, false, true),

    /**
     * The znode will not be automatically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number.
     * However if the znode has not been modified within the given TTL, it
     * will be deleted once it has no children
     */
    PERSISTENT_SEQUENTIAL_WITH_TTL(6, false, true, false, true);

    private static final Logger LOG = LoggerFactory.getLogger(CreateMode.class);

    private boolean ephemral;
    private boolean sequential;
    private final boolean isContainer;
    private int flag;
    private boolean isTTL;


    CreateMode(int flag, boolean ephemral, boolean sequential,
               boolean isContainer,  boolean isTTL) {
        this.ephemral = ephemral;
        this.sequential = sequential;
        this.isContainer = isContainer;
        this.flag = flag;
        this.isTTL = isTTL;
    }

    public boolean isEphemral() {
        return ephemral;
    }

    public boolean isSequential() {
        return sequential;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public int toFlag() {
        return flag;
    }

    public boolean isTTL() {
        return isTTL;
    }



    /**
     * Map an integer value to a CreateMode value
     */
    static public CreateMode fromFlag(int flag) throws KeeperException {
        switch(flag) {
            case 0: return CreateMode.PERSISTENT;

            case 1: return CreateMode.EPHEMERAL;

            case 2: return CreateMode.PERSISTENT_SEQUENTIAL;

            case 3: return CreateMode.EPHEMERAL_SEQUENTIAL ;

            case 4: return CreateMode.CONTAINER;

            case 5: return CreateMode.PERSISTENT_WITH_TTL;

            case 6: return CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL;

            default:
                String errMsg = "Received an invalid flag value: " + flag
                        + " to convert to a CreateMode";
                LOG.error(errMsg);
                throw new KeeperException.BadArgumentsException(errMsg);
        }
    }

    /**
     * Map an integer value to a CreateMode value
     */
    static public CreateMode fromFlag(int flag, CreateMode defaultMode) {
        switch(flag) {
            case 0:
                return CreateMode.PERSISTENT;

            case 1:
                return CreateMode.EPHEMERAL;

            case 2:
                return CreateMode.PERSISTENT_SEQUENTIAL;

            case 3:
                return CreateMode.EPHEMERAL_SEQUENTIAL;

            case 4:
                return CreateMode.CONTAINER;

            case 5:
                return CreateMode.PERSISTENT_WITH_TTL;

            case 6:
                return CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL;

            default:
                return defaultMode;
        }
    }
}
