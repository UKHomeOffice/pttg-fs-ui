Feature: Total Funds Required Calculation - Tier 5 (Youth Mobility Scheme) (single current account ) No dependants are permitted in this scheme

    Acceptance criteria

    Requirement to meet Tier 5 (Youth Mobility Scheme ) thresholds = £1890.00 for main applicants (dependants not Permitted) passed and not passed

    Required to meet tier 5 youth mobility scheme = £1890 ( dependants not permitted)
    Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And the api threshold response will be t5 #
        And caseworker is using the financial status service ui
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |


############################# tier 5 (youth mobility scheme) main applicant #############################################

    Scenario: Lewis is a Tier 5 Youth mobility scheme worker #
        Given caseworker is on page t5/application/calc/youth #
         When the financial status check is performed with #
        Then the service displays the following result #
            | Outcome              | £1890.00 | #
            | Total funds required | £1890.00 | #




