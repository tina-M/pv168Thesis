package ThesisMan;

import java.util.Objects;

/**
 * This entity represents Student. Student has name and surname. One student
 * could have one or more theses.
 * 
 * @author Kristina Miklasova, 4333 83
 */
public class Student {
    
    private Long id;
    private String name;
    private String surname;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", name=" + name + ", surname=" + surname + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Student)) {
            return false;
        }
        
        final Student other = (Student) obj;
        // if (getId() != null ? !getId().equals(other.getId()) : other.getId() != null) return false;
        if (!this.id.equals(other.getId())) {
            return false;
        } 
        if (!this.name.equals(other.getName())) {
            return false;
        }
        if (!this.surname.equals(other.getSurname())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.surname);
        return hash;
    }
}
