Feature: Insufficient Information - Tier 4 (General) Student In Country (single current account and no dependants)
    Tool identifies account number and sort code does not exit with Barclay's

    Fields mandatory to fill in:
    Maintenance Period End Date - Format should be dd/mm/yyyy
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Scenario: Caseworker enters account number and sort code where no records exist within the period stated (no test data for all 9's)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 10/06/2016 |
            | Inner London borough            | No         |
            | Course length                   | 2          |
            | Total tuition fees              | 3000.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 99-99-99   |
            | Account number                  | 99999999   |

        Then the service displays the account not found page
            | Page dynamic heading  | There is no record for the sort code and account number with Barclays                                                            |
            | Page dynamic detail | We couldn't perform the financial requirement check as no information exists for sort code 99-99-99 and account number 99999999. |
        And the service displays the following your search data
            | Sort Code      | 99-99-99 |
            | Account Number | 99999999 |
