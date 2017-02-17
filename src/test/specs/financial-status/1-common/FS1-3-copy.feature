Feature: Copy results to paste buffer

########################################################################################################################

    Background:
        Given the api health check response has status 200
        And the api daily balance response will Pass
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And caseworker is using the financial status service ui
        #And the api threshold response will be t4
        And the caseworker selects Tier four
        And the general student type is chosen
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 33-33-33   |
            | Account number | 33333333   |
        And the default details are
            | Application raised date         | 31/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 10         |
            | Accommodation fees already paid | 20         |
            | Dependants                      | 0          |
            #| Sort code                       | 11-11-11   |
            #| Account number                  | 11111111   |
            #| DOB                             | 27/07/1981 |
            | Continuation Course             | No         |
            | Course type                     | Main       |



    ## WARNING this test will only be exectuted in Firefox as PhantomJS and Chrome have proven unreliable
    ## although the actual functionality in the end product is expected to work in all modern browsers
    ## the methods available to test copy paste are poorly supported
    Scenario: copy results
        Given the account has sufficient funds
        And the financial status check is performed
        When the copy button is clicked
        Then the copied text includes
            | PASSED                          | This applicant meets the financial requirements |
            | Account holder name             | Laura Taylor                                    |
            | Total funds required            | £16,090.00                                      |
            | 28-day period checked           | 03/05/2016 to 30/05/2016                        |
            | Course length                   | 7 (limited to 9)                                |
            | Applicant type                  | Tier 4 (General) student                        |
            | In London                       | Yes                                             |
            | Course dates                    | 30/05/2016 to 30/11/2016                        |
            | Total tuition fees              | £8,500.00                                       |
            | Tuition fees already paid       | £10.00                                          |
            | Accommodation fees already paid | £20.00 (limited to £1,265.00)                   |
            | Dependants                      | 0                                               |
            | Sort code                       | 11-11-11                                        |
            | Account number                  | ****1111                                        |
            | Date of birth                   | 27/07/1981                                      |
            | Application raised date         | 31/05/2015                                      |
            | Continuation Course             | No                                              |
            | Leave End Date                  |                                                 |
            | Date Received                   |                                                 |

    Scenario: copy results when continuation course is selected as yes
        Given the account has sufficient funds
        And the financial status check is performed with
            | Continuation course        | Yes      |
            | Original course start date | 1/1/2014 |
        When the copy button is clicked
        Then the copied text includes
            | PASSED                          | This applicant meets the financial requirements |
            | Account holder name             | Laura Taylor                                    |
            | Total funds required            | £16,090.00                                      |
            | 28-day period checked           | 03/05/2016 to 30/05/2016                        |
            | Course length                   | 7 (limited to 9)                                |
            | Applicant type                  | Tier 4 (General) student                        |
            | In London                       | Yes                                             |
            | Course dates                    | 30/05/2016 to 30/11/2016                        |
            | Total tuition fees              | £8,500.00                                       |
            | Tuition fees already paid       | £10.00                                          |
            | Accommodation fees already paid | £20.00 (limited to £1,265.00)                   |
            | Dependants                      | 0                                               |
            | Sort code                       | 11-11-11                                        |
            | Account number                  | ****111X                                        |
            | Date of birth                   | 27/07/1981                                      |
            | Application raised date         |                                                 |
            | Continuation Course             | Yes                                             |
            | Original Course Start Date      |                                                 |
            | Leave End Date                  |                                                 |
            | Result timestamp                |                                                 |
