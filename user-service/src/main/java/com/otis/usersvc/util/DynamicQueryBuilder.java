package com.otis.usersvc.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicQueryBuilder {
    private final String baseTable;
    private final List<String> selectColumns;
    private final List<WhereClause> whereClauses;
    private String orderBy;
    private Integer limit;
    private Integer offset;

    public DynamicQueryBuilder(String baseTable) {
        this.baseTable = baseTable;
        this.selectColumns = new ArrayList<>();
        this.whereClauses = new ArrayList<>();
    }

    public DynamicQueryBuilder select(String... columns) {
        selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public DynamicQueryBuilder where(String column, String operator, Object value) {
        whereClauses.add(new WhereClause(column, operator, value));
        return this;
    }

    public DynamicQueryBuilder orderBy(String column, String direction) {
        this.orderBy = column + " " + direction;
        return this;
    }

    public DynamicQueryBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public DynamicQueryBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public String buildQuery() {
        StringBuilder sql = new StringBuilder("SELECT ");

        if (selectColumns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", selectColumns));
        }

        sql.append(" FROM ").append(baseTable);

        if (!whereClauses.isEmpty()) {
            sql.append(" WHERE ");
            List<String> conditions = new ArrayList<>();
            for (WhereClause clause : whereClauses) {
                conditions.add(clause.toSql());
            }
            sql.append(String.join(" AND ", conditions));
        }

        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }

        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }

        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }

    public void setParameters(PreparedStatement stmt) throws SQLException {
        int paramIndex = 1;
        for (WhereClause clause : whereClauses) {
            clause.setParameter(stmt, paramIndex++);
        }
    }

    public List<Object> getParameters() {
        List<Object> params = new ArrayList<>();
        for (WhereClause clause : whereClauses) {
            params.add(clause.value);
        }
        return params;
    }

    private static class WhereClause {
        final String column;
        final String operator;
        final Object value;

        WhereClause(String column, String operator, Object value) {
            this.column = column;
            this.operator = operator;
            this.value = value;
        }

        String toSql() {
            String castType = "";
            if (value instanceof java.util.UUID) {
                castType = "::uuid";
            }

            return switch (operator.toUpperCase()) {
                case "LIKE", "ILIKE" -> column + " " + operator + " ?";
                case "IN" -> column + " IN (?)";
                case "IS NULL" -> column + " IS NULL";
                case "IS NOT NULL" -> column + " IS NOT NULL";
                default -> column + " " + operator + " ?" + castType;
            };
        }

        void setParameter(PreparedStatement stmt, int index) throws SQLException {
            if (value == null) {
                stmt.setNull(index, java.sql.Types.NULL);
            } else if (value instanceof String stringValue) {
                stmt.setString(index, stringValue);
            } else if (value instanceof Integer integerValue) {
                stmt.setInt(index, integerValue);
            } else if (value instanceof Long longValue) {
                stmt.setLong(index, longValue);
            } else if (value instanceof java.util.UUID) {
                stmt.setObject(index, value);
            } else {
                stmt.setObject(index, value);
            }
        }
    }
}
