package com.lami.tuomatuo.mq.zookeeper;

import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Interface definitions of asynchronous callbacks
 * An asynchronous callback is deferred to invoke after a function returns.
 * Asynchronous calls usually improve system efficiency an IO-related APIs
 * ZooKeeper provides asynchronous version as equivalent to synchronous APIs
 *
 * Created by xujiankang on 2017/3/16.
 */

public interface AsyncCallback {

    /**
     * This callback is used to retrieve the stat of the node.
     */
    interface StatCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#BADVERSION}
         * - The given version doesn't match the node's version
         * for some API calls.
         * </li>
         * </ul>
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param stat {@link org.apache.zookeeper.data.Stat} object of
         *             the node on given path.
         */
        public void processResult(int rc, String path, Object ctx, Stat stat);
    }

    /**
     * This callback is used to retrieve the data and stat of the node.
     */
    interface DataCallback extends AsyncCallback {
        /**
         * Process the result of asynchronous calls.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * </ul>
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param data The {@link org.apache.zookeeper.server.DataNode#data}
         *             of the node.
         * @param stat {@link org.apache.zookeeper.data.Stat} object of
         *             the node on given path.
         */
        public void processResult(int rc, String path, Object ctx, byte data[],
                                  Stat stat);
    }

    /**
     * This callback is used to retrieve the ACL and stat of the node.
     */
    interface ACLCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * </ul>
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param acl  ACL Id in
         *             {@link org.apache.zookeeper.ZooDefs.Ids}.
         * @param stat {@link org.apache.zookeeper.data.Stat} object of
         *             the node on given path.
         */
        public void processResult(int rc, String path, Object ctx,
                                  List<ACL> acl, Stat stat);
    }

    /**
     * This callback is used to retrieve the children of the node.
     */
    interface ChildrenCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * </ul>
         *
         * @param rc       The return code or the result of the call.
         * @param path     The path that we passed to asynchronous calls.
         * @param ctx      Whatever context object that we passed to
         *                 asynchronous calls.
         * @param children An unordered array of children of the node on
         *                 given path.
         */
        public void processResult(int rc, String path, Object ctx,
                                  List<String> children);
    }

    /**
     * This callback is used to retrieve the children and stat of the node.
     */
    interface Children2Callback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * See {@link org.apache.zookeeper.AsyncCallback.ChildrenCallback}.
         *
         * @param rc       The return code or the result of the call.
         * @param path     The path that we passed to asynchronous calls.
         * @param ctx      Whatever context object that we passed to
         *                 asynchronous calls.
         * @param children An unordered array of children of the node on
         *                 given path.
         * @param stat     {@link org.apache.zookeeper.data.Stat} object of
         *                 the node on given path.
         */
        public void processResult(int rc, String path, Object ctx,
                                  List<String> children, Stat stat);
    }

    /**
     * This callback is used to retrieve the name and stat of the node.
     */
    interface Create2Callback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * See {@link org.apache.zookeeper.AsyncCallback.StringCallback}.
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param name The name of the Znode that was created.
         *             On success, <i>name</i> and <i>path</i> are usually
         *             equal, unless a sequential node has been created.
         * @param stat {@link org.apache.zookeeper.data.Stat} object of
         *             the node on given path.
         */
        public void processResult(int rc, String path, Object ctx,
                                  String name, Stat stat);
    }

    /**
     * This callback is used to retrieve the name of the node.
     */
    interface StringCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NODEEXISTS}
         * - The node on give path already exists for some API calls.
         * </li>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * <li>
         * {@link
         * org.apache.zookeeper.KeeperException.Code#NOCHILDRENFOREPHEMERALS}
         * - an ephemeral node cannot have children. There is discussion in
         * community. It might be changed in the future.
         * </li>
         * </ul>
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param name The name of the Znode that was created.
         *             On success, <i>name</i> and <i>path</i> are usually
         *             equal, unless a sequential node has been created.
         */
        public void processResult(int rc, String path, Object ctx, String name);
    }

    /**
     * This callback doesn't retrieve anything from the node. It is useful
     * for some APIs that doesn't want anything sent back, e.g. {@link
     * org.apache.zookeeper.ZooKeeper#sync(String,
     * org.apache.zookeeper.AsyncCallback.VoidCallback, Object)}.
     */
    interface VoidCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * <p/>
         * On failure, rc is set to the corresponding failure code in
         * {@link org.apache.zookeeper.KeeperException}.
         * <ul>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NONODE}
         * - The node on given path doesn't exist for some API calls.
         * </li>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#BADVERSION}
         * - The given version doesn't match the node's version
         * for some API calls.
         * </li>
         * <li>
         * {@link org.apache.zookeeper.KeeperException.Code#NOTEMPTY}
         * - the node has children and some API calls cannnot succeed,
         * e.g. {@link
         * org.apache.zookeeper.ZooKeeper#delete(String, int,
         * org.apache.zookeeper.AsyncCallback.VoidCallback, Object)}.
         * </li>
         * </ul>
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         */
        public void processResult(int rc, String path, Object ctx);
    }

    /**
     * This callback is used to process the multiple results from
     * a single multi call.
     * See {@link org.apache.zookeeper.ZooKeeper#multi} for more information.
     */
    interface MultiCallback extends AsyncCallback {
        /**
         * Process the result of the asynchronous call.
         * <p/>
         * On success, rc is
         * {@link org.apache.zookeeper.KeeperException.Code#OK}.
         * All opResults are
         * non-{@link org.apache.zookeeper.OpResult.ErrorResult},
         *
         * <p/>
         * On failure, rc is a failure code in
         * {@link org.apache.zookeeper.KeeperException.Code}.
         * All opResults are
         * {@link org.apache.zookeeper.OpResult.ErrorResult}.
         * All operations will be rollback-ed even if operations
         * before the failing one were successful.
         *
         * @param rc   The return code or the result of the call.
         * @param path The path that we passed to asynchronous calls.
         * @param ctx  Whatever context object that we passed to
         *             asynchronous calls.
         * @param opResults The list of results.
         *                  One result for each operation,
         *                  and the order matches that of input.
         */
        public void processResult(int rc, String path, Object ctx,
                                  List<OpResult> opResults);
    }
}
