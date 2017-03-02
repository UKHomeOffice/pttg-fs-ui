Feature: Total Funds Required Calculation - Tier 2 & 5 (General) (single current account including dependants) and dependant only applicants

    Acceptance criteria

    Requirement to meet Tier 2 & 5 (General)  passed and not passed

    Required Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |

##### pass for main applicant #####

    Scenario: Laura is a Tier 2 (General)
        Given caseworker is on page t2/calc/main
        And the api threshold response will be t2
        When the financial status check is performed with
            | Dependants | 0 |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 0                        |

 ##### not pass as main applicant #####

    Scenario: Rob is a Tier 5 (Temporary) and has a dependant
        Given caseworker is on page t5/calc/main
        And the api threshold response will be t5
        When the financial status check is performed with
            | Dependants | 1 |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £945.00                  |
            | Lowest Balance             | £1,574.90 on 01/06/2016  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 1                        |

##### pass for main applicant #####

    Scenario: Laura is a Tier 2 (General) dependant only
        Given caseworker is on page t2/calc/dependant
        And the api threshold response will be t2
        When the financial status check is performed with
            | Dependants | 0 |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 0                        |

 ##### not pass as main applicant #####

    Scenario: Rob is a Tier 5 (Temporary) and has a dependant dependant only
        Given caseworker is on page t5/calc/dependant
        And the api threshold response will be t5
        When the financial status check is performed with
            | Dependants | 1 |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £945.00                  |
            | Lowest Balance             | £1,574.90 on 01/06/2016  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 1                        |

