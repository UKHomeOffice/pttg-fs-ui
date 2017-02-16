Feature: Total Funds Required Calculation - Tier 2 & 5 (General) (single current account including dependants) and dependant only applicants

    Acceptance criteria

    Requirement to meet Tier 2 & 5 (General)  passed and not passed

    Required Maintenance threshold calculation to pass this feature file

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api threshold response will be t2
        And the api daily balance response will Pass
        And caseworker is using the financial status service ui
        And the caseworker selects Tier two
        And Main applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
            | DOB            | 25/03/1987 |
        And the default details are
            | Application raised date | 30/07/2016 |
            | End date                | 04/07/2016 |
            | Dependants              | 0          |

##### pass for main applicant #####

    Scenario: Laura is a Tier 2 (General) and sufficient funds
        When the financial status check is performed with
            | Dependants | 0 |
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
            | Dependants | 1 |
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
        Given caseworker is on page t2/dependant/bank/details
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £945.00                  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

   ##### not pass dependant only #####

    Scenario: Karen is a Tier 5 (Temporary) and has insufficient funds
        Given caseworker is on page t2/dependant/bank/details
        And the api daily balance response will Fail-low-balance
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £945.00                |
            | Lowest Balance             | £100.00 on 03/10/2016   |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

##### PASS for dependant only applicant - Tier 2 #####

    Scenario: Donald and Hilary are Tier 2 (General) dependant only applicants and have sufficient funds
        Given the account has sufficient funds for tier 2
        When the financial status check is performed with
            | Dependants | 2 |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Account holder name        | Ronald Taylor            |
            | Total funds required       | £1260.00                 |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 2                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |

##### NOT PASS for dependant only applicant - Tier 5 #####

    Scenario: Simon, Alvin and Theodore are Tier 5 (Temporary) dependant only applicants and have insufficient funds
        Given the account does not have sufficient funds for tier 5
        When the financial status check is performed with
            | Dependants | 3 |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £1890.00                 |
            | Lowest Balance             | £1,160.00 on 01/06/2016  |
            | Maintenance period checked | 06/04/2016 to 04/07/2016 |
            | Dependants                 | 3                        |
            | Sort code                  | 22-22-23                 |
            | Account number             | 22222223                 |
            | DOB                        | 25/03/1987               |
