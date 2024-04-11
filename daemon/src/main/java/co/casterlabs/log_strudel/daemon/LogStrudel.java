package co.casterlabs.log_strudel.daemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.auth0.jwt.interfaces.JWTVerifier;

import co.casterlabs.log_strudel.daemon.config.Config;
import lombok.NonNull;
import lombok.experimental.StandardException;

public class LogStrudel {
    public static Config config;
    public static Heartbeat heartbeat;
    public static JWTVerifier verifier;
    public static Connection db;

    public static List<DatabaseRow> query(@NonNull String sql, @Nullable Object... params) throws DatabaseException {
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            for (int idx = 0; idx < params.length; idx++) {
                Object obj = params[idx];

                if (obj instanceof byte[]) {
                    statement.setBytes(idx + 1, (byte[]) obj);
                    continue;
                }

                statement.setObject(idx + 1, obj);
            }
            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData metadata = resultSet == null ? null : resultSet.getMetaData();

            if (metadata == null) {
                return Collections.emptyList();
            }

            String[] columns = new String[metadata.getColumnCount()];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = metadata.getColumnLabel(i + 1);
            }

            List<DatabaseRow> rows = new LinkedList<DatabaseRow>();
            while (resultSet.next()) {
                DatabaseRow row = new DatabaseRow();
                for (String columnName : columns) {
                    row.put(
                        columnName,
                        resultSet.getObject(columnName)
                    );
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @StandardException
    public static class DatabaseException extends Exception {
        private static final long serialVersionUID = 6248204611557908581L;

    }

    public static class DatabaseRow extends HashMap<String, Object> {
        private static final long serialVersionUID = -5171461231167019370L;

    }

}
