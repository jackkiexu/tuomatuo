package com.lami.tuomatuo.mq.jafka.utils;

/**
 * two elements tple
 *
 * Created by xjk on 2016/9/30.
 */
public class KV<K, V> {

    public K k;

    public V v;

    public KV(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((k == null)? 0: k.hashCode() );
        result = prime * result + ((v == null)? 0: v.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        KV other = (KV)obj;
        if(k == null){
            if(other.k != null) return false;
        }else if(!k.equals(other.k)){
            return false;
        }
        if(v == null){
            if(other.v != null) return false;
        }else if(!v.equals(other.v)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KV{" +
                "k=" + k +
                ", v=" + v +
                '}';
    }

    public static class StringKV extends KV<String, String>{
        public StringKV(String s, String s2) {
            super(s, s2);
        }
    }
}
