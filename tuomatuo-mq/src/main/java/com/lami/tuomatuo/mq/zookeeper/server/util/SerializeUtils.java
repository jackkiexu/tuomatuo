package com.lami.tuomatuo.mq.zookeeper.server.util;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.txn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xjk on 3/17/17.
 */
public class SerializeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SerializeUtils.class);

    public static Record deserializeTxn(byte txnBytes[], TxnHeader hdr) throws IOException{
        final ByteArrayInputStream bais = new ByteArrayInputStream(txnBytes);
        InputArchive ia = BinaryInputArchive.getArchive(bais);

        hdr.deserialize(ia, "hdr");
        bais.mark(bais.available());
        Record txn = null;
        switch (hdr.getType()){
            case ZooDefs.OpCode.createSession:
                // This isn't  really an error txn; it just has the same
                // format. The error represents the timeout
                txn = new CreateSessionTxn();
                break;
            case ZooDefs.OpCode.closeSession:
                return null;
            case ZooDefs.OpCode.create:
            case ZooDefs.OpCode.create2:
                txn = new CreateTxn();
                break;
            case ZooDefs.OpCode.createTTL:
                txn = new CreateTTLTxn();
                break;
            case ZooDefs.OpCode.createContainer:
                txn = new CreateContainerTxn();
                break;
            case ZooDefs.OpCode.delete:
            case ZooDefs.OpCode.deleteContainer:
                txn = new DeleteTxn();
                break;
            case ZooDefs.OpCode.reconfig:
            case ZooDefs.OpCode.setData:
                txn = new SetDataTxn();
                break;
            case ZooDefs.OpCode.setACL:
                txn = new SetACLTxn();
                break;
            case ZooDefs.OpCode.error:
                txn = new ErrorTxn();
                break;
            case ZooDefs.OpCode.multi:
                txn = new MultiTxn();
                break;
            default:
                throw new IOException("Unsupported Txn with type = %d" + hdr.getType());
        }

        if(txn != null){
            try{
                txn.deserialize(ia, "txn");
            }catch (Exception e){
                // perhaps this is a V0 Create
                if(hdr.getType() == ZooDefs.OpCode.create){
                    CreateTxn create = (CreateTxn)txn;
                    bais.reset();
                    CreateTxnV0 createv0 = new CreateTxnV0();
                    createv0.deserialize(ia, "txn");
                    // cool now make it V1, a-1 parentCVersion will
                    // trigger fixup processing in processTxn
                    create.setPath(createv0.getPath());
                    create.setData(createv0.getData());
                    create.setEphemeral(createv0.getEphemeral());
                    create.setParentCVersion(-1);
                }else{
                    throw e;
                }
            }
        }
        return txn;
    }

    public static void deserializeSnapshot(DataTree dt, InputArchive ia,
                                           Map<Long, Integer> sessions) throws IOException{
        int count = ia.readInt("count");
        while(count > 0){
            long id = ia.readLong("id");
            int to = ia.readInt("timeout");
            sessions.put(id, to);
            ZooTrace.logTraceMessage(LOG, ZooTrace.SESSION_TRACE_MASK,
                    "loadData -- session in archive: " + id+ " with timeout: " + to);
            count--;
        }
        dt.deserialize(ia, "tree");
    }

    public static void serializeSnapshot(DataTree dt, OutputArchive oa,
                                         Map<Long, Integer> sessions) throws IOException{
        HashMap<Long, Integer> sessSnap = new HashMap<>(sessions);
        oa.writeInt(sessSnap.size(), "count");
        for(Map.Entry<Long, Integer> entry : sessSnap.entrySet()){
            oa.writeLong(entry.getKey().longValue(), "id");
            oa.writeInt(entry.getKey().intValue(), "timeout");
        }
        dt.serialize(oa, "tree");
    }


}
