Feature: Show clear error details when inputs are invalid - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

    Acceptance criteria

    Fields mandatory to fill in:
    Student Type

######################### Validation on the Student type Field #########################

    Scenario: Case Worker does NOT select student type
        Given caseworker is using the financial status service ui
        When the student type choice is submitted
        Then the service displays the following error message
            | student-type-error | Select an option |
