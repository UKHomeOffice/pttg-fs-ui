Feature: Non Doctorate Content - Tier 4 (General) student (single current account and no dependants)


 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | The end of 28-day period              | 20/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 9          |
            | Total tuition fees for the first year | 9755.50    |
            | Tuition fees already paid             | 500        |
            | Accommodation fees already paid       | 250.50     |
            | Sort code                             | 14-55-11   |
            | Account number                        | 23568599   |
        Then the service displays the following result
            | Page dynamic detail | Passed                                            |
            | Results Row 1       | Results Total funds required                      |
            | Results Row 2       | Results 28-day period checked                     |
            | Your Search Row 1   | Your search Sort code                             |
            | Your Search Row 2   | Your search Account number                        |
            | Your Search Row 3   | Your search Inner London borough                  |
            | Your Search Row 4   | Your search Course length                         |
            | Your Search Row 5   | Your search Total tuition fees for the first year |
            | Your Search Row 6   | Your search Tuition fees already paid             |
            | Your Search Row 7   | Your search Accommodation fees already paid       |


    Scenario: Check for important text on the page
    This scenario is to check for required text on the page
        Given Case Worker is using the Financial Status Service Case Worker Tool
        When the financial status check is performed with
            | The end of 28-day period              | 20/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 9          |
            | Total tuition fees for the first year | 9755.50    |
            | Tuition fees already paid             | 500        |
            | Accommodation fees already paid       | 250.50     |
            | Sort code                             | 14-55-11   |
            | Account number                        | 23568599   |
        Then The FSPS Tier 4 general Case Worker tool page provides the following result
            | Page title         | Tier 4 (General) student (non-doctorate)                        |
            | Page sub title     | Financial Status check                                          |
            | Page static detail | This application meets all of the financial status requirements |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given Case Worker is using the Financial Status Service Case Worker Tool
        When the financial status check is performed with
            | The end of 28-day period              | 07/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 6          |
            | Total tuition fees for the first year | 8500.00    |
            | Tuition fees already paid             | 0          |
            | Accommodation fees already paid       | 0          |
            | Sort code                             | 13-56-09   |
            | Account number                        | 12345677   |
        Then The FSPS Tier 4 general Case Worker tool page provides the following result
            | Page dynamic detail | Not Passed                                        |
            | Results Row 1       | Results total funds required                      |
            | Results Row 2       | Results 28-day period checked                     |
            | Your Search Row 1   | Your search Sort code                             |
            | Your Search Row 2   | Your search Account number                        |
            | Your Search Row 3   | Your search Inner London borough                  |
            | Your Search Row 4   | Your search Course length                         |
            | Your Search Row 5   | Your search Total tuition fees for the first year |
            | Your Search Row 6   | Your search Tuition fees already paid             |
            | Your Search Row 7   | Your search Accommodation fees already paid       |

    Scenario: Check for important text on the page
    This scenario is to check for required text on the page
        Given Case Worker is using the Financial Status Service Case Worker Tool
        When the financial status check is performed with
            | The end of 28-day period              | 07/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 6          |
            | Total tuition fees for the first year | 8500.00    |
            | Tuition fees already paid             | 0          |
            | Accommodation fees already paid       | 0          |
            | Sort code                             | 13-56-09   |
            | Account number                        | 12345677   |
        Then The FSPS Tier 4 general Case Worker tool page provides the following result
            | Page title         | Tier 4 (General Student) (non-doctorate)                                |
            | Page sub title     | Financial Status check                                                  |
            | Page static detail | This application does not meet all of the financial status requirements |


###################################### Section - Check for text on Output  - Insufficient Information ######################################



    Scenario: Caseworker enters account number and sort code where no records exist within the period stated (no test data for all 9's)
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | The end of 28-day period              | 07/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 6          |
            | Total tuition fees for the first year | 8500.00    |
            | Tuition fees already paid             | 0          |
            | Accommodation fees already paid       | 0          |
            | Sort code                             | 99-99-99   |
            | Account number                        | 99999999   |
        Then the service displays the following result
            | Page dynamic heading  | There is no record for the sort code and account number with Barclays                                                           |
            | Page dynamic sub text | We couldn't perform the financial requirement check as no information exists for sort code 99-99-99 and account number 99999999 |
        And the service displays the following your search data
            | Your Search Row 1   | Your search Sort code                             |
            | Your Search Row 2   | Your search Account number                        |
            | Your Search Row 3   | Your search Inner London borough                  |
            | Your Search Row 4   | Your search Course length                         |
            | Your Search Row 5   | Your search Total tuition fees for the first year |
            | Your Search Row 6   | Your search Tuition fees already paid             |
            | Your Search Row 7   | Your search Accommodation fees already paid       |



 ###################################### Section - Check for text on input page ######################################


    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up

        Given Case worker is using the Financial Status Service Case Worker Tool
        When Case worker is displayed the Income Proving Service Case Worker Tool input page
            | The end of 28-day period              | 20/06/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 9          |
            | Total tuition fees for the first year | 9755.50    |
            | Tuition fees already paid             | 500        |
            | Accommodation fees already paid       | 250.50     |
            | Sort code                             | 13-00-11   |
            | Account number                        | 23578499   |
        Then FSPS Tier 4 general Case Worker tool input page provides the following result
            | Page title     | Tier 4 (General) student (non-doctorate)                                                             |
            | Page sub title | Financial Status Check                                                                               |
            | Page sub text  | Online statement checker for a Barclays current account holder (must be in the applicants own name). |


