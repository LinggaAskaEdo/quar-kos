package com.otis.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.otis.common.exception.SqlQueryLoadException;

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
			for (Map.Entry<String, String> entry : queries.entrySet()) {
				queryCache.put(fileName + ":" + entry.getKey(), entry.getValue());
			}

			return queryCache.get(cacheKey);
		} catch (Exception e) {
			throw new SqlQueryLoadException("Failed to load SQL query: " + queryName + " from " + fileName, e);
		}
	}

	private static Map<String, String> parseQueries(String content) {
		Map<String, String> queries = new HashMap<>();
		String[] lines = content.split("\n");
		StringBuilder currentQuery = new StringBuilder();
		String currentName = null;
		boolean inQuery = false;

		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.startsWith("-- @name:")) {
				// Save previous query
				if (currentName != null && !currentQuery.isEmpty()) {
					queries.put(currentName, cleanQuery(currentQuery.toString()));
				}
				// Start new query
				currentName = trimmed.substring("-- @name:".length()).trim();
				currentQuery.setLength(0);
				inQuery = true;
			} else if (inQuery && !trimmed.startsWith("--")) {
				currentQuery.append(line).append("\n");
			}
		}

		// Save the last query
		if (currentName != null && !currentQuery.isEmpty()) {
			queries.put(currentName, cleanQuery(currentQuery.toString()));
		}

		return queries;
	}

	private static String cleanQuery(String rawQuery) {
		// Remove inline -- comments (including at end of line)
		String cleaned = rawQuery.replaceAll("--.*$", "");

		// Trim and remove trailing semicolon + whitespace
		cleaned = cleaned.trim();

		if (cleaned.endsWith(";")) {
			cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
		}

		return cleaned;
	}
}
