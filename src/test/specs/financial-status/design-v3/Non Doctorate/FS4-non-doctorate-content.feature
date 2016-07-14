@DataDir=v2
Feature: Non Doctorate Content - Tier 4 (General) student (single current account and no dependants)

 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | Yes        |
            | Course length                   | 9          |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
            | Sort code                       | 22-22-22   |
            | Account number                  | 22222222   |
        Then the service displays the following result page content
            | Page dynamic heading | Passed                                          |
         ##   | Page heading         | Tier 4 (General) student                                        |
         ##   | Page sub heading     | Financial Status check                                          |
            | Page dynamic detail  | This applicant meets the financial requirements |
        And the service displays the following results headers in order
            | Total funds required  | £20,390.00               |
            | 28-day period checked | 03/05/2016 to 30/05/2016 |
        And the service displays the following your search headers in order
            | Inner London borough            | Yes       |
            | Course length                   | 9         |
            | Total tuition fees              | £9,755.00 |
            | Tuition fees already paid       | £500.00   |
            | Accommodation fees already paid | £250.00   |
            | Sort code                       | 22-22-22  |
            | Account number                  | 22222222  |

 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 07/06/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following result page content
            | Page dynamic heading | Not passed                                              |
          ##  | Page heading         | Tier 4 (General) student                                |
          ##  | Page sub heading     | Financial status check                                  |
            | Page dynamic detail  | This applicant does not meet the financial requirements |
        And the service displays the following results headers in order
            | Total funds required       | £16,090.00               |
            | 28-day period checked | 11/05/2016 to 07/06/2016 |
        And the service displays the following your search headers in order
            | Inner London borough            | Yes       |
            | Course length                   | 6         |
            | Total tuition fees              | £8,500.00 |
            | Tuition fees already paid       | £0.00     |
            | Accommodation fees already paid | £0.00     |
            | Sort code                       | 11-11-11  |
            | Account number                  | 11111111  |

###################################### Section - Check for text on Output  - Insufficient Information ######################################

    Scenario: Caseworker enters account number and sort code where no records exist within the period stated (no test data for all 9's)
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 07/06/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 99-99-99   |
            | Account number                  | 99999999   |
        Then the service displays the following page content
            | Page dynamic heading  | There is no record for the sort code and account number with Barclays                                                            |
            | Page Dynamic Sub Text | We couldn't perform the financial requirement check as no information exists for sort code 99-99-99 and account number 99999999. |
        And the service displays the following your search headers in order
            | Sort code      | 99-99-99 |
            | Account number | 99999999 |

 ###################################### Section - Check for text on input page ######################################

    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the caseworker views the query page
        Then the service displays the following page content
            | Page heading | Tier 4 (General) student |



