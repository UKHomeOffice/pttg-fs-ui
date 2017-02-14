Feature: Tier 2 & Tier 5

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the t2main student type is chosen
        And the caseworker selects the Yes, Check Barclays  radio button
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222223   |
            | DOB                     | 25/03/1987 |
            | Dependants              | 0          |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds for tier 2
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Not passed                                                            |
            | Page dynamic detail  | One or more daily closing balances are below the total funds required |
        And the service displays the following results headers in order
            | Account holder name   |
            | Total funds required  |
            | 90-day period checked |
            | Lowest balance        |
            | Result timestamp      |
        And the service displays the following your search headers in order
            | Application raised date |
            | Applicant type          |
            | Number of dependants              |
            | Sort code               |
            | Account number          |
            | Date of birth           |



