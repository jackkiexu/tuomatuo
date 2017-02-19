/**
 * Created by xjk on 10/22/16.
 *
 * rox java nio tutorial (http://rox-xmlrpc.sourceforge.net/niotut/)
 * 非常重要 (下面的)
 * http://flychao88.iteye.com/blog/2207499
 * 代码的 github 地址
 * https://github.com/jjenkov/java-nio-server
 * 注意点
 * http://codepub.cn/2016/02/01/Java-NIO-development-Precautions/
 * http://www.cnblogs.com/pingh/p/3224990.html
 *
 * General principles
 *
 * 1. Use a single selecting thread
 *      Although NIO selectors are threadsafe their key sets are not. The upshot of this is that if you try to build a solution that depends on multiple threads accessing
 *      your selector you ver quickly end up in one of the two situations
 *
 * 2. Modify the selector from the selecting thread only
 *
 * 3. Set OP_WRITE only when you have data readly
 *
 * 4. Alternate betweenn OP_READ and OP_WRITE
 *
 */
package com.lami.tuomatuo.mq.nio;