Feature: Insufficient Records

    Scenario: Not enough records found
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the doctorate student type is chosen
        Given the account does not have sufficient records
        When the financial status check is performed with
            | Application raised date         | 11/06/2016 |
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
            | DOB                             | 27/05/1986 |
        Then the service displays the following page content
            | Page dynamic heading | Not passed                                                          |
            | Page Dynamic detail  | The records for this account does not cover the whole 28 day period |
        And the service displays the following your search data
            | Sort Code      | 11-11-11   |
            | Account Number | 11111111   |
            | DOB            | 27/05/1986 |




