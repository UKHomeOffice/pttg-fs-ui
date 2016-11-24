Feature: Insufficient Information
    Tool identifies account number, sort code and date of birth combination does not exist with Barclay's

    # error message needs to be reviewed

    Scenario: No records exist within the period stated
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the non-doctorate student type is chosen
        Given no record for the account
        When the financial status check is performed with
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Total tuition fees              | 3000.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 99-99-99   |
            | Account number                  | 99999999   |
            | DOB                             | 29/07/1978 |
        #Then the service displays the account not found page
        And the service displays the following page content
            | Page dynamic heading | Invalid or inaccessible account                                                  |
            | Page Dynamic detail  | One or more of the following conditions prevented us from accessing the account: |
        And the service displays the following your search data
            | Sort Code      | 99-99-99 |
            | Account Number | 99999999 |
            | DOB            | 29/07/1978 |
