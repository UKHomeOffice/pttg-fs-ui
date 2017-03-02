Feature: Insufficient Information
    Tool identifies account number, sort code and date of birth combination does not exist with Barclay's

    # error message needs to be reviewed

    Scenario: No records exist within the period stated
        Given the api health check response has status 200
        And the api consent response will be 404
        And caseworker is on page t4/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        Then the service displays the following page content
            | Outcome        | Invalid or inaccessible account                                                  |
            | Outcome detail | One or more of the following conditions prevented us from accessing the account: |
