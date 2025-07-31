package org.example.complete_ums.Databases;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * A functional interface for processing a Java SQL ResultSet.
 * This allows you to define custom logic for mapping a ResultSet to
 any Java object.
 * @param <T> The type of the object that the ResultSet will be mapped
to.
 */
@FunctionalInterface
public interface ResultSetProcessor<T> {
    T process(ResultSet rs) throws SQLException;
}
