Feature: Insufficient Records

    Scenario: Not enough records found
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And caseworker is using the financial status service ui
        And the caseworker selects Tier five
        And Dependant applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        Given the account does not have sufficient records
        When the financial status check is performed with
            | Application raised date         | 11/06/2016 |
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
        Then the service displays the following page content
            | Outcome | Not passed                                                          |
            | Outcome detail  | The records for this account does not cover the whole 90 day period |



