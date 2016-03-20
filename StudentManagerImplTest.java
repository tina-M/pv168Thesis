package thesisman;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ThesisMan.ServiceFailureException;
import ThesisMan.Student;
import ThesisMan.StudentManagerImpl;

/**
 *
 * @author Kristina Miklasova, 4333 83
 */
public class StudentManagerImplTest {
    
    private StudentManagerImpl manager;
    private DataSource dataSource;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /*
    @Before
    public void setUp() throws SQLException {
        manager = new StudentManagerImpl();
    }
    */
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE STUDENT ("
            + "id bignit primary key generated always as identity,"
            + "name varchar(30), surname varchar(30)").executeUpdate();
       }
        manager = new StudentManagerImpl(dataSource);
    }
    
    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE STUDENT").executeUpdate();
        }
    }
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        
        ds.setDatabaseName("memory:studentmgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Test
    public void createStudent() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        
        Long studentId = student.getId();
        assertThat(student.getId(), is(not(equalTo(null))));
        
        Student result = manager.getStudentById(studentId);
        assertThat(result, is(equalTo(student)));
        assertThat(result, is(not(sameInstance(student))));
        assertDeepEquals(student, result);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createNullStudent() throws ServiceFailureException {
        manager.createStudent(null);
    }
   
    @Test
    public void createStudentWithWrongId() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        student.setId(1L);
        
        expectedException.expect(IllegalArgumentException.class);        
        manager.createStudent(student);
    }
    
    @Test
    public void createStudentWithNullName() throws ServiceFailureException {
        Student student = newStudent(null, "Bloch");
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }
    
    @Test
    public void createStudentWithEmptyName() throws ServiceFailureException {
        Student student = newStudent("", "Bloch");
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }
    
    @Test
    public void createStudentWithNumericName() throws ServiceFailureException {
        Student student = newStudent("Jos69", "Bloch");
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }     
    
    @Test
    public void createStudentWithNullSurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", null);
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }   
    
    @Test
    public void createStudentWithEmptySurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", "");
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }    
        
    @Test
    public void createStudentWithNumericSurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", "B6l9o");
        expectedException.expect(IllegalArgumentException.class);
        manager.createStudent(student);
    }
    
    @Test
    public void getAllStudents() throws ServiceFailureException {
        assertTrue(manager.findAllStudents().isEmpty());
        
        Student s1 = newStudent("Joshua", "Bloch");
        Student s2 = newStudent("Martin", "Fowler");
        
        manager.createStudent(s1);
        manager.createStudent(s2);
        
        List<Student> expected = Arrays.asList(s1, s2);
        List<Student> actual = manager.findAllStudents();
        
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);
        
        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }
    
    @Test
    public void getStudentById() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");     
        manager.createStudent(student);
         
        Student result = manager.getStudentById(student.getId());
        
        assertEquals(student, result);
        assertDeepEquals(student, result);
    }
    
    @Test
    public void updateStudentName() throws ServiceFailureException {
        Student student1 = newStudent("Joshua", "Bloch");
        Student student2 = newStudent("Martin", "Fowler");
        
        manager.createStudent(student1);
        manager.createStudent(student2);
        Long studentId = student1.getId();
        
        student1.setName("George");
        manager.updateStudent(student1);
        
        student1 = manager.getStudentById(studentId);
        assertThat(student1.getName(), is(equalTo("George")));
        assertThat(student1.getSurname(), is(equalTo("Bloch")));
        
        assertDeepEquals(student2, manager.getStudentById(student2.getId()));
    }
    
    @Test
    public void updateStudentSurname() throws ServiceFailureException {
        Student student1 = newStudent("Joshua", "Bloch");
        Student student2 = newStudent("Martin", "Fowler");
        
        manager.createStudent(student1);
        manager.createStudent(student2);
        Long studentId = student1.getId();
        
        student1.setSurname("Bush");
        manager.updateStudent(student1); 
        
        student1 = manager.getStudentById(studentId);
        assertThat(student1.getName(), is(equalTo("Joshua")));
        assertThat(student1.getSurname(), is(equalTo("Bush")));
        
        assertDeepEquals(student2, manager.getStudentById(student2.getId()));
    }
   
    @Test (expected = IllegalArgumentException.class)
    public void updateWithNullStudent() throws ServiceFailureException {    
        manager.updateStudent(null);
    }
    
    @Test
    public void updateStudentWithNullName() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setName(null);
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void updateStudentWithEmptyName() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setName("");
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void updateStudentWithNumericName() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setName("J6sh9ua");
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void updateStudentWithEmptySurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setSurname("");
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void updateStudentWithNullSurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setSurname(null);
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void updateStudentWithNumericSurname() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        manager.createStudent(student);
        Long studentId = student.getId();
        
        student = manager.getStudentById(studentId);
        student.setSurname("B6lo9ch");
        expectedException.expect(IllegalArgumentException.class);
        manager.updateStudent(student);
    }
    
    @Test
    public void deleteStudent() throws ServiceFailureException {
        Student student1 = newStudent("Joshua", "Bloch");
        Student student2 = newStudent("Martin", "Fowler");
        
        manager.createStudent(student1);
        manager.createStudent(student2);
        
        assertNotNull(manager.getStudentById(student1.getId()));
        assertNotNull(manager.getStudentById(student2.getId()));
        
        manager.deleteStudent(student1);
        
        assertNull(manager.getStudentById(student1.getId()));
        assertNotNull(manager.getStudentById(student2.getId()));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void deleteNullStudent() throws ServiceFailureException {
        manager.deleteStudent(null);
    }
    
    @Test
    public void deleteStudentWithNullId() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        
        student.setId(null);
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteStudent(student);
    }
    
    @Test
    public void deleteStudentWithWrongId() throws ServiceFailureException {
        Student student = newStudent("Joshua", "Bloch");
        
        student.setId(1L);
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteStudent(student);
    }
    
    
    
    private static Student newStudent(String name, String surname) {
        Student student = new Student();
        student.setName(name);
        student.setSurname(surname);
        
        return student;
    }
    
    private static Comparator<Student> idComparator = new Comparator<Student>() {

        @Override
        public int compare(Student s1, Student s2) {
            return s1.getId().compareTo(s2.getId());
        }
    };

    private void assertDeepEquals(Student expected, Student actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSurname(), actual.getSurname());
    }
    
    private void assertDeepEquals(List<Student> expectedList, List<Student> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Student expected = expectedList.get(i);
            Student actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
}
