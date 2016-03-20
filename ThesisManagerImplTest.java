package thesisman;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ThesisMan.ServiceFailureException;
import ThesisMan.Student;
import ThesisMan.StudentManagerImpl;
import thesismanager.Thesis;
import ThesisMan.ThesisManagerImpl;
import ThesisMan.Type;

/**
 * Tests for ThesisManager.
 * @author peter
 */
public class ThesisManagerImplTest {
    
    private ThesisManagerImpl manager;
    private StudentManagerImpl studentManager; 
    private Student student;
    
    @Before
    public void setUp() throws ServiceFailureException {
        manager = new ThesisManagerImpl();
        studentManager = new StudentManagerImpl();
        student = new Student();
        student.setName("Noel");
        student.setSurname("Mad");
        studentManager.createStudent(student);
    }
    
    @Rule
    public ExpectedException expectedExpection = ExpectedException.none();
    
    @Test
    public void testCreateThesis(){
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        
        assertThat(thesis.getId(), is(not(equalTo(null))));
        
        Thesis tester = manager.getThesisById(thesis.getId());
        
        assertThat(tester, is(equalTo(thesis)));
        assertThat(tester, is(not(sameInstance(thesis))));
        assertDeepEquals(thesis, tester);
    }
    
    @Test (expected = IllegalAccessException.class)
    public void testCreateNullThesis() throws Exception {
        manager.createThesis(null);
    }
    
    @Test (expected = IllegalAccessException.class)
    public void createThesisWithNullName() throws Exception {
        Thesis thesis = newThesis(null, Type.MASTER, 2012, student);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.createThesis(thesis);
    }
    
    @Test (expected = IllegalAccessException.class)
    public void createThesisWithEmptyName() throws Exception {
        Thesis thesis = newThesis("", Type.MASTER, 2012, student);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.createThesis(thesis);
    }
    
    @Test (expected = IllegalAccessException.class)
    public void createThesisWithNullType() throws Exception {
        Thesis thesis = newThesis("RocketScience", null, 2012, student);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.createThesis(thesis);
    }
    
    @Test (expected = IllegalAccessException.class)
    public void createThesisWithNegativeYear() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, -256, student);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.createThesis(thesis);
    }
    
     @Test (expected = IllegalAccessException.class)
    public void createThesisWithNullAuthor() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, null);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.createThesis(thesis);
    }
    
    @Test
    public void updateThesisName() {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis.setName("NoScience");
        manager.updateThesis(thesis);
        thesis = manager.getThesisById(thesisId);
        
        assertThat(thesis.getName(), is(equalTo("NoScience")));
        assertThat(thesis.getAuthor(), is(equalTo(student)));
        assertThat(thesis.getType(), is(equalTo(Type.MASTER)));
        assertThat(thesis.getYear(), is(equalTo(2012)));       
    }
    
    @Test
    public void updateThesisType() {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis.setType(Type.BACHELOR);
        manager.updateThesis(thesis);
        thesis = manager.getThesisById(thesisId);
        
        assertThat(thesis.getName(), is(equalTo("RocketScience")));
        assertThat(thesis.getAuthor(), is(equalTo(student)));
        assertThat(thesis.getType(), is(equalTo(Type.MASTER)));
        assertThat(thesis.getYear(), is(equalTo(2012)));       
    }
    
    @Test
    public void updateThesisYear() {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis.setYear(1876);
        manager.updateThesis(thesis);
        thesis = manager.getThesisById(thesisId);
        
        assertThat(thesis.getName(), is(equalTo("RocketScience")));
        assertThat(thesis.getAuthor(), is(equalTo(student)));
        assertThat(thesis.getType(), is(equalTo(Type.MASTER)));
        assertThat(thesis.getYear(), is(equalTo(1876)));       
    }
    
    @Test
    public void updateThesisAuthor() {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2001, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        Student student2 = new Student();
        student2.setName("Joshua");
        student2.setSurname("Bloch");
        studentManager.createStudent(student2);
        
        thesis.setAuthor(student2);
        manager.updateThesis(thesis);
        thesis = manager.getThesisById(thesisId);
        
        assertThat(thesis.getName(), is(equalTo("RocketScience")));
        assertThat(thesis.getType(), is(equalTo(Type.MASTER)));
        assertThat(thesis.getYear(), is(equalTo(2001)));
        assertThat(thesis.getAuthor(), is(equalTo(student2)));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void updateWithNullThesis() {
        manager.updateThesis(null);
    }
               
    @Test
    public void updateThesisWithNullAuthor() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis = manager.getThesisById(thesisId);
        thesis.setAuthor(null);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.updateThesis(thesis);   
    }
    
    @Test
    public void updateThesisWithNegativeYear() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis = manager.getThesisById(thesisId);
        thesis.setYear(-69);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.updateThesis(thesis);  
    }
    
    @Test
    public void updateThesisWithEmptyName() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis = manager.getThesisById(thesisId);
        thesis.setName("");
        expectedExpection.expect(IllegalArgumentException.class);
        manager.updateThesis(thesis);  
    }
    
    @Test
    public void updateThesisWithNullName() throws Exception {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        Long thesisId = thesis.getId();
        
        thesis = manager.getThesisById(thesisId);
        thesis.setName(null);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.updateThesis(thesis);  
    }
  
    @Test
    public void deleteThesis() {
        Thesis thesis1 = newThesis("Effective Java", Type.MASTER, 2001, student);       
        Thesis thesis2 = newThesis("Refactoring", Type.BACHELOR, 1999, student);     
        manager.createThesis(thesis1);
        manager.createThesis(thesis2);
        
        assertNotNull(manager.getThesisById(thesis1.getId()));
        assertNotNull(manager.getThesisById(thesis2.getId()));
        
        manager.deleteThesis(thesis1);
        
        assertNull(manager.getThesisById(thesis1.getId()));
        assertNotNull(manager.getThesisById(thesis2.getId()));
    }
    
    @Test  (expected = IllegalAccessException.class)
    public void deleteNullThesis(){ 
         manager.deleteThesis(null);
    }
    
    @Test
    public void deleteThesisWithNullId() throws Exception {
        Thesis thesis = newThesis("Effective Java", Type.BACHELOR, 2001, student);
        
        thesis.setId(null);
        expectedExpection.expect(IllegalArgumentException.class);
        manager.deleteThesis(thesis);
    }
    
    @Test
    public void getThesisById() {
        Thesis thesis = newThesis("RocketScience", Type.MASTER, 2012, student);
        manager.createThesis(thesis);
        
        Thesis tester = manager.getThesisById(thesis.getId());
        
        assertEquals(thesis, tester);
        assertDeepEquals(thesis, tester);
    }
    
    @Test  (expected = IllegalAccessException.class)
    public void getThesisWithNullId(){
         manager.getThesisById(null);
    }
    
    @Test
    public void getAllTheses(){
        assertTrue(manager.findAllTheses().isEmpty());
            
        Thesis thesis1 = newThesis("RocketScience", Type.MASTER, 2012, student);
        Thesis thesis2 = newThesis("Math", Type.BACHELOR, 2003, student);
            
        manager.createThesis(thesis1);
        manager.createThesis(thesis2);
            
        List<Thesis> expected = Arrays.asList(thesis1, thesis2);
        List<Thesis> actual = manager.findAllTheses();
            
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);
            
        assertEquals(actual, expected);
        assertDeepEquals(expected, actual);    
    }
    
    @Test
    public void getThesisForStudent(){
        Thesis thesis1 = newThesis("RocketScience", Type.MASTER, 2012, student);
        Thesis thesis2 = newThesis("Math", Type.BACHELOR, 2003, student);
            
        manager.createThesis(thesis1);
        manager.createThesis(thesis2);
            
         List<Thesis> expected = Arrays.asList(thesis1, thesis2);
         List<Thesis> actual = manager.getThesesForStudent(student);
            
         Collections.sort(actual,idComparator);
         Collections.sort(expected,idComparator);
            
         assertEquals(actual, expected);
         assertDeepEquals(expected, actual);
    }
    
    private static Comparator <Thesis> idComparator = new Comparator<Thesis>(){
        @Override
        public int compare(Thesis t1, Thesis t2){
            return t1.getId().compareTo(t2.getId());
        }
    };
    
    public static Thesis newThesis(String name, Type type, int year, Student author){
         Thesis thesis = new Thesis();
         thesis.setName(name);
         thesis.setType(type);
         thesis.setYear(year);
         thesis.setAuthor(author);
         return thesis;
        
    }
    
     private void assertDeepEquals(List<Thesis> expectedList, List<Thesis> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Thesis expected = expectedList.get(i);
            Thesis actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    
    private void assertDeepEquals(Thesis expected, Thesis actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getAuthor(), actual.getAuthor());
    }
}