Feature: Handle the responses from the Barclays Consent API and display the appropriate output page when applicant consent is not granted.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status.


            ## 'Pending' consent status followed by 'Failure' status (e.g. consent not granted) ##
    Scenario Outline: Consent status is in 'Pending' status when the financial status check is performed
        Given the api health check response has status 200
        And the api consent response will be FAILURE
        And the api threshold response will be t2
        And caseworker is using the financial status service ui
        And the caseworker selects <Tier>
        And <Applicant> type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222229   |
        And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date         | 30/06/2016 |
            | End Date                        | 31/05/2016 |
            | Dependants                      | 0          |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 0                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222229                 |
            | DOB                        | 25/03/1987               |
        Examples:
            | Tier      | Applicant     |
            | Tier two  | Main          |
            | Tier five | Dependent     |


#        ## 'Failure' consent status received before the financial status check is performed ##
#    Scenario Outline: 'Failure' Status received before the financial status check is performed
#        And the caseworker selects <Tier>
#        And <Applicant> type is selected
#        And the caseworker selects the Yes, check Barclays radio button
#        And consent is sought for the following:
#            | DOB            | 25/03/1987 |
#            | Sort code      | 22-22-23   |
#            | Account number | 22222225   |
#        When caseworker submits the 'Get Consent' section of the form
#        And the Consent API is invoked
#        And the financial status check is performed with
#            | Application raised date         | 30/05/2016 |
#            | End Date                        | 30/05/2016 |
#            | In London                       | Yes        |
#            | Accommodation fees already paid | 0          |
#            | Dependants                      | 0          |
#            | DOB                             | 25/03/1987 |
#            | Sort code                       | 22-22-23   |
#            | Account number                  | 22222225   |
#        Then The service displays the Failure output page including the results and your search headers
#    Examples:
#    | Tier      | Applicant     |
#    | Tier two  | Main          |
#    | Tier four | Non Doctorate |
#    | Tier five | Dependent     |
