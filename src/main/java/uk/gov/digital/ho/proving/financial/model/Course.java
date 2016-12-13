package uk.gov.digital.ho.proving.financial.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@CourseDetails
public final class Course {

    @NotNull(message = "Missing parameter")
    private Boolean inLondon;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseEndDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate originalCourseStartDate;

    @NotNull(message = "Missing parameter")
    private String studentType;

    private String courseType;


    public Course(){

    }

    public Course(Boolean inLondon, LocalDate courseStartDate, LocalDate courseEndDate, String studentType, String courseType) {
        this(inLondon, courseStartDate, courseEndDate, null, studentType, courseType);
    }

//    public Course(Boolean inLondon, LocalDate courseStartDate, LocalDate courseEndDate, LocalDate originalCourseStartDate, String studentType, String courseType) {
//        this.inLondon = inLondon;
//        this.courseStartDate = courseStartDate;
//        this.courseEndDate = courseEndDate;
//        this.originalCourseStartDate = originalCourseStartDate;
//        this.studentType = studentType;
//        this.courseType = courseType;
//    }

    public Course(Boolean inLondon, LocalDate courseStartDate, LocalDate courseEndDate, LocalDate originalCourseStartDate, String studentType, String courseType) {
        this.inLondon = inLondon;
        this.courseStartDate = courseStartDate;
        this.courseEndDate = courseEndDate;
        this.originalCourseStartDate = originalCourseStartDate;
        this.studentType = studentType;
        this.courseType = courseType;
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

    public LocalDate getOriginalCourseStartDate() {
        return originalCourseStartDate;
    }

    public void setOriginalCourseStartDate(LocalDate originalCourseStartDate) {
        this.originalCourseStartDate = originalCourseStartDate;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "Course{" +
            "inLondon=" + inLondon +
            ", courseStartDate=" + courseStartDate +
            ", courseEndDate=" + courseEndDate +
            ", originalCourseStartDate=" + originalCourseStartDate +
            ", studentType='" + studentType + '\'' +
            ", courseType='" + courseType + '\'' +
            '}';
    }
}
