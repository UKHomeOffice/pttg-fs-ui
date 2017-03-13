Feature: Tier 2 & Tier 5

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api threshold response will be t2
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given caseworker is on page t2/application/calc/main
        When the financial status check is performed
        Then the service displays the following result headers in order
            | Total funds required |
            | Result timestamp     |
        And the service displays the following criteria headers in order
            | Tier                    |
            | Applicant type          |
            | Application raised date |
            | Number of dependants    |




