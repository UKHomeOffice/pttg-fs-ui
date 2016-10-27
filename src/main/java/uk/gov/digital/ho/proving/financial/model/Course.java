package uk.gov.digital.ho.proving.financial.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
public final class Course {

    @NotNull(message = "Missing parameter")
    private Boolean inLondon;

    @NotNull(message = "Missing parameter")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseStartDate;

    @NotNull(message = "Missing parameter")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseEndDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate  continuationEndDate;


    @NotNull(message = "Missing parameter")
    private String studentType;

    public Course(){

    }

    public Course(Boolean inLondon, LocalDate courseStartDate, LocalDate courseEndDate, String studentType) {
        this(inLondon, courseStartDate, courseEndDate, null, studentType);
    }

    public Course(Boolean inLondon, LocalDate courseStartDate, LocalDate courseEndDate, LocalDate continuationEndDate, String studentType) {
        this.inLondon = inLondon;
        this.courseStartDate = courseStartDate;
        this.courseEndDate = courseEndDate;
        this.continuationEndDate = continuationEndDate;
        this.studentType = studentType;
    }

    public Boolean getInLondon() {
        return inLondon;
    }

    public void setInLondon(Boolean inLondon) {
        this.inLondon = inLondon;
    }

    public LocalDate getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(LocalDate courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public LocalDate getCourseEndDate() {
        return courseEndDate;
    }

    public void setCourseEndDate(LocalDate courseEndDate) {
        this.courseEndDate = courseEndDate;
    }

    public LocalDate getContinuationEndDate() {
        return continuationEndDate;
    }

    public void setContinuationEndDate(LocalDate continuationEndDate) {
        this.continuationEndDate = continuationEndDate;
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
            ", courseStartDate=" + courseStartDate +
            ", courseEndDate=" + courseEndDate +
            ", continuationEndDate=" + continuationEndDate +
            ", studentType='" + studentType + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (inLondon != null ? !inLondon.equals(course.inLondon) : course.inLondon != null) return false;
        if (courseStartDate != null ? !courseStartDate.equals(course.courseStartDate) : course.courseStartDate != null)
            return false;
        if (courseEndDate != null ? !courseEndDate.equals(course.courseEndDate) : course.courseEndDate != null)
            return false;
        if (continuationEndDate != null ? !continuationEndDate.equals(course.continuationEndDate) : course.continuationEndDate != null)
            return false;
        return studentType != null ? studentType.equals(course.studentType) : course.studentType == null;

    }

    @Override
    public int hashCode() {
        int result = inLondon != null ? inLondon.hashCode() : 0;
        result = 31 * result + (courseStartDate != null ? courseStartDate.hashCode() : 0);
        result = 31 * result + (courseEndDate != null ? courseEndDate.hashCode() : 0);
        result = 31 * result + (continuationEndDate != null ? continuationEndDate.hashCode() : 0);
        result = 31 * result + (studentType != null ? studentType.hashCode() : 0);
        return result;
    }
}
