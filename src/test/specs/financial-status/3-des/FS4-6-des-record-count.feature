Feature: Insufficient Records

    Scenario: Not enough records found
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Fail-record-count
        And the api threshold response will be t4
        And caseworker is using the financial status service ui
        And caseworker is on page t4/status/dependant/des
        When the financial status check is performed with
            | Application raised date         | 11/06/2016 |
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following page content
            | Outcome        | Not passed                                                          |
            | Outcome detail | The records for this account does not cover the whole 28 day period |



