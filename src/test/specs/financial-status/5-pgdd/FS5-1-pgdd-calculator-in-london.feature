Feature: Total Funds Required Calculation - Tier 4 (General) Student post graduate doctor or dentist In London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 post graduate doctor or dentist passed and not passed

    In London - The applicant must show evidence of funds to cover £1,265 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate in London
    (£1265) * remaining course length) -  Accommodation fees already paid

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass


        #Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Raj is a postgraduate doctor or dentist in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        And caseworker is using the financial status service ui
        And caseworker is on page t4/pgdd/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed with
            | Application raised date         | 29/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following result
            | Outcome                         | Not passed                     |
            | Account holder name             | Shelly Smith                   |
            | Total funds required            | £16,090.00                     |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016       |
            | Condition Code                  |                                |
            | Course dates checked            | 30/05/2016 to 30/07/2016       |
            | Lowest Balance                  | £100.00 on 03/10/2016          |
            | Tier                            | Tier 4 (General)               |
            | Applicant type                  | Postgraduate doctor or dentist |
            | In London                       | Yes                            |
            | Course length                   | 3 (limited to 9)               |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00)   |
            | Dependants                      | 0                              |
            | Sort code                       | 11-11-11                       |
            | Account number                  | 11111111                       |
            | DOB                             | 25/03/1987                     |
            | Continuation Course             | Yes                            |
            | Original Course Start Date      | 30/10/2015                     |

        #Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a postgraduate doctor or dentist in London student and has sufficient funds
        Given the account has sufficient funds
        And caseworker is using the financial status service ui
        And caseworker is on page t4/pgdd/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed with
            | Application raised date         | 31/05/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/06/2016 |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following result
            | Outcome                         | Passed                         |
            | Course dates checked            | 30/05/2016 to 30/06/2016       |
            | Tier                            | Tier 4 (General)               |
            | Applicant type                  | Postgraduate doctor or dentist |
            | In London                       | Yes                            |
            | Accommodation fees already paid | £100.00 (limited to £1,265.00) |
            | Dependants                      | 0                              |
            | Sort code                       | 11-11-11                       |
            | Account number                  | 11111111                       |
            | DOB                             | 25/03/1987                     |
            | Continuation Course             | Yes                            |
            | Original Course Start Date      | 30/10/2015                     |
        And the result table contains the following
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £16,090.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Condition Code             |                          |
            | Estimated Leave End Date   | 22/10/2017               |
            | Course length              | 2 (limited to 9)         |
            | Entire course length       | 9                        |

## Dependant Only - not pass - in London ##

    Scenario: Josie and Esther are a dependant only application (x2) - postgraduate doctor or dentist in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        And caseworker is using the financial status service ui
        And caseworker is on page t4/pgdd-dependants/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed with
            | Application raised date    | 29/06/2016 |
            | End date                   | 30/05/2016 |
            | In London                  | Yes        |
            | Course start date          | 30/05/2016 |
            | Course end date            | 30/07/2016 |
            | Dependants                 | 2          |
            | Continuation Course        | Yes        |
            | Original Course Start Date | 30/10/2015 |
        Then the service displays the following result
            | Outcome                    | Not passed                     |
            | Account holder name        | Shelly Smith                   |
            | Total funds required       | £16,090.00                     |
            | Maintenance period checked | 03/05/2016 to 30/05/2016       |
            | Condition Code             |                                |
            | Course dates checked       | 30/05/2016 to 30/07/2016       |
            | Lowest Balance             | £100.00 on 03/10/2016          |
            | Tier                       | Tier 4 (General)               |
            | Applicant type             | Postgraduate doctor or dentist |
            | In London                  | Yes                            |
            | Course length              | 3 (limited to 9)               |
            | Dependants                 | 2                              |
            | Sort code                  | 11-11-11                       |
            | Account number             | 11111111                       |
            | DOB                        | 25/03/1987                     |
            | Continuation Course        | Yes                            |
            | Original Course Start Date | 30/10/2015                     |


 ## Dependant Only - pass - in London ##

    Scenario: Ander is a a dependant only application (postgraduate doctor or dentist in London student and has sufficient funds)
        Given the account has sufficient funds
        And caseworker is using the financial status service ui
        And caseworker is on page t4/pgdd-dependants/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed with
            | Application raised date    | 31/05/2016 |
            | End date                   | 30/05/2016 |
            | In London                  | Yes        |
            | Course start date          | 30/05/2016 |
            | Course end date            | 30/06/2016 |
            | Dependants                 | 1          |
            | Continuation Course        | Yes        |
            | Original Course Start Date | 30/10/2015 |
        Then the service displays the following result
            | Outcome                    | Passed                         |
            | Course dates checked       | 30/05/2016 to 30/06/2016       |
            | Tier                       | Tier 4 (General)               |
            | Applicant type             | Postgraduate doctor or dentist |
            | In London                  | Yes                            |
            | Dependants                 | 1                              |
            | Sort code                  | 11-11-11                       |
            | Account number             | 11111111                       |
            | DOB                        | 25/03/1987                     |
            | Continuation Course        | Yes                            |
            | Original Course Start Date | 30/10/2015                     |
        And the result table contains the following
            | Account holder name        | Laura Taylor             |
            | Total funds required       | £16,090.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Condition Code             |                          |
            | Estimated Leave End Date   | 22/10/2017               |
            | Course length              | 2 (limited to 9)         |
            | Entire course length       | 9                        |
