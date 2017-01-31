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

        ######################### Validation on the Barclays radio button Field - T2 #########################

    Scenario: Case Worker does NOT select Barclays radio button for T2

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 2 <student-type> is chosen
        And the <page-title> is chosen
        And the Barclays radio button or the No radio button appears
        When the Check Barclays button is submitted
        Then the service displays the following error message
            | applicant-type-error | Select an option |

 ######################### Validation on the Barclays radio button Field - T4 #########################

    Scenario: Case Worker does NOT select Barclays radio button for T4

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 4 <student-type> is chosen
        And the <page-title> is chosen
        And the Barclays radio button or the No radio button appears
        When the Check Barclays button is submitted
        Then the service displays the following error message
            | applicant-type-error | Select an option |



         ######################### Validation on the Barclays radio button Field - T5 #########################

    Scenario: Case Worker does NOT select Barclays radio button for T5

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 5 <student-type> is chosen
        And the <page-title> is chosen
        And the Barclays radio button or the No radio button appears
        When the Check Barclays button is submitted
        Then the service displays the following error message
            | applicant-type-error | Select an option |
