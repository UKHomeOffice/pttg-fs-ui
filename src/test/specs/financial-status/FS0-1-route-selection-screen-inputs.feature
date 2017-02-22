Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Background:
        Given the api health check response has status 200


    Scenario:  Caseworker selects Tier 4
        When caseworker is on page t4
        Then applicant type should have the following options
            | main  | Main applicant (with & without dependants) |
            | dependant | Dependants only                            |


    Scenario: Caseworker selects the student type - Tier 4 (General)
        When caseworker is on page t4
        And the main option of the applicant type radio is selected
        And the submit button is clicked
        Then student type should have the following options
            | general   | General student                  |
            | doctorate | Doctorate extension scheme       |
            | pgdd      | Postgraduate doctor or dentist   |
            | sso       | Student union sabbatical officer |

    Scenario: Caseworker selects Tier 2 (General)
        When caseworker is on page t2
        Then applicant type should have the following options
            | main      | Main applicant (with & without dependants) |
            | dependant | Dependants only                            |

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5
        Then applicant type should have the following options
            | main      | Main applicant (with & without dependants) |
            | dependant | Dependants only                            |

    Scenario: Caseworker selects Tier 5
        When caseworker is on page t5/main
        Then do check should have the following options
            | yes | Yes, check Barclays |
            | no  | No                  |
