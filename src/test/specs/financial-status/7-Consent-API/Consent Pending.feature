Feature: Process 'pending' status and display the appropriate output page

    The Barclays Consent API response will return a consent 'status' (Initiated, Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status returned from Barclays.

    Background:
        Given the api health check response has status 200
        And the api consent response will be PENDING
        And the api threshold response will be t2
        And caseworker is using the financial status service ui

    ## Service receives a 'Pending' status and displays the appropriate output page ##

    Scenario Outline: Consent status is in 'Pending' status when the financial status check is performed

        And the caseworker selects <Tier>
        And <Applicant> applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222226   |
        And the financial status check is performed with
            | Application raised date         | 30/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | Course start date               | 20/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Tuition fees already paid       | 300        |
            | Total tuition fees              | 8500.00    |
            | Continuation Course             | No         |
            | Course type                     | Main       |
        Then the service displays the following page content
            | Outcome        | Consent pending                                             |
            | Outcome detail | The applicant has not yet responded to the request.         |
            | Consent check  | We will automatically check for consent again in 5s.|
        Examples:
            | Tier      | Applicant     |
            | Tier two  | Main          |
            | Tier five | Dependant     |


## 'Pending' consent status followed by 'SUCCESS' ##
    Scenario Outline: Consent status is in PENDING followed by SUCCESS
        Given the api health check response has status 200
        And the api consent response will be PENDING
        And the api threshold response will be t2
        And caseworker is using the financial status service ui
        And the caseworker selects <Tier>
        And <Applicant> applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222229   |
        And the financial status check is performed with
            | Application raised date | 30/07/2016 |
            | End Date                | 04/07/2016 |
            | Dependants              | 0          |
        And the progress bar is displayed
        And after at least 5 seconds
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the check again button is clicked
        And after at least 1 seconds
        Then the service displays the following result
            | Outcome                    | Passed                                          |
            | Outcome detail             | This applicant meets the financial requirements |
            | Total funds required       | £945.00                                         |
            | Maintenance period checked | 06/04/2016 to 04/07/2016                        |
            | Dependants                 | 0                                               |
            | Sort code                  | 22-22-23                                        |
            | Account number             | 22222229                                        |
            | DOB                        | 25/03/1987                                      |
        Examples:
            | Tier      | Applicant |
            | Tier two  | Main      |
            | Tier five | Dependant |


        ## Service receives a 'Pending' status and then times out (e.g > 15 minutes without a response from applicant)

#    Scenario: 'Pending' status received from the Consent API and remains at 'Pending' status after 15 minutes
#
#        Given the financial status check is performed
#        And the default details are
#            | Application raised date         | 30/06/2016 |
#            | End Date                        | 01/06/2016 |
#            | In London                       | Yes        |
#            | Accommodation fees already paid | 100        |
#            | Dependants                      | 0          |
#            | DOB                             | 25/03/1987 |
#            | Sort code                       | 22-22-23   |
#            | Account number                  | 22222223   |
#        And the Barclays Consent API provides the following response:
#            | status | PENDING |
#        And the Consent API is invoked at regular intervals
#        And the Barclays Consent API provides the following response:
#            | status | PENDING |
#        When 15 minutes have passed since the 'Initiated' status has been received
#        Then the service displays the 'More than 15 minutes has passed' page including the results and your search headers

    ## Remove 'Check again' link from 'timeout' page after 15 minutes

#    Scenario: 'Pending' status received 15 minutes after 'Initiated' status - remove 'Check again' link
#
#        Given the financial status check is performed
#        And the default details are
#            | Application raised date         | 30/06/2016 |
#            | End Date                        | 01/06/2016 |
#            | In London                       | Yes        |
#            | Accommodation fees already paid | 100        |
#            | Dependants                      | 0          |
#            | DOB                             | 25/03/1987 |
#            | Sort code                       | 22-22-23   |
#            | Account number                  | 22222223   |
#        And the Barclays Consent API provides the following response:
#            | status | PENDING |
#        And the Consent API is invoked at regular intervals
#        When the Barclays Consent API provides the following response:
#            | status | PENDING |
#        And  15 minutes have passed since the status "Initiated" has been received
#        And the service displays the 'More than 15 minutes has passed' page including the results and your search headers
#        Then The 'Check Again' link is not available selection

        ##  Timeout bar counts down from the 'Initiated Status' being received ##



