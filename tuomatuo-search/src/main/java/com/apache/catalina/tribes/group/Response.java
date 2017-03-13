package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.Member;
import lombok.Data;

import java.io.Serializable;

/**
 * A response object haolds a message from a responding partner
 * Created by xjk on 3/13/17.
 */
@Data
public class Response {

    private Member source;
    private Serializable message;

    public Response() {
    }

    public Response(Member source, Serializable message) {
        this.source = source;
        this.message = message;
    }


}
