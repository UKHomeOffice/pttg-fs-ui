package uk.gov.digital.ho.proving.financial.model;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CourseSpecValidator implements ConstraintValidator<CourseSpec, Course> {

    public static final String IS_NOT_ALLOWED = " is not allowed";
    public static final String IS_REQUIRED = " is required";

    @Override
    public void initialize(CourseSpec constraintAnnotation) {
    }

    @Override
    public boolean isValid(Course value, ConstraintValidatorContext context) {

        if(value.getStudentType().equalsIgnoreCase("DOCTORATE")){

            if(value.getCourseStartDate() != null){
                return reject("courseStartDate", context, IS_NOT_ALLOWED);
            }
            if(value.getCourseEndDate() != null){
                return reject("courseEndDate", context, IS_NOT_ALLOWED);
            }
            if(value.getContinuationEndDate() != null){
                return reject("continuationEndDate", context, IS_NOT_ALLOWED);
            }


        } else {

            if(value.getCourseStartDate() == null){
                return reject("courseStartDate", context, IS_REQUIRED);
            }
            if(value.getCourseEndDate() == null){
                return reject("courseEndDate", context, IS_REQUIRED);
            }
        }

        return true;
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
