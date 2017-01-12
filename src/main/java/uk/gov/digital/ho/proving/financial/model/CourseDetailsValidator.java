package uk.gov.digital.ho.proving.financial.model;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author Home Office Digital
 */
public class CourseDetailsValidator implements ConstraintValidator<CourseDetails, Course> {

    public static final String COURSE_START_DATE_PARAM = "courseStartDate";
    public static final String COURSE_END_DATE_PARAM = "courseEndDate";
    public static final String ORIGINAL_COURSE_START_DATE_PARAM = "originalCourseStartDate";

    public static final String IS_NOT_ALLOWED = " is not allowed";
    public static final String IS_REQUIRED = " is required";
    public static final String STUDENT_TYPE_DOCTORATE = "DOCTORATE";

    @Override
    public void initialize(CourseDetails constraintAnnotation) {
    }

    @Override
    public boolean isValid(Course value, ConstraintValidatorContext context) {

        if(STUDENT_TYPE_DOCTORATE.equalsIgnoreCase(value.getStudentType())){

            if(value.getCourseStartDate() != null){
                return notAllowed(COURSE_START_DATE_PARAM, context);
            }
            if(value.getCourseEndDate() != null){
                return notAllowed(COURSE_END_DATE_PARAM, context);
            }
            if(value.getOriginalCourseStartDate() != null){
                return notAllowed(ORIGINAL_COURSE_START_DATE_PARAM, context);
            }

            return true;

        } else {

            if(value.getCourseStartDate() == null){
                return required(COURSE_START_DATE_PARAM, context);
            }
            if(value.getCourseEndDate() == null){
                return required(COURSE_END_DATE_PARAM, context);
            }
        }

        return true;
    }

    private boolean notAllowed(String parameter, ConstraintValidatorContext context) {
        return reject (parameter, context, IS_NOT_ALLOWED);
    }

    private boolean required(String parameter, ConstraintValidatorContext context) {
        return reject (parameter, context, IS_REQUIRED);
    }

    private boolean reject(String parameter, ConstraintValidatorContext context, String reason) {

        context.disableDefaultConstraintViolation();

        context
            .buildConstraintViolationWithTemplate(parameter + reason)
            .addPropertyNode(parameter)
            .addConstraintViolation();

        return false;
    }
}
