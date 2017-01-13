Feature: Process 'pending' status and display the appropriate output page

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status returned from Barclays.


    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And caseworker submits the 'Get Consent' section of the form
        And the Consent API is invoked


    ## Service receives a 'Pending'status and displays the appropriate output page ##

    Scenario: 'Pending' status received from the Barclays Consent API response

        Given the financial status check is performed
        When the Barclays Consent API provides the following response:
            | status | "PENDING" |
        Then  The service displays the 'Consent pending' output page

      ## Service receives a 'Pending'status then 'Success' Status - result page ##

    Scenario: 'Pending' status received from the Consent API followed by 'Success' status - result page

        Given the financial status check is performed
        And the Barclays Consent API provides the following response:
            | status | "PENDING" |
        And the Consent API is invoked again to obtain the consent status
        And the Barclays Consent API provides the following response:
            | status | "SUCCESS" |
        And the Barclays Balances API is invoked
        When the service receives balance data from Barclays
        Then the service displays the 'not passed' page including the results and your search headers

        ## Service receives a 'Pending' status and then times out (e.g > 15 minutes without response)

    Scenario: 'Pending' status received from the Consent API and remains at Pending status after 15 minutes

        Given the financial status check is performed
        And the Barclays Consent API provides the following response:
            | status | "PENDING" |
        And the Consent API is invoked
        When the Barclays Consent API provides the following response:
            | status | "PENDING" |
        Then the service displays the 'timeout' page including the results and your search headers




