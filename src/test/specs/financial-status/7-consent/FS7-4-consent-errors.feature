Feature: Handle the responses from the Barclays Consent API & Balances API and display the appropriate error page.

    Scenario: Error code 400

        Given the api health check response has status 200
        And the api consent response will be 400
        And caseworker is using the financial status service ui
        And caseworker is on page t4/des/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        Then the service displays the following page content
            | Outcome        | Invalid or inaccessible account                                                  |
            | Outcome detail | One or more of the following conditions prevented us from accessing the account: |
