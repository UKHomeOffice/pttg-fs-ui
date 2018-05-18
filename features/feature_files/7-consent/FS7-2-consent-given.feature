Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate result page.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario Outline: T2 Main applicant - Consent granted, balances API invoked and data received - display result page

        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the api threshold response will be t<Tier>
        And caseworker is using the financial status service ui
        Given caseworker is on page t<Tier>/application/status/<Applicant>
        And the financial status check is performed with
            | Application raised date | 30/06/2016 |
            | End Date                | 31/05/2016 |
            | Dependants              | 1          |
            | DOB                     | 25/03/1987 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222223   |
        And after at least 1 seconds
        Then the service displays the following result
            | Outcome              | Passed       |
            | Account holder name  | Laura Taylor |
            | Total funds required | <Funds>      |
        Examples:
            | Tier | Applicant | Funds   |
            | 2    | main      | £945.00 |
            | 2    | dependant | £945.00 |
