Feature: Tier 2 & Tier 5

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api threshold response will be t2
        And the api daily balance response will Pass
        And caseworker is using the financial status service ui
        And the caseworker selects Tier two
        And Main applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
            | DOB            | 25/03/1987 |
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds for tier 2
        When the financial status check is performed
        Then the service displays the following page content
            | Outcome        | Not passed                                                            |
            | Outcome detail | One or more daily closing balances are below the total funds required |
        And the service displays the following result headers in order
            | Account holder name   |
            | Total funds required  |
            | 90-day period checked |
            | Lowest balance        |
            | Result timestamp      |
        And the service displays the following criteria headers in order
            | Tier                    |
            | Applicant type          |
            | Application raised date |
            | Number of dependants    |
            | Sort code               |
            | Account number          |
            | Date of birth           |



