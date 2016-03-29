package ThesisMan;

import ThesisManCommon.ValidationException;
import ThesisManCommon.ServiceFailureException;
import ThesisManCommon.IllegalEntityException;
import ThesisManCommon.EntityNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;


/**
 * Implements methods for ThesisManager.
 * @author peter
 */
public class ThesisManagerImpl implements ThesisManager {
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource databSource) {
        this.dataSource = databSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createThesis(Thesis thesis) throws ServiceFailureException {
        
        checkDataSource();
        validate(thesis);
        if (thesis.getId() != null) {
            throw new IllegalEntityException("thesis id is already set");
        }

        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO THESIS (name, type, yearOfPublication, authorId) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)){
            
            st.setString(1, thesis.getName());
            st.setString(2, thesis.getType().toString());
            st.setInt(3, thesis.getYear());
            st.setLong(4, thesis.getAuthor().getId());
                       
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                  throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert thesis " + thesis);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            thesis.setId(getKey(keyRS, thesis));
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inseting thesis " + thesis, ex);
        }     
    }
    
    public void validate(Thesis thesis) throws IllegalArgumentException {
        if (thesis == null) {
            throw new IllegalArgumentException("thesis is null");
        }
        if (thesis.getName() == null) {
            throw new ValidationException("thesis name is null");
        }
        if (thesis.getName().equals("")) {
            throw new ValidationException("thesis name is empty");
        }           
        if (thesis.getAuthor() == null) {
            throw new ValidationException("author is null");
        }
        if (thesis.getYear() < 0 ) {
            throw new ValidationException("year is negative");
        }
        if (thesis.getType() == null) {
            throw new ValidationException("type is null");
        }
    }

    @Override
    public void updateThesis(Thesis thesis) throws ServiceFailureException {
    
        checkDataSource();
        validate(thesis);
        if (thesis.getId() == null) {
            throw new IllegalEntityException("thesis id is null");
        }
        
        try(Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                "UPDATE Thesis SET name = ?, yearOfPublication = ?, type = ?,  authorId = ? WHERE id = ?")){
            
            st.setString(1, thesis.getName());
            st.setInt(2, thesis.getYear());
            st.setObject(3, thesis.getType().toString());
            st.setLong(4, thesis.getAuthor().getId());
            st.setLong(5, thesis.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Thesis " + thesis + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating thesis " + thesis, ex);
        }        
    }

    @Override
    public void deleteThesis(Thesis thesis) throws ServiceFailureException {
        checkDataSource();
        if (thesis == null) {
            throw new IllegalArgumentException("thesis is null");
        }
        if (thesis.getId() == null) {
            throw new IllegalEntityException("thesis id is null");
        }
        try (Connection connection = dataSource.getConnection();
               PreparedStatement st = connection.prepareStatement(
               "DELETE FROM thesis WHERE id = ?")){
            
            st.setLong(1, thesis.getId());
           
            int count = st.executeUpdate();
            if (count == 0) {
                throw new IllegalArgumentException("thesis " + thesis + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating thesis " + thesis, ex);
        }
    }

    @Override
    public Thesis getThesisById(Long id) throws ServiceFailureException {
        
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
             "SELECT id, name, type, yearOfPublication, authorId FROM thesis WHERE id = ?")) {
                            
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
               
                if (rs.next()) {                   
                    Thesis thesis = resultToThesis(rs, connection);
                   
                    if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + thesis + " and " + resultSetToThesis(rs));
                    }
                    
                    return thesis;
                    
                } else {
                    return null;
                }
                
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving student with id " + id, ex);
        } 
    }
    
    private Thesis resultToThesis(ResultSet rs, Connection conn) throws SQLException, ServiceFailureException {
        Thesis thesis = new Thesis();
        thesis.setId(rs.getLong("id"));
        thesis.setName(rs.getString("name"));
        thesis.setYear(rs.getInt("yearofpublication"));
        thesis.setType(Type.valueOf(rs.getString("type")));
        thesis.setAuthor(null);
       
        Long authorId = rs.getLong("authorid");
        
        if (authorId == null) {
            throw new IllegalArgumentException("authorId is null");
        }
        
        try (PreparedStatement st = conn.prepareStatement(
            "SELECT id, name, surname FROM student WHERE id = ?")) {
                st.setLong(1, authorId);
                ResultSet rset = st.executeQuery();
            
                if (rset.next()) {
                    Student student = resultSetToStudent(rset);
                    
                    if (rset.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + authorId + ", found " + student + " and " + resultSetToStudent(rset));
                    }
                
                    thesis.setAuthor(student);
                    return thesis;
           
                } else {
                    return null;
                }
                
        } catch (SQLException ex) {
                    throw new ServiceFailureException(
                    "Error when retrieving student with id " + authorId, ex);
        }
    }
    
    private Student resultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setName(rs.getString("name"));
        student.setSurname(rs.getString("surname"));
        
        return student;
    }
    
    private Thesis resultSetToThesis(ResultSet rs) throws SQLException {
        Thesis thesis = new Thesis();
        thesis.setId(rs.getLong("id"));
        thesis.setName(rs.getString("name"));
        thesis.setYear(rs.getInt("yearOfPublication"));
        thesis.setType(Type.valueOf(rs.getString("type")));

        return thesis;
    }
    
    @Override
    public List<Thesis> getAllTheses() throws ServiceFailureException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
             "SELECT id, name, yearOfPublication, type, authorId FROM thesis")) {
            
            ResultSet rs = st.executeQuery();
            
            List<Thesis> result = new ArrayList<>();

            while (rs.next()) {                   
                Thesis thesis = resultToThesis(rs, connection);       
                result.add(thesis);
                    
            } 
            return result;
            
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all students", ex);
        } 
    }

    @Override
    public List<Thesis> getThesesForStudent(Student student) throws ServiceFailureException {
        checkDataSource();
        if (student == null) {
            throw new IllegalArgumentException("student is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
             "SELECT id, name, yearOfPublication, type, authorId FROM thesis WHERE authorId = ?")) {
            
                st.setLong(1, student.getId());
                ResultSet rs = st.executeQuery();
                
                List<Thesis> result = new ArrayList<>();
                while (rs.next()) {                   
                    Thesis thesis = resultToThesis(rs, connection);
                   
                    result.add(thesis);  
                } 
                return result;
                               
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all students", ex);
        }
    }
    
     private Long getKey(ResultSet keyRS, Thesis thesis) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert thesist " + thesis
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert thesis " + thesis
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert thesis " + thesis
                    + " - no key found");
        }        
    }
     
}
