Feature: Copy results to paste buffer

########################################################################################################################

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the non-doctorate student type is chosen
        And the default details are
            | Application raised date         | 31/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 10         |
            | Accommodation fees already paid | 20         |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
            | DOB                             | 27/07/1981 |
            | Continuation Course             | No         |
            | Course type                     | Main       |



    ## WARNING this test will only be exectuted in Firefox as PhantomJS and Chrome have proven unreliable
    ## although the actual functionality in the end product is expected to work in all modern browsers
    ## the methods available to test copy paste are poorly supported
    Scenario: copy button clicked indication
        Given the account does not have sufficient funds
        And the financial status check is performed
        And the copy button text is 'Copy to clipboard'
        When the copy button is clicked
        Then the copy button text is 'Copied'
        And after at least 3 seconds
        And the copy button text is 'Copy to clipboard'


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
            | Student type                    | Tier 4 (General) student                        |
            | In London                       | Yes                                             |
            | Course dates                    | 30/05/2016 to 30/11/2016                        |
            | Total tuition fees              | £8,500.00                                       |
            | Tuition fees already paid       | £10.00                                          |
            | Accommodation fees already paid | £20.00 (limited to £1,265.00)                   |
            | Number of dependants            | 0                                               |
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
            | Student type                    | Tier 4 (General) student                        |
            | In London                       | Yes                                             |
            | Course dates                    | 30/05/2016 to 30/11/2016                        |
            | Total tuition fees              | £8,500.00                                       |
            | Tuition fees already paid       | £10.00                                          |
            | Accommodation fees already paid | £20.00 (limited to £1,265.00)                   |
            | Number of dependants            | 0                                               |
            | Sort code                       | 11-11-11                                        |
            | Account number                  | ****111X                                        |
            | Date of birth                   | 27/07/1981                                      |
            | Application raised date         |                                                 |
            | Continuation Course             | Yes                                             |
            | Original Course Start Date      |                                                 |
            | Leave End Date                  |                                                 |
            | Result timestamp                |                                                 |