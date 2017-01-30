Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate result page.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario: Consent granted, balances API invoked and data received - display result page

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And consent is sought for the following:
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        When caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date         | 30/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        Then the service displays the result page including the results and your search headers
            | Outcome                         | Passed                                                |
            | Account holder name             | Laura Taylor                                          |
            | Total funds required            | £16,090.00                                            |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                              |
            | Applicant type                  | Tier 4 (General) student (doctorate extension scheme) |
            | In London                       | Yes                                                   |
            | Accommodation fees already paid | £100.00 (limited to £1,265.00)                        |
            | Dependants                      | 0                                                     |
            | Sort code                       | 22-22-23                                              |
            | Account number                  | 22222223                                              |
            | DOB                             | 25/03/1987                                            |
            | Application raised date         | 30/05/2016                                            |
