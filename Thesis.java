package ThesisMan;

import java.util.Objects;

/**
 * This entity represents Thesis. Thesis has some name, the year of publication, 
 * the type of thesis (enumeration - BACHELOR, MASTER or PHD) and author. One thesis
 * could belong to only one student.
 * 
 * @author Kristina Miklasova, 4333 83
 */
public class Thesis {
    
    private Long id;
    private String name;
    private int year;
    private Type type;
    private Student author;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Student getAuthor() {
        return author;
    }

    public void setAuthor(Student author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Thesis{" + "id=" + id + ", name=" + name + ", year=" + year + ", type=" + type + ", author=" + author + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Thesis)) {
            return false;
        }
        final Thesis other = (Thesis) obj;
        if (!getId().equals(other.getId())) {
            return false;
        }
        if (!getName().equals(other.getName())){
            return false;
        }
        if (getYear() != other.getYear()){
            return false;
        }
        if (!getType().equals(other.getType())){
            return false;
        }
        return getAuthor().equals(other.getAuthor());
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + this.year;
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.author);
        return hash;
    }
}
