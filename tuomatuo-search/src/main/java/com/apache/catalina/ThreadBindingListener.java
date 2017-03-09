package com.apache.catalina;

/**
 * Callback for establishing naming association when entering the application
 * scope. This corresponds to setting the context classloader
 * Created by xjk on 3/6/17.
 */
public interface ThreadBindingListener {

    void bind();

    void unbind();

}
