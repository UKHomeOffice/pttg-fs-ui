Feature: Insufficient Information
    Tool identifies account number, sort code and date of birth combination does not exist with Barclay's

    # error message needs to be reviewed

    Scenario: No records exist within the period stated
        Given the api health check response has status 200
        Given no record for the account
        And the api consent response will be SUCCESS
        Given no record for the account
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        And the doctorate student type is chosen
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |

        When the financial status check is performed with
            | Application raised date         | 30/06/2016 |
            | End date                        | 10/06/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |

        Then the service displays the following page content
            | Page dynamic heading | Invalid or inaccessible account                                                  |
            | Page Dynamic detail  | One or more of the following conditions prevented us from accessing the account: |
        And the service displays the following your search data
            | Sort Code      | 99-99-99   |
            | Account Number | 99999999   |
            | DOB            | 27/05/1986 |

