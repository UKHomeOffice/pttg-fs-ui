Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate result page.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario Outline: Main applicant - Consent granted, balances API invoked and data received - display result page

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the caseworker selects <Tier>
        And <Applicant> type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date         | 30/06/2016 |
            | End Date                        | 31/05/2016 |
            | Dependants                      | 0          |
        Then the Consent Pending page is displayed
        And the service displays the following result
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
        Examples:
            | Tier      | Applicant     |
            | Tier two  | Main          |
            | Tier five | Dependent     |
