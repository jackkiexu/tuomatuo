package com.lami.tuomatuo.search.base.solr;

/**
 * Created by xujiankang on 2016/4/14.
 */
public interface SolrSynaxConstant
{
    /**
     * 组查询进一步限制
     */
    public static final String GROUP_QUERY_PARAM = "group.query";
    /**
     * 组查询定义排序条件
     */
    public static final String GROUP_SORT_PARAM = "group.sort";
    /**
     * 组查询定义相应的数目
     */
    public static final String GROUP_LIMIT_PARAM = "group.limit";
    /**
     * 组查询定义分组列
     */
    public static final String GROUP_FIELD_PARAM = "group.field";
    /**
     * 组查询参数
     */
    public static final String GROUP_PARAM = "group";
    /**
     * 所要统计的列
     */
    public static final String STATS_FIELD_PARAM = "stats.field";
    /**
     * 统计参数
     */
    public static final String STATS_PARAM = "stats";
    /**
     * SOLR打分字段
     */
    public static final String SCORE_FIELD = " score ";
    /**
     * 匹配任意字符
     */
    public static final String ANY = "*";
    /**
     * 或的表达式
     */
    public static final String OR = " || ";
    /**
     * 且表达式
     */
    public static final String AND = " && ";
    /**
     * 不等表达式
     */
    public static final String NOT = " NOT ";
    /**
     * 匹配表达式
     */
    public static final String MATCH = ":";
    /**
     * 区间中的TO
     */
    public static final String TO = " TO ";
    /**
     * 中括号左边
     */
    public static final String BRACKETS_START = " [ ";
    /**
     * 中括号右边
     */
    public static final String BRACKETS_END = " ] ";
    /**
     * 真
     */
    public static final String TRUE = "true";
}
