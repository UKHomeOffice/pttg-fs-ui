Feature: Total Funds Required Calculation - Tier 2 & 5 (General) (single current account including dependants)

    Acceptance criteria

    Requirement to meet Tier 2 & 5 (General)  passed and not passed

    Required Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 2 (General) is chosen
        And the default details are
            | End date       | 04/07/2016 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
            | DOB            | 25/03/1987 |


##### pass #####

    Scenario: Laura is a Tier 2 (General) and sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | Main applicant       | yes |
            | Number of dependants | 1   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £1,575.00                |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Number of dependants       | 1                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 02/12/1985               |

 ##### not pass #####

    Scenario: Rob is a Tier 5 (General) and has insufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | Main applicant       | yes |
            | Number of dependants | 1   |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Rob Taylor               |
            | Total funds required       | £1,575.00                |
            | Lowest Balance             | £1.574.90 on 01/06/2016  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Number of dependants       | 1                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 02/12/1985               |
