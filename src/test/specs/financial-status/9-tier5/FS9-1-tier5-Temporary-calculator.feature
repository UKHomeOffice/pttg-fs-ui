Feature: Total Funds Required Calculation - Tier 5 (Temporary) (single current account including dependants) and dependant only applicants

    Acceptance criteria

    Requirement to meet Tier 5 (temporary worker) thresholds = £945 for main applicants and £630 for dependants passed and not passed


    Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And the api threshold response will be t5 #
        And caseworker is using the financial status service ui
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |

########################## tier 5 temporary main applicant ##################################################

    Scenario: Laura is a Tier 5 (temporary)
        Given caseworker is on page t5/application/calc/temp/main #
        When the financial status check is performed with #
            | Dependants | 0 | #
        Then the service displays the following result #
            | Outcome              | £945.00 | #
            | Total funds required | £945.00 | #
            | Dependants           | 0       | #

 ###########################  tier 5 (Temporary) main  applicant with 1 dependant ################################################

    Scenario: Rob is a Tier 5 (Temporary) and has a dependant #
        Given caseworker is on page t5/application/calc/temp/main #
        When the financial status check is performed with #
            | Dependants | 1 | #
        Then the service displays the following result
            | Outcome              | £1,575.00 | #
            | Total funds required | £1,575.00 | #
            | Dependants           | 1         | #

####################################### tier 5 (temporary) dependant only applicant #########################################

    Scenario: Jessica and Hannah are two Tier 5 (Temporary) dependants #
        Given caseworker is on page t5/application/calc/temp/dependant
        When the financial status check is performed with
            | Dependants | 2 |
        Then the service displays the following result
            | Outcome              | £1260.00 | #
            | Total funds required | £1260.00 | #
            | Dependants           | 2       | #




