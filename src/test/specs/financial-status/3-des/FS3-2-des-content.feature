Feature: Tier 4 doctorate extension scheme content (single current account with dependants)

    Background:
        Given the api health check response has status 200
       # Given the account has sufficient funds
        And the api consent response will be SUCCESS
        And caseworker is using the financial status service ui
        And the api threshold response will be t4
        And the api condition codes response will be 2--
        And caseworker is on page t4/application/status/main/des

 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | Application raised date | 29/06/2016 |
            | End date                | 30/05/2016 |
            | In London               | Yes        |
            | Accommodation fees paid | 100        |
            | Dependants              | 0          |
            | DOB                     | 25/03/1987 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222223   |
        Then the service displays the following page content
            | Outcome        | Not passed                                                            |
            | Outcome detail | One or more daily closing balances are below the total funds required |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Lowest balance           |
            | Condition code           |
            | Estimated leave end date |
            | Result timestamp         |
        And the service displays the following criteria headers in order
            | Tier                            |
            | Applicant type                  |
            | Student type                    |
            | Application raised date         |
            | In London                       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |



