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
        Given caseworker is on page t<Tier>/application/status/<Applicant>
        And the financial status check is performed with
            | DOB                             | 25/03/1987                   |
            | Sort code                       | 22-22-23                     |
            | Account number                  | 22222226                     |
            | Application raised date         | 30/05/2016                   |
            | End Date                        | 30/05/2016                   |
            | In London                       | Yes                          |
            | Accommodation fees paid | 0                            |
            | Dependants                      | 1                            |
            | Course start date               | 20/05/2016                   |
            | Course end date                 | 30/11/2016                   |
            | Tuition fees paid       | 300                          |
            | Tuition fees              | 8500.00                      |
            | Continuation Course             | No                           |
            | Course type                     | Main course degree of higher |
        Then the service displays the following page content
            | Outcome        | Consent pending                                      |
            | Outcome detail | The applicant has not yet responded to the request.  |
            | Consent check  | We will automatically check for consent again in 5s. |
        Examples:
            | Tier | Applicant |
            | 2    | main      |
            | 5    | dependant |


## 'Pending' consent status followed by 'SUCCESS' ##
    Scenario Outline: Consent status is in PENDING followed by SUCCESS
        Given the api health check response has status 200
        And the api consent response will be PENDING
        And the api threshold response will be t2
        And caseworker is using the financial status service ui
        Given caseworker is on page t<Tier>/application/status/<Applicant>
        And the financial status check is performed with
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222229   |
            | Application raised date | 30/07/2016 |
            | End Date                | 04/07/2016 |
            | Dependants              | 1          |
        And the progress bar is displayed
        And after at least 2 seconds
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the check again button is clicked
        And after at least 1 seconds
        Then the service displays the following result
            | Outcome                    | Passed                                          |
            | Outcome detail             | This applicant meets the financial requirements |
        Examples:
            | Tier | Applicant |
            | 2    | main      |
            | 5    | dependant |
