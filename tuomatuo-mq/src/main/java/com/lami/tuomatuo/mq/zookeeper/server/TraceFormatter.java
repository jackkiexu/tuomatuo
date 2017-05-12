package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;

/**
 * Created by xjk on 3/19/17.
 */
public class TraceFormatter {

    public static String op2String(int op){
        switch (op){
            case ZooDefs.OpCode.notification:
                return "notification";
            case ZooDefs.OpCode.create:
                return "create";
            case ZooDefs.OpCode.create2:
                return "create2";
            case ZooDefs.OpCode.createTTL:
                return "createTtl";
            case ZooDefs.OpCode.createContainer:
                return "createContainer";
            case ZooDefs.OpCode.delete:
                return "delete";
            case ZooDefs.OpCode.deleteContainer:
                return "deleteContainer";
            case ZooDefs.OpCode.exists:
                return "exists";
            case ZooDefs.OpCode.getData:
                return "getData"; // 这一部分在 github 有 bug
            case ZooDefs.OpCode.setData:
                return "setData";
            case ZooDefs.OpCode.multi:
                return "multi";
            case ZooDefs.OpCode.getACL:
                return "getACL";
            case ZooDefs.OpCode.setACL:
                return "setACL";
            case ZooDefs.OpCode.getChildren:
                return "getChildren";
            case ZooDefs.OpCode.getChildren2:
                return "getChildren2";
            case ZooDefs.OpCode.ping:
                return "ping";
            case ZooDefs.OpCode.createSession:
                return "createSession";
            case ZooDefs.OpCode.closeSession:
                return "closeSession";
            case ZooDefs.OpCode.error:
                return "error";
            case ZooDefs.OpCode.reconfig:
                return "reconfig";
            default:
                return "unknown " + op;

        }
    }
}
