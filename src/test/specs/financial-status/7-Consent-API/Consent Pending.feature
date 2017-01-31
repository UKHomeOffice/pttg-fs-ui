Feature: Process 'pending' status and display the appropriate output page

    The Barclays Consent API response will return a consent 'status' (Initiated, Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status returned from Barclays.

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the caseworker selects the Yes, check Barclays radio button
        And caseworker submits the 'Get Consent' section of the form
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |

    ## Service receives a 'Pending' status and displays the appropriate output page ##

    Scenario: 'Pending' status received from the Barclays Consent API response

        Given the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        When the Barclays Consent API provides the following response:
            | status | PENDING |
        Then the Consent Pending page is displayed
        And the service displays the following result
            | Total funds required       | £16,090.00               |
            | Maintenance period checked | 05/05/2016 to 30/05/2016 |
            | Estimated Leave End Date   | 22/10/2017               |
            | Calculator result received |                          |
        And the result table contains the following
            | Application raised date                 | 30/06/2016                                            |
            | Last date of the 28-day period to check |                                                       |
            | Dependants                              | 0                                                     |
            | In London                               | Yes                                                   |
            | Applicant type                          | Tier 4 (General) student (doctorate extension scheme) |
            | Course start date                       | 01/05/2016                                            |
            | Course end date                         | 25/09/2017                                            |
            | Continuation Course                     | Yes                                                   |
            | Original Course Start Date              | 30/10/2015                                            |
            | Total tuition fees                      | £8,500.00                                             |
            | Tuition fees already paid               | £0.00                                                 |
            | Accommodation fees already paid         | £100.00                                               |

      ## Service receives a 'Pending' status then 'Success' Status - result page ##

    Scenario: 'Pending' status received from the Consent API followed by 'Success' status

        Given the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        And the Consent API is invoked again to obtain the consent status
        And the Barclays Consent API provides the following response:
            | status | SUCCESS |
        When the Barclays Balances API is invoked
        Then the service receives balance data from Barclays
        And the service displays the appropriate 'result' page including the results and your search headers

        ## Service receives a 'Pending' status and then times out (e.g > 15 minutes without a response from applicant)

    Scenario: 'Pending' status received from the Consent API and remains at 'Pending' status after 15 minutes

        Given the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        And the Consent API is invoked at regular intervals
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        When 15 minutes have passed since the 'Initiated' status has been received
        Then the service displays the 'More than 15 minutes has passed' page including the results and your search headers

    ## Remove 'Check again' link from 'timeout' page after 15 minutes

    Scenario: 'Pending' status received 15 minutes after 'Initiated' status - remove 'Check again' link

        Given the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        And the Consent API is invoked at regular intervals
        When the Barclays Consent API provides the following response:
            | status | PENDING |
        And  15 minutes have passed since the status "Initiated" has been received
        And the service displays the 'More than 15 minutes has passed' page including the results and your search headers
        Then The 'Check Again' link is not available selection

        ##  Timeout bar counts down from the 'Initiated Status' being received ##

    Scenario:  The 'Consent pending' output page displays the timeout bar and uses the 'Initiated Status' to start timing

        Given the Barclays Consent API provides the following response:
            | status | INITIATED |
        And the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        When the Consent API is invoked at regular intervals
        And the Barclays Consent API provides the following response:
            | status | "PENDING" |
        And  The service displays the 'Consent pending' output page
        Then I can view a timeout bar and it counts down for a period of 15 minutes starting from the time stamp of the receipt of the 'Initiated' status


## User clicks 'Check Again Now' on output page

        Given the Barclays Consent API provides the following response:
            | status | INITIATED |
        And the financial status check is performed
        And the default details are
            | Application raised date         | 30/06/2016 |
            | End Date                        | 01/06/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        And the Consent API is invoked at regular intervals
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        And The service displays the 'Consent pending' output page
        When The user selects 'Check Again Now'
        And Less than 15 minutes have passed
        And the Barclays Consent API provides the following response:
            | status | PENDING |
        Then The page refreshes
        And I can view a timeout bar and it counts down for a period of 15 minutes starting from the time stamp of the receipt of the 'Initiated' status
