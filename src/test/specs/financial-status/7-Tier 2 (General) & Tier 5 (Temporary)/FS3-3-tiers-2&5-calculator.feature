Feature: Total Funds Required Calculation - Tier 2 & 5 (General) (single current account including dependants)

    Acceptance criteria

    Requirement to meet Tier 2 & 5 (General)  passed and not passed

    Required Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the t2main student type is chosen
        And the caseworker selects the Yes, Check Barclays  radio button
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222223   |
            | DOB                     | 25/03/1987 |


##### pass for main applicant #####

    Scenario: Laura is a Tier 2 (General) and sufficient funds
        Given the account has sufficient funds for tier 2
        When the financial status check is performed with
            | Dependants              | 0          |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 0                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

 ##### not pass as main applicant #####

    Scenario: Rob is a Tier 5 (Temporary) and has insufficient funds
        Given the account does not have sufficient funds for tier 2
        When the financial status check is performed with
            | Dependants     | 1   |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £945.00                  |
            | Lowest Balance             | £1,574.90 on 01/06/2016  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 1                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

##### pass dependant only ####

    Scenario: Lizzie is a Tier 2 (General) and sufficient funds
        Given caseworker is using the financial status service ui
        And the t2dependant student type is chosen
        And the account has sufficient funds for tier 2
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor            |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

   ##### not pass dependant only #####

    Scenario: Karen is a Tier 5 (Temporary) and has insufficient funds
        Given caseworker is using the financial status service ui
        And the t5dependant student type is chosen
        And the account does not have sufficient funds for tier 5
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £1,575.00                |
            | Lowest Balance             | £600.90 on 01/06/2016    |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |



