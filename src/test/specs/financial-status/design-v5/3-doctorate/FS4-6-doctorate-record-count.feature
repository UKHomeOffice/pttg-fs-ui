Feature: Insufficient Records

    Scenario: Not enough records found
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen
        Given the account does not have sufficient records
        When the financial status check is performed with
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following page content
            | Page dynamic heading | This account has been open less than 28 days                                                           |
            | Page Dynamic detail  | 27 records available |
        And the service displays the following your search data
            | Sort Code      | 11-11-11 |
            | Account Number | 11111111 |




