package com.lami.tuomatuo.search.base.solr;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lami.tuomatuo.utils.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Created by xujiankang on 2016/4/14.
 */
public class SolrStatementUtils
{
    public static final String NUMBER_PATTERN_1 = "####.0000";
    /**
     * 匹配任意表达式
     *
     * @return
     */
    public static final String generateMatchAnyStatement()
    {
        return new StringBuilder(SolrSynaxConstant.ANY).append(SolrSynaxConstant.MATCH).append(SolrSynaxConstant.ANY).toString();
    }

    /**
     * 生成最基本的Solr匹配表达式
     *
     * field:value
     *
     * @param field
     * @param value
     * @return
     */
    public static final String generateBaseMatchStatement(String field, Object value)
    {
        if (field != null && !"".equals(field) && value != null && !"".equals(value))
        {
            return new StringBuilder(field).append(SolrSynaxConstant.MATCH).append(value.toString()).toString();
        }
        return null;
    }

    /**
     * 生成Solr的模糊匹配表达式
     *
     * @param field
     * @param value
     * @return
     */
    public static final String generateBlurMatchStatement(String field, Object value)
    {
        if (field != null && !"".equals(field) && value != null && !"".equals(value))
        {
            return new StringBuilder(field).append(SolrSynaxConstant.MATCH).append(generateBlurValue(value.toString())).toString();
        }
        return null;
    }

    /**
     * 生成范围匹配表达式
     *
     * field:[start TO end]
     *
     * @param field
     * @param start
     * @param end
     * @return
     */
    public static final String generateRangeMatchStatement(String field, Double start, Double end)
    {
        if (field != null)
        {
            StringBuilder statement = new StringBuilder();
            statement = statement.append(SolrSynaxConstant.BRACKETS_START)
                    .append((start != null) ? formatNumber(start, NUMBER_PATTERN_1) : SolrSynaxConstant.ANY)
                    .append(SolrSynaxConstant.TO)
                    .append((end != null) ? formatNumber(end, NUMBER_PATTERN_1) : SolrSynaxConstant.ANY)
                    .append(SolrSynaxConstant.BRACKETS_END);
            return generateBaseMatchStatement(field, statement.toString());
        }
        return null;
    }

    /**
     * 多个且条件拼装成对应的查询表达式
     *
     * @param caseMap
     * @return
     */
    public static final String generateAndMatchStatement(Map<String, String> caseMap)
    {
        if (caseMap != null && !caseMap.isEmpty())
        {
            StringBuilder statement = new StringBuilder();
            Set<Entry<String, String>> entries = caseMap.entrySet();
            int i = 0;
            for (Entry<String, String> entry : entries)
            {
                String key = entry.getKey();
                String value = entry.getValue();
                if (i == 0)
                {
                    statement.append(generateBaseMatchStatement(key, (value != null ? value : SolrSynaxConstant.ANY)));
                }
                else
                {
                    statement.append(SolrSynaxConstant.AND).append(generateBaseMatchStatement(key, (value != null ? value : SolrSynaxConstant.ANY)));
                }
                i++;
            }
            return statement.toString();
        }
        return null;
    }

    /**
     * 多个或条件拼装成对应的查询表达式
     *
     * @param caseMap
     * @return
     */
    public static final String generateOrMatchStatement(Map<String, String> caseMap)
    {

        if (caseMap != null && !caseMap.isEmpty())
        {
            StringBuilder statement = new StringBuilder();
            Set<Entry<String, String>> entries = caseMap.entrySet();
            int i = 0;
            for (Entry<String, String> entry : entries)
            {
                String key = entry.getKey();
                String value = entry.getValue();
                if (i == 0)
                {
                    statement.append(generateBaseMatchStatement(key, (value != null ? value : SolrSynaxConstant.ANY)));
                }
                else
                {
                    statement.append(SolrSynaxConstant.OR).append(generateBaseMatchStatement(key, (value != null ? value : SolrSynaxConstant.ANY)));
                }
                i++;
            }
            return statement.toString();
        }
        return null;
    }

    /**
     * 生成模糊值匹配值*...*
     *
     * @param value
     * @return
     */
    public static final String generateBlurValue(String value)
    {
        if (StringUtils.isNotEmpty(value))
        {
            StringBuilder result = new StringBuilder(SolrSynaxConstant.ANY).append(ClientUtils.escapeQueryChars(value)).append(SolrSynaxConstant.ANY);
            return result.toString();
        }
        else
        {
            return SolrSynaxConstant.ANY;
        }
    }

    /**
     * 根据传入的语句拼装组合且语句
     *
     * @param statementList
     * @return
     */
    public static final String generateAndQueryByList(List<String> statementList)
    {
        if (CollectionUtils.isNotEmpty(statementList))
        {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < statementList.size(); i++)
            {
                if (query.length() == 0)
                {
                    query.append(" (").append(statementList.get(i)).append(") ");
                }
                else
                {
                    query.append(SolrSynaxConstant.AND).append(" (").append(statementList.get(i)).append(") ");
                }
            }
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * 根据传入的语句拼装组合且语句(不带括号)
     *
     * @param statementList
     * @return
     */
    public static final String generateAndQueryByListWithOutBracket(List<String> statementList)
    {
        if (CollectionUtils.isNotEmpty(statementList))
        {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < statementList.size(); i++)
            {
                if (query.length() == 0)
                {
                    query.append(statementList.get(i));
                }
                else
                {
                    query.append(SolrSynaxConstant.AND).append(statementList.get(i));
                }
            }
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * 根据传入的语句拼装组合且语句取反
     *
     * @param statementList
     * @return
     */
    public static final String generateNotEqualQueryByList(List<String> statementList)
    {
        if (CollectionUtils.isNotEmpty(statementList))
        {
            StringBuilder query = new StringBuilder();
            query.append(SolrSynaxConstant.NOT).append("(");
            for (int i = 0; i < statementList.size(); i++)
            {
                if (i == 0)
                {
                    query.append(statementList.get(i));
                }
                else
                {
                    query.append(SolrSynaxConstant.AND).append(statementList.get(i));
                }
            }
            query.append(")");
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * 生成join表达式 {!join from=fromField to=toField}caseStatement
     *
     * @param fromField
     * @param toField
     * @param caseStatement
     * @return
     */
    public static final String generateJoinStatement(String fromField, String toField, String caseStatement)
    {
        if (StringUtils.isNotEmpty(fromField) && StringUtils.isNotEmpty(toField))
        {
            StringBuilder statement = new StringBuilder();
            statement.append("{!join from=").append(fromField).append(" to=").append(toField).append("}").append("(").append(caseStatement)
                    .append(")");
            return statement.toString();
        }

        return null;
    }

    /**
     * 根据传入的语句组合拼装成或语句
     *
     * @param statementList
     * @return
     */
    public static final String generateOrQueryByList(List<String> statementList)
    {
        if (CollectionUtils.isNotEmpty(statementList))
        {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < statementList.size(); i++)
            {
                if (query.length() == 0)
                {
                    query.append(" (").append(statementList.get(i)).append(") ");
                }
                else
                {
                    query.append(SolrSynaxConstant.OR).append(" (").append(statementList.get(i)).append(") ");
                }
            }
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * 根据传入的语句组合拼装成或语句
     *
     * @param statementList
     * @return
     */
    public static final String generateOrQueryByValues(String field, List<String> values)
    {
        if (CollectionUtils.isNotEmpty(values))
        {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < values.size(); i++)
            {
                if (query.length() == 0)
                {
                    query.append(" (").append(generateBaseMatchStatement(field, values.get(i))).append(") ");
                }
                else
                {
                    query.append(SolrSynaxConstant.OR).append(" (").append(generateBaseMatchStatement(field, values.get(i))).append(") ");
                }
            }
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * 根据传入的语句组合拼装成或的模糊匹配语句
     *
     * @param statementList
     * @return
     */
    public static final String generateOrQueryByBlurValues(String field, List<String> values)
    {
        if (CollectionUtils.isNotEmpty(values))
        {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < values.size(); i++)
            {
                if (query.length() == 0)
                {
                    query.append(" (").append(generateBlurMatchStatement(field, values.get(i))).append(") ");
                }
                else
                {
                    query.append(SolrSynaxConstant.OR).append(" (").append(generateBaseMatchStatement(field, values.get(i))).append(") ");
                }
            }
            return query.toString();
        }
        return generateMatchAnyStatement();
    }

    /**
     * @description: long类型字段范围
     * @param field
     * @param start
     * @param end
     * @return
     */
    public static final String generateLongRangeMatchStatement(String field, Long start, Long end)
    {
        if (field != null)
        {
            StringBuilder statement = new StringBuilder();
            statement = statement.append(SolrSynaxConstant.BRACKETS_START)
                    .append((start != null) ? formatLong(start) : SolrSynaxConstant.ANY)
                    .append(SolrSynaxConstant.TO)
                    .append((end != null) ? formatLong(end) : SolrSynaxConstant.ANY)
                    .append(SolrSynaxConstant.BRACKETS_END);
            return generateBaseMatchStatement(field, statement.toString());
        }
        return null;
    }
    /**
     * 数字格式化
     *
     * @param number
     * @param pattern
     * @return
     */
    public static final String formatNumber(Double number, String pattern)
    {
        if (number == null)
        {
            number = 0.0;
        }
        if (pattern == null || "".equals(pattern))
        {
            pattern = NUMBER_PATTERN_1;
        }
        return new DecimalFormat(pattern).format(number);
    }

    /**
     * 数字格式化
     *
     * @param number
     * @param pattern
     * @return
     */
    public static final String formatDecNumber(Double number, String pattern)
    {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        if (number == null)
        {
            number = 0.0;
        }
        if (pattern == null || "".equals(pattern))
        {
            pattern = NUMBER_PATTERN_1;
        }
        else
        {
            decimalFormat.applyPattern(pattern);
        }
        return decimalFormat.format(number);
    }
    /**
     * 数字格式化
     *
     * @param number
     * @param pattern
     * @return
     */
    public static final String formatLong(Long number)
    {
        if (number == null)
        {
            number = 0L;
        }

        return number.toString();
    }

    /**
     * 检测当前字符串能不能转化为对应的数字
     *
     * @param text
     * @return
     */
    public static final Boolean checkTextToNum(String text)
    {
        try
        {
            Double.parseDouble(text);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}