package com.lami.tuomatuo.mq.zookeeper;

import com.lami.tuomatuo.mq.zookeeper.common.PathUtils;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.proto.CheckVersionRequest;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.proto.SetDataRequest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a single operation in a multi-operation. Each operation can be a create, update
 * or delete or can just be a version check
 *
 * Sub-class of Op each represent each detailed type but should not normally be referenced except via
 * the provided factory method
 *
 * Created by xujiankang on 2017/3/19.
 */
public abstract class Op {

    private int type;
    private String path;

    // prevent untyped construction
    public Op(int type, String path) {
        this.type = type;
        this.path = path;
    }

    /**
     * Constructs a create operation. Arguments are as for the ZooKeeper method of the same name
     * @param path
     * @param data
     * @param acl
     * @param flags
     * @return
     */
    public static Op create(String path, byte[] data, List<ACL> acl, int flags){
        return new Create(path, data, acl, flags);
    }

    public static Op create(String path, byte[] data, List<ACL> acl, CreateMode createMode){
        return new Create(path, data, acl, createMode);
    }

    public static Op delete(String path, int version){
        return new Delete(path, version);
    }

    public static Op setData(String path, byte[] data, int version){
        return new SetData(path, data, version);
    }

    public static Op check(String path, int version){
        return new Check(path, version);
    }

    // Gets the integer type code for an Op.
    // This code should be as from ZooDefs.OpCode
    public int getType() {
        return type;
    }
    // Gets the path for an Op
    public String getPath() {
        return path;
    }

    /**
     * Encodes an op for wire transmission
     * @return an appropriate Record structure
     */
    public abstract Record toRequestRecord();

    /**
     * Reconstructs the transaction with the chroot prefix
     * @param addRootPrefix
     * @return
     */
    abstract Op withChroot(String addRootPrefix);

    /**
     * Performs client path validations
     * @throws KeeperException
     */
    void validate() throws KeeperException{
        PathUtils.validatePath(path);
    }




    public static class Create extends Op{

        private byte[] data;
        private List<ACL> acl;
        private int flags;

        private Create(String path, byte[] data, List<ACL> acl, int flags){
            super(ZooDefs.OpCode.create, path);
            this.data = data;
            this.acl = acl;
            this.flags = flags;
        }


        private Create(String path, byte[] data, List<ACL> acl, CreateMode createMode){
            super(ZooDefs.OpCode.create, path);
            this.data = data;
            this.acl = acl;
            this.flags = createMode.toFlag();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Create)) return false;

            Create op = (Create) o;

            boolean aclEquals = true;
            Iterator<ACL> i = op.acl.iterator();
            for (ACL acl : op.acl) {
                boolean hasMoreData = i.hasNext();
                if (!hasMoreData) {
                    aclEquals = false;
                    break;
                }
                ACL otherAcl = i.next();
                if (!acl.equals(otherAcl)) {
                    aclEquals = false;
                    break;
                }
            }
            return !i.hasNext() && getType() == op.getType() && Arrays.equals(data, op.data) && flags == op.flags && aclEquals;
        }

        @Override
        public int hashCode() {
            return getType() + getPath().hashCode() + Arrays.hashCode(data);
        }

        public Create(int type, String path) {
            super(type, path);
        }

        @Override
        public Record toRequestRecord() {
            return new CreateRequest(getPath(), data, acl, flags);
        }

        @Override
        Op withChroot(String addRootPrefix) {
            return new Create(addRootPrefix, data, acl, flags);
        }

        @Override
        void validate() throws KeeperException {
            CreateMode createMode = CreateMode.fromFlag(flags);
            PathUtils.validatePath(getPath(), createMode.isSequential());
        }
    }

    public static class Delete extends Op{
        private int version;

        public Delete(String path, int version) {
            super(ZooDefs.OpCode.delete, path);
            this.version = version;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Delete)) return false;

            Delete op = (Delete) o;

            return getType() == op.getType() && version == op.version
                    && getPath().equals(op.getPath());
        }

        @Override
        public int hashCode() {
            return getType() + getPath().hashCode() + version;
        }

        @Override
        public Record toRequestRecord() {
            return new DeleteRequest(getPath(), version);
        }

        @Override
        Op withChroot(String addRootPrefix) {
            return new Delete(addRootPrefix, version);
        }
    }

    public static class SetData extends Op{
        private byte[] data;
        private int version;

        public SetData(String path, byte[] data, int version) {
            super(ZooDefs.OpCode.setData, path);
            this.data = data;
            this.version = version;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SetData)) return false;

            SetData op = (SetData) o;

            return getType() == op.getType() && version == op.version
                    && getPath().equals(op.getPath()) && Arrays.equals(data, op.data);
        }

        @Override
        public int hashCode() {
            return getType() + getPath().hashCode() + Arrays.hashCode(data) + version;
        }

        @Override
        public Record toRequestRecord() {
            return new SetDataRequest(getPath(), data, version);
        }

        @Override
        Op withChroot(String addRootPrefix) {
            return new SetData(addRootPrefix, data, version);
        }
    }

    public static class Check extends Op{
        private int version;

        public Check(String path, int version) {
            super(ZooDefs.OpCode.check, path);
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Check)) return false;

            Check op = (Check) o;

            return getType() == op.getType() && getPath().equals(op.getPath()) && version == op.version;
        }

        @Override
        public int hashCode() {
            return getType() + getPath().hashCode() + version;
        }


        @Override
        public Record toRequestRecord() {
            return new CheckVersionRequest(getPath(), version);
        }

        @Override
        Op withChroot(String addRootPrefix) {
            return new Check(addRootPrefix, version);
        }
    }
}
