package ThesisMan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 * This class implements StudentManager.
 * 
 * @author Kristina Miklasova, 4333 83
 */
public class StudentManagerImpl implements StudentManager {

    private final DataSource dataSource;
    
    public StudentManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void createStudent(Student student) throws ServiceFailureException {
        
        validate(student);
        if (student.getId() != null ){
            throw new IllegalArgumentException("grave id is already set");
        }
        
        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO STUDENT (name, surname) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            st.setString(1, student.getName());
            st.setString(2, student.getSurname());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert student " + student);
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inseting student " + student, ex);
        }
    }
    
    public void validate(Student student) throws IllegalArgumentException {
        if (student == null) {
            throw new IllegalArgumentException("student is null");
        }
        if (student.getName() == null) {
            throw new IllegalArgumentException("student name is null");
        }
        if (student.getSurname() == null ) {
            throw new IllegalArgumentException("student surname is null");
        }
        if (student.getName().equals("")) {
            throw new IllegalArgumentException("student name is empty");
        }
        if (student.getSurname().equals("")) {
            throw new IllegalArgumentException("student surname is empty");
        }
        if (!Pattern.matches("[a-zA-Z]+", student.getName())) {
            throw new IllegalArgumentException("student name contains digits");
        }
        if (!Pattern.matches("[a-zA-Z]+", student.getSurname())) {
            throw new IllegalArgumentException("student surname contains digits");
        }      
    }
    
    private Long getKey(ResultSet keyRS, Student student) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert student " + student
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert student " + student
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert student " + student
                    + " - no key found");
        }        
    }
    
    @Override
    public void updateStudent(Student student) throws ServiceFailureException {
        validate(student);
        if (student.getId() == null) {
            throw new IllegalArgumentException("student id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
             "UPDATE Student SET name = ?, surname = ? WHERE id = ?")) {
            
            st.setString(1, student.getName());
            st.setString(2, student.getSurname());
            st.setLong(3, student.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Student " + student + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating student " + student, ex);
        } 
    }

    @Override
    public void deleteStudent(Student student) throws ServiceFailureException {
        if (student == null) {
            throw new IllegalArgumentException("student is null");
        }
        if (student.getId() == null) {
            throw new IllegalArgumentException("student id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
            "DELETE FROM student WHERE id = ?")) {
            
            st.setLong(1, student.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Student " + student + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating student " + student, ex);
        }
    }

    @Override
    public Student getStudentById(Long id) throws ServiceFailureException {
        try ( Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
            "SELECT id,name, surname FROM student WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
            
                if (rs.next()) {
                    Student student = resultSetToStudent(rs);
                
                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + student + " and " + resultSetToStudent(rs));
                    }
                
                    return student;
                
                } else {
                        return null;
                }
                
        } catch (SQLException ex) {
                    throw new ServiceFailureException(
                    "Error when retrieving student with id " + id, ex);
        }
        
    }

    private Student resultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setName(rs.getString("name"));
        student.setSurname(rs.getString("surname"));
        return student;
    }
    
    @Override
    public List<Student> findAllStudents() throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
             "SELECT id,name,surname FROM student")) {
            
            ResultSet rs = st.executeQuery();
            
            List<Student> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToStudent(rs));
            }
            return result;
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all students", ex);
        }
    }   
    
}
