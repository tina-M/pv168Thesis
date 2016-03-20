package ThesisMan;

import java.util.List;

/**
 * Interface for thesis manager.
 * 
 * @author Kristina Miklasova, 4333 83
 */
public interface ThesisManager {
    
     /**
     * Stores new thesis into database. Id for the new thesis is automatically
     * generated and stored into id attribute.
     * 
     * @param thesis thesis to be created
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when thesis is null, or thesis has already assigned id.
     */
    void createThesis(Thesis thesis) throws ServiceFailureException;
    
     /**
     * Updates thesis in database.
     * 
     * @param thesis updated thesis to be stored into database.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when thesis is null, or thesis has null id.
     */
    void updateThesis(Thesis thesis) throws ServiceFailureException;

    /**
     * Deletes thesis from database. 
     * 
     * @param thesis thesis to be deleted from db.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when thesis is null, or thesis has null id.
     */
    void deleteThesis(Thesis thesis) throws ServiceFailureException;
    
    /**
     * Returns thesis with given id.
     * 
     * @param id primary key of requested thesis.
     * @return thesis with given id or null if such thesis does not exist.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when given id is null.
     */
    Thesis getThesisById(Long id) throws ServiceFailureException;
    
    /**
     * Returns list of all thesis in the database for requested student.
     * 
     * @param student student for whom all his theses should be found in the database. 
     * @return list of all theses in the database for requested student.
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalArgumentException when student is null, or student has null id.
     */
    List<Thesis> getThesesForStudent(Student student) throws ServiceFailureException;
    
     /**
     * Returns list of all thesss in the database.
     * 
     * @return list of all thesss in database.
     * @throws ServiceFailureException when db operation fails.
     */
    List<Thesis> findAllTheses() throws ServiceFailureException;
}
