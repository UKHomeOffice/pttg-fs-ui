
Feature: Total Funds Required Calculation - Tier 2 (General) (single current account including dependants) and dependant only applicants
    Acceptance criteria

    Requirement to meet Tier 2 (General)thresholds = £945 for main applicants and £630 for dependants


    Maintenance threshold calculation for this feature file

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |

##### main applicant #####

    Scenario: Laura is a Tier 2 (General)
        Given caseworker is on page t2/application/calc/main
        And the api threshold response will be t2
        When the financial status check is performed with
            | Dependants | 0 |
        Then the service displays the following result
            | Outcome              | £945.00 |
            | Total funds required | £945.00 |
            | Dependants           | 0       |



##### dependant only applicant #####

    Scenario: Laura is a Tier 2 (General) dependant only
        Given caseworker is on page t2/application/calc/dependant
        And the api threshold response will be t2
        When the financial status check is performed with
            | Dependants | 1 |
        Then the service displays the following result
            | Outcome              | £945.00 |
            | Total funds required | £945.00 |
            | Dependants           | 1       |


######### threshold calculation for main applicant with 1 dependant###############

    Scenario: Maria is a tier 2 (General) and has a dependant
        Given caseworker is on page t2/application/calc/main
        And the api threshold response will be t2
        When the financial status check is performed with
            | Dependants | 1 |
        Then the service displays the following result
            | Outcome                | £945.00 |
            | Total funds required   | £945.00 |
            | Dependants             | 1        |





