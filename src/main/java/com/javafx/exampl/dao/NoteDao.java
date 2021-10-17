package com.javafx.exampl.dao;

import com.javafx.exampl.entity.Note;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    private static final String URL = "jdbc:mysql://localhost:3306/car_shop";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static final String INSERT_QUERY = "INSERT INTO note(description, created_time) VALUES (?, ?)";
    public static final String SELECT_ALL_QUERY = "SELECT id, description, created_time FROM note";
    public static final String DELETE_QUERY = "DELETE FROM note WHERE id = ?";

    public Note create(Note note) throws DaoException {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, note.getDescription());
            Timestamp timestamp = Timestamp.valueOf(note.getCreatedTime());
            preparedStatement.setTimestamp(2, timestamp);
            preparedStatement.execute();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            note.setId(id);
            return note;
        } catch (SQLException | ClassNotFoundException e) {
            throw new DaoException("Failed to connect");
        }
    }

    public List<Note> findAllNotes() throws DaoException {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet allNotesSet = statement.executeQuery(SELECT_ALL_QUERY);
            List<Note> noteList = new ArrayList<>();
            while (allNotesSet.next()) {
                Note note = new Note();
                note.setId(allNotesSet.getInt("id"));
                note.setDescription(allNotesSet.getString("description"));
                note.setCreatedTime(Timestamp.valueOf(allNotesSet.getString("created_time")).toLocalDateTime());
                noteList.add(note);
            }
            return noteList;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DaoException("Failed to connect");
        }
    }

    public void delete(Integer id) throws DaoException {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DaoException("Failed to connect");
        }
    }


    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
