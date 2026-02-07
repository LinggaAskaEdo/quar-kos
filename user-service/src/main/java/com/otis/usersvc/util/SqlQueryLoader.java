package com.otis.usersvc.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.otis.usersvc.exception.SqlQueryLoadException;

public class SqlQueryLoader {
    private SqlQueryLoader() {
    }

    private static final Map<String, String> queryCache = new HashMap<>();

    public static String loadQuery(String fileName, String queryName) {
        String cacheKey = fileName + ":" + queryName;

        if (queryCache.containsKey(cacheKey)) {
            return queryCache.get(cacheKey);
        }

        try (InputStream is = SqlQueryLoader.class.getClassLoader().getResourceAsStream("sql/" + fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            Map<String, String> queries = parseQueries(content);
            queryCache.putAll(queries);

            return queries.get(queryName);
        } catch (Exception e) {
            throw new SqlQueryLoadException("Failed to load SQL query: " + queryName + " from " + fileName, e);
        }
    }

    private static Map<String, String> parseQueries(String content) {
        Map<String, String> queries = new HashMap<>();
        String[] sections = content.split("-- @name:");

        for (int i = 1; i < sections.length; i++) {
            String section = sections[i].trim();
            int firstNewLine = section.indexOf('\n');
            if (firstNewLine > 0) {
                String name = section.substring(0, firstNewLine).trim();
                String query = section.substring(firstNewLine + 1).trim();

                // Remove any additional comments
                query = query.replaceAll("--[^\n]*\n", "\n").trim();

                queries.put(name, query);
            }
        }

        return queries;
    }
}
