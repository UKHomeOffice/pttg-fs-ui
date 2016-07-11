package uk.gov.digital.ho.proving.financial.model;

import javax.validation.constraints.NotNull;

/**
 * @Author Home Office Digital
 */
public final class Course {



    @NotNull(message = "Missing parameter")
    private Boolean innerLondonBorough;

    @NotNull(message = "Missing parameter")
    private Integer courseLength;

    @NotNull(message = "Missing parameter")
    private String studentType;

    public Course(){

    }

    public Course(Boolean innerLondonBorough, Integer courseLength, String studentType) {
        this.innerLondonBorough = innerLondonBorough;
        this.courseLength = courseLength;
        this.studentType = studentType;
    }

    public Boolean getInnerLondonBorough() {
        return innerLondonBorough;
    }

    public void setInnerLondonBorough(Boolean innerLondonBorough) {
        this.innerLondonBorough = innerLondonBorough;
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
            "innerLondonBorough=" + innerLondonBorough +
            ", courseLength=" + courseLength +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course course = (Course) o;

        if (innerLondonBorough != null ? !innerLondonBorough.equals(course.innerLondonBorough) : course.innerLondonBorough != null)
            return false;
        if (courseLength != null ? !courseLength.equals(course.courseLength) : course.courseLength != null)
            return false;
        return studentType != null ? studentType.equals(course.studentType) : course.studentType == null;

    }

    @Override
    public int hashCode() {
        int result = innerLondonBorough != null ? innerLondonBorough.hashCode() : 0;
        result = 31 * result + (courseLength != null ? courseLength.hashCode() : 0);
        result = 31 * result + (studentType != null ? studentType.hashCode() : 0);
        return result;
    }
}
