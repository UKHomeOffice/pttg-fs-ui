package uk.gov.digital.ho.proving.financial.model;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
public final class Course {

    @NotNull(message = "Missing parameter")
    private Boolean innerLondonBorough;

    @NotNull(message = "Missing parameter")
    private Integer courseLength;

    public Course(){

    }

    public Course(Boolean innerLondonBorough, Integer courseLength) {
        this.innerLondonBorough = innerLondonBorough;
        this.courseLength = courseLength;
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
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(innerLondonBorough, course.innerLondonBorough) &&
            Objects.equals(courseLength, course.courseLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerLondonBorough, courseLength);
    }
}
