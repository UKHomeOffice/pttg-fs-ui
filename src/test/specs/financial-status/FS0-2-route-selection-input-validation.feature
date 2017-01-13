Feature: Show clear error details when inputs are invalid - Tier 2, 4 or 5

    Acceptance criteria

    Fields mandatory to fill in:
    Application Type

######################### Validation on the Application type Field #########################

    Scenario: Case Worker does NOT select application type
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the student type choice is submitted
        Then the service displays the following error message
            | applicant-type-error | Select an option |
