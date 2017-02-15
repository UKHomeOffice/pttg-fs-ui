Feature: Show clear error details when inputs are invalid - Tier 2, 4 or 5

    Background:
        Given the api health check response has status 200

        ##############  ########### Validation on the Barclays radio button Field - T2 #########################

    Scenario: Case Worker does NOT select an applicant type
        Given caseworker is on page t2
        When the submit button is clicked
        Then the service displays the following error message
            | applicant type-error | Select an option |

 ######################### Validation on the Barclays radio button Field - T4 #########################

    Scenario: Case Worker does NOT select Barclays radio button for T4
        Given caseworker is on page t4/main
        When the submit button is clicked
        Then the service displays the following error message
            | do check-error | Select an option |
