Feature: Insufficient Information
    Tool identifies account number and sort code does not exist with Barclay's

    Scenario: No records exist within the period stated
        Given caseworker is using the financial status service ui
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
        Then the service displays the account not found page
        And the service displays the following page content
            | Page dynamic heading | There is no record for the sort code and account number with Barclays                                                           |
            | Page dynamic detail  | We couldn't perform the financial requirement check as no information exists for sort code 99-99-99 and account number 99999999 |
        And the service displays the following your search data
            | Sort Code      | 99-99-99 |
            | Account Number | 99999999 |
            | DOB            | 29/07/1978 |
