Feature: Availability message

    ## Warning message when unavailalble ##
    Scenario: Out of order message is shown on start page when the '/availability' end point reports an issue
        Given the api is unreachable
        And caseworker is using the financial status service ui
        Then the service displays the following message
            | availability-heading | You can’t use this service just now                  |

    Scenario: Out of order message is shown on ApplicantType page when the '/availability' end point reports an issue
        Given the api is unreachable
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        Then the service displays the following message
            | availability-heading | You can’t use this service just now                  |

    Scenario: Out of order message is shown on Details page when the '/availability' end point reports an issue
        Given the api is unreachable
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        And the des student type is chosen
        Then the service displays the following message
            | availability-heading | You can’t use this service just now                  |


    ## Warning should not be shown when available ##
    Scenario: Out of order message is NOT shown on start type page when the '/availability' end point reports OK
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        Then the availability warning box should not be shown

    Scenario: Out of order message is NOT shown on Applicant type page when the '/availability' end point reports OK
        Given the api health check response has status 200
        And the caseworker selects Tier four
        And caseworker is using the financial status service ui
        Then the availability warning box should not be shown


    Scenario: Out of order message is NOT shown on Details page when the '/availability' end point reports OK
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        And the des student type is chosen
        Then the availability warning box should not be shown


    Scenario: Out of order message disappears when availability changes to OK
        Given the api health check response has status 503
        And caseworker is using the financial status service ui
        And the service displays the following message
            | availability-heading | You can’t use this service just now                  |
        When the api health check response has status 200
        And after at least 2 seconds
        Then the availability warning box should not be shown
