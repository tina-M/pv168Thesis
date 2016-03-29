package ThesisMan;

import ThesisManCommon.ServiceFailureException;
import java.util.List;

/**
 * Interface for student manager.
 * 
 * @author Kristina Miklasova, 4333 83
 */
public interface StudentManager {
    
    /**
     * Stores new student into database. Id for the new student is automatically
     * generated and stored into id attribute.
     * 
     * @param student student to be created
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when student is null, or student has already assigned id.
     */
    void createStudent(Student student) throws ServiceFailureException;
    
    /**
     * Updates student in database.
     * 
     * @param student updated student to be stored into database.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when student is null, or student has null id.
     */
    void updateStudent(Student student) throws ServiceFailureException;
    
    /**
     * Deletes student from database. 
     * 
     * @param student student to be deleted from db.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when student is null, or student has null id.
     */
    void deleteStudent(Student student) throws ServiceFailureException;
    
    /**
     * Returns student with given id.
     * 
     * @param id primary key of requested student.
     * @return student with given id or null if such student does not exist.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when given id is null.
     */
    Student getStudentById(Long id) throws ServiceFailureException;
    
     /**
     * Returns list of all students in the database.
     * 
     * @return list of all students in database.
     * @throws ServiceFailureException when db operation fails.
     */
    List<Student> findAllStudents() throws ServiceFailureException;
}
