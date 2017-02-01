Feature: Handle the responses from the Barclays Consent API and display the appropriate output page when applicant consent is not granted.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status.

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui


            ## 'Pending' consent status followed by 'Failure' status (e.g. consent not granted) ##

    Scenario Outline: Consent status is in 'Pending' status when the financial status check is performed
        And the caseworker selects <Tier>
        And <Applicant> type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222224   |
        And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date         | 30/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222224   |
        Then The service displays the Consent has not been given output page including the results and your search headers
    Examples:
    | Tier      | Applicant     |
    | Tier two  | Main          |
    | Tier four | Non Doctorate |
    | Tier five | Dependent     |

        ## 'Failure' consent status received before the financial status check is performed ##
    Scenario: 'Failure' Status received before the financial status check is performed

        And consent is sought for the following:
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222224   |
        When caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked
        And the financial status check is performed with
            | Application raised date         | 30/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222225   |
        Then The service displays the Failure output page including the results and your search headers

