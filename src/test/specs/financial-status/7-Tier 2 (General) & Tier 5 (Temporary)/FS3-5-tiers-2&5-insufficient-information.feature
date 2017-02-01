Feature: Insufficient Information
    Tool identifies account number, sort code and date of birth combination does not exist with Barclay's

    # error message needs to be reviewed

    Scenario: No records exist within the period stated
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the t2main student type is chosen
        And the caseworker selects the Yes, Check Barclays  radio button
        Given no record for the account
        When the financial status check is performed with
            | Application raised date | 30/06/2016 |
            | End date                | 10/06/2016 |
            | Sort code               | 99-99-99   |
            | Account number          | 99999999   |
            | DOB                     | 27/05/1986 |
            | Dependants              | 0          |
        Then the service displays the following page content
            | Page dynamic heading | Invalid or inaccessible account                                                  |
            | Page Dynamic detail  | One or more of the following conditions prevented us from accessing the account: |
        And the service displays the following your search data
            | Sort Code      | 99-99-99   |
            | Account Number | 99999999   |
            | DOB            | 27/05/1986 |

