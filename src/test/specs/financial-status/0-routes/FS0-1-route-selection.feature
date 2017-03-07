Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Background:
        Given the api health check response has status 200


    Scenario:  Caseworker selects Tier 4
        When caseworker is on page t4
        Then the service displays the following page content
            | Page title  | Check financial status            |
            | Get consent | Get consent for a financial check |
            | Check status | Check fincancial status           |
            | calculator  | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 4 Check status
        When caseworker is on page t4/status
        Then the service displays the following page content
            | Main Applicant | Main applicant (with & without dependants) |
            | Dependant Only | Dependants only                            |


    Scenario: Caseworker selects the student type - Tier 4 (General)
        When caseworker is on page t4/status/main
        Then the service displays the following page content
            | general | General student                  |
            | des     | Doctorate extension scheme       |
            | pgdd    | Postgraduate doctor or dentist   |
            | suso    | Student union sabbatical officer |

        #### TIER 2 ####

    Scenario:  Caseworker selects Tier 2
        When caseworker is on page t2
        Then the service displays the following page content
            | Get consent | Get consent for a financial check |
            | Check status | Check fincancial status           |
            | calculator  | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 2 (General)
        When caseworker is on page t2/status
        Then the service displays the following page content
            | Main Applicant | Main applicant (with & without dependants) |
            | Dependant Only | Dependants only                            |

        #### TIER 5 ####

    Scenario:  Caseworker selects Tier 5
        When caseworker is on page t5
        Then the service displays the following page content
            | Get consent | Get consent for a financial check |
            | Check status | Check fincancial status           |
            | calculator  | Calculate daily funds required    |

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5/status
        Then the service displays the following page content
            | Main Applicant | Main applicant (with & without dependants) |
            | Dependant Only | Dependants only                            |
