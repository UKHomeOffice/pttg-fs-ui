Feature: Handle the responses from the Barclays Consent API & Balances API and display the result page

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display a result page when a 'success' status is received from the Consent API and the balances data has been received from the Balances API.

    Scenario: Consent granted and display result page

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked
        And the financial status check is performed
        And the Barclays Consent API response returns
            | status | "SUCCESS |
        And the Barclays Balances API is invoked
        When the service receives balance data from Barclays
        Then the service displays the result page including the results and your search headers




