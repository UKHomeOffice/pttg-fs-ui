Feature: Handle the responses from the Barclays Consent API and display the appropriate output page when applicant consent is not granted.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status.

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked

            ## 'Pending' consent status followed by 'Failure' status (e.g. consent not granted) ##

    Scenario: 'Pending' status received before the financial status check is performed followed by a 'Failure' Status

        Given the Barclays Consent API response returns
            | status | "PENDING" |
        And the financial status check is performed
        And the service displays the 'Consent pending' output page
        And the Consent API is invoked again to obtain the consent status
        When the Barclays Consent API response returns
            | status | "Failure" |
        Then The service displays the 'Consent has not been given' output page including the results and your search headers

        ## 'Failure' consent status received before the financial status check is performed ##
    Scenario: 'Failure' Status received before the financial status check is performed

        Given the Barclays Consent API response returns
            | status | "FAILURE |
        When the financial status check is performed
        Then The service displays the 'Consent has not been given' output page including the results and your search headers

