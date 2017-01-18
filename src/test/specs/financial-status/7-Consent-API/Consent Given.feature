Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate result page.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario: Consent granted and display result page

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked
        And the default details are
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        And the financial status check is performed
        And the default details are
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
            | DOB                             | 25/03/1987 |
        And the Barclays Consent API response returns
            | status | "SUCCESS |
        When the Barclays Balances API is invoked
        Then the service receives balance data from Barclays
        And the service displays the result page including the results and your search headers




