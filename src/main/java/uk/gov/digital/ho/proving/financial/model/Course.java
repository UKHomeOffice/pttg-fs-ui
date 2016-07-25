package uk.gov.digital.ho.proving.financial.model;

import javax.validation.constraints.NotNull;

/**
 * @Author Home Office Digital
 */
public final class Course {



    @NotNull(message = "Missing parameter")
    private Boolean inLondon;

    @NotNull(message = "Missing parameter")
    private Integer courseLength;

    @NotNull(message = "Missing parameter")
    private String studentType;

    public Course(){

    }

    public Course(Boolean inLondon, Integer courseLength, String studentType) {
        this.inLondon = inLondon;
        this.courseLength = courseLength;
        this.studentType = studentType;
    }

    public Boolean getInLondon() {
        return inLondon;
    }

    public void setInLondon(Boolean inLondon) {
        this.inLondon = inLondon;
    }

    public Integer getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(Integer courseLength) {
        this.courseLength = courseLength;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    @Override
    public String toString() {
        return "Course{" +
            "inLondon=" + inLondon +
            ", courseLength=" + courseLength +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course course = (Course) o;

        if (inLondon != null ? !inLondon.equals(course.inLondon) : course.inLondon != null)
            return false;
        if (courseLength != null ? !courseLength.equals(course.courseLength) : course.courseLength != null)
            return false;
        return studentType != null ? studentType.equals(course.studentType) : course.studentType == null;

    }

    @Override
    public int hashCode() {
        int result = inLondon != null ? inLondon.hashCode() : 0;
        result = 31 * result + (courseLength != null ? courseLength.hashCode() : 0);
        result = 31 * result + (studentType != null ? studentType.hashCode() : 0);
        return result;
    }
}
