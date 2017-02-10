Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate result page.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario Outline: Main applicant - Consent granted, balances API invoked and data received - display result page

        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the api threshold response will be t2
        And caseworker is using the financial status service ui
        And the caseworker selects <Tier>
        And <Applicant> type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
       # And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date | 30/06/2016 |
            | End Date                | 31/05/2016 |
            | Dependants              | 0          |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 03/03/2016 to 31/05/2016 |
            | Dependants                 | 0                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

        Examples:
            | Tier      | Applicant |
            | Tier two  | Main      |
            | Tier five | Dependant |


    Scenario: Tier 4 Non-Doctorate - Consent granted, balances API invoked and data received - display result page

        Given the api health check response has status 200
        And the api daily balance response will Pass
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        And the non-doctorate student type is chosen
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        When the financial status check is performed with
            | Application raised date         | 05/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 1          |
            | Continuation Course             | No         |
            | Course type                     | Main       |
        Then the service displays the following result
            | Outcome | Passed |
        And the result table contains the following
            | Total funds required       | £16,090.00               |
            | Account holder name        | Laura Taylor             |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Estimated leave end date   | 22/10/2017               |
            | Course length              | 7 (limited to 9)         |
        And the service displays the following page content
            | Application Raised Date         | 05/06/2016                   |
            | Dependants                      | 1                            |
            | In London                       | Yes                          |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Course dates checked            | 30/05/2016 to 30/11/2016     |
            | Continuation Course             | No                           |
            | Course type                     | Main course                  |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Tier                            | Tier 4 (General)             |
            | Applicant type                  | General student              |
