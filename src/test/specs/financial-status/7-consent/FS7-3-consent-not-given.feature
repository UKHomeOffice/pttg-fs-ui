Feature: Handle the responses from the Barclays Consent API and display the appropriate output page when applicant consent is not granted.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status.

            ## 'Pending' consent status followed by 'Failure' status (e.g. consent not granted) ##
    Scenario Outline: Consent status is in 'Pending' status when the financial status check is performed
        Given the api health check response has status 200
        And the api consent response will be PENDING
        And the api threshold response will be t<Tier>
        And caseworker is using the financial status service ui
        And caseworker is on page t<Tier>/status/<Applicant>
        And the financial status check is performed with
            | Application raised date | 30/07/2016 |
            | End Date                | 04/07/2016 |
            | Dependants              | 1          |
            | DOB                     | 25/03/1987 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222229   |
        And after at least 5 seconds
        And the api consent response will be FAILURE
        And the check again button is clicked
        Then the service displays the following result
            | Outcome              | Consent has not been given                                                                    |
            | Outcome detail       | The applicant has not given consent to check their financial status directly with their bank. |
            | Total funds required | £945.00                                                                                       |
        Examples:
            | Tier | Applicant |
            | 2    | main      |
            | 5    | dependant |


        ## 'Failure' consent status received immediately when requesting consent (before the financial status check is performed) ##
    Scenario Outline: 'Failure' consent status received immediately when requesting consent (before the financial status check is performed)
        Given the api health check response has status 200
        And the api consent response will be FAILURE
        And caseworker is using the financial status service ui
        And caseworker is on page t<Tier>/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222225   |
        Then the service displays the following page content
            | Outcome        | Consent has not been given                                                                    |
            | Outcome detail | The applicant has not given consent to check their financial status directly with their bank. |
        Examples:
            | Tier      |
            | Tier two  |
            | Tier five |

