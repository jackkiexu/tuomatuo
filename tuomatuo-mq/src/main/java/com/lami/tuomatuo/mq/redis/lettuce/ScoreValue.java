package com.lami.tuomatuo.mq.redis.lettuce;

/**
 * Created by xjk on 9/16/16.
 */
public class ScoreValue<V> {

    public double score;
    public V value;

    public ScoreValue(double score, V value) {
        this.score = score;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreValue<?> that = (ScoreValue<?>) o;

        if (Double.compare(that.score, score) != 0) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(score);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public String toString(){
        return String.format("(%f, %s)", score, value.toString());
    }
}
