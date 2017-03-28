package com.lami.tuomatuo.mq.zookeeper;

import org.apache.zookeeper.data.Stat;

/**
 * Encode the result of a single part of a multiple operation commit
 *
 * Created by xujiankang on 2017/3/19.
 */
public abstract class OpResult {

    private int type;

    public OpResult(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * A result from a create operation. This kind of result allows the
     * path to be retrieved since the create might have been a sequential
     * create
     */
    public static class CreateResult extends OpResult{

        private String path;

        public CreateResult(String path) {
            super(ZooDefs.OpCode.create);
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreateResult that = (CreateResult) o;

            return path != null ? path.equals(that.path) : that.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }


    /**
     * A result from a delete operation. No special values are available
     */
    public static class DeleteResult extends OpResult{

        public DeleteResult() {
            super(ZooDefs.OpCode.delete);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DeleteResult)) return false;

            DeleteResult opResult = (DeleteResult) o;
            return getType() == opResult.getType();
        }

        @Override
        public int hashCode() {
            return getType();
        }

    }



    public static class SetDataResult extends OpResult{
        private Stat stat;

        public SetDataResult(Stat stat) {
            super(ZooDefs.OpCode.setData);
            this.stat = stat;
        }

        public Stat getStat() {
            return stat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SetDataResult that = (SetDataResult) o;

            return stat != null ? stat.equals(that.stat) : that.stat == null;

        }

        @Override
        public int hashCode() {
            return stat != null ? stat.hashCode() : 0;
        }
    }


    /**
     * A result from a version check operation. No special values are available
     */
    public static class CheckResult extends OpResult{
        public CheckResult() {
            super(ZooDefs.OpCode.check);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CheckResult)) return false;

            CheckResult other = (CheckResult) o;
            return getType() == other.getType();
        }

        @Override
        public int hashCode() {
            return getType();
        }
    }


    /**
     * An error result from any kind of operation. The point of error results
     * is that they contain an error code which helps understand what happened
     */
    public static class ErrorResult extends OpResult{
        private int err;

        public ErrorResult(int err) {
            super(ZooDefs.OpCode.error);
            this.err = err;
        }

        public int getErr() {
            return err;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ErrorResult)) return false;

            ErrorResult other = (ErrorResult) o;
            return getType() == other.getType() && err == other.getErr();
        }

        @Override
        public int hashCode() {
            return getType() * 35 + err;
        }

    }
}
