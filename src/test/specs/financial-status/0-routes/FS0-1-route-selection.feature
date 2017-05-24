Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Background:
        Given the api health check response has status 200


    Scenario: Caseworker selects Tier 4
        When caseworker is on page t4
        Then the service displays the following page content
            | Page title   | Check financial status            |
            | Get consent  | Get consent for a financial check |
            | Check status | Check financial status            |
            | calculator   | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 4 Check status
        When caseworker is on page t4/application/status
        Then the service displays the following page content
            | Main Applicant | Main applicant  |
            | Dependant Only | Dependants only |


    Scenario: Caseworker selects the student type - Tier 4
        When caseworker is on page t4/application/status/main
        Then the service displays the following page content
            | general | General student                  |
            | des     | Doctorate extension scheme       |
            | pgdd    | Postgraduate doctor or dentist   |
            | suso    | Student union sabbatical officer |

        #### TIER 2 ####

    Scenario: Caseworker selects Tier 2
        When caseworker is on page t2
        Then the service displays the following page content
            | Get consent  | Get consent for a financial check |
            | Check status | Check financial status            |
            | calculator   | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 2 (General)
        When caseworker is on page t2/application/status
        Then the service displays the following page content
            | Main Applicant | Main applicant  |
            | Dependant Only | Dependants only |

        #### TIER 5 incorporate youth mobility scheme and temporary worker####

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5
        Then the service displays the following page content
            | Get consent  | Get consent for a financial check |
            | Check status | Check financial status            |
            | calculator   | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5/application/status
        Then the service displays the following page content
            | Main Applicant | Main applicant  |
            | Dependant Only | Dependants only |

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5/application/Status/select applicant
        Then the service displays the following page content
            | Main Applicant | Youth Mobility Scheme | #
            | Main Applicant | Temporary Worker      | #

    Scenario: Caseworker selects tier 5
        When caseworker is on page t5/application/status/select applicant/youth mobility scheme #
        Then the service displays the following content #

            | DOB | #
            | Sort Code | #
            | Account Number | #
            | Application raised date | #
            | 90 day period check     | #

    Scenario: Caseworker selects tier 5
        When caseworker is on page t5/application/status/select applicant/temporary worker #
        Then the service displays the following content #

            | DOB | #
            | Sort Code | #
            | Account Number | #
            | Application raised date | #
            | 90 day period check     | #
            | Dependants              | #
