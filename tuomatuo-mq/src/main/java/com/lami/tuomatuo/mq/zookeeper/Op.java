package com.lami.tuomatuo.mq.zookeeper;

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
}
