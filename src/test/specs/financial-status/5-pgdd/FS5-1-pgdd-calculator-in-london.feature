Feature: Total Funds Required Calculation - Tier 4 (General) Student post graduate doctor or dentist In London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 post graduate doctor or dentist passed and not passed

    In London - The applicant must show evidence of funds to cover £1,265 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate in London
    (£1265) * remaining course length) -  Accommodation fees already paid

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the pgdd student type is chosen

        #Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Raj is a postgraduate doctor or dentist in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | Application raised date         | 29/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-12   |
            | Account number                  | 11111112   |
            | DOB                             | 18/01/1992 |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following result
            | Outcome                         | Not passed                                                |
            | Account holder name             | Shelly Smith                                              |
            | Total funds required            | £16,090.00                                                |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                                  |
            | Course dates checked            | 30/05/2016 to 30/07/2016                                  |
            | Lowest Balance                  | £100.00 on 03/10/2016                                     |
            | Student type                    | Tier 4 (General) student (postgraduate doctor or dentist) |
            | In London                       | Yes                                                       |
            | Course length                   | 3 (limited to 9)                                          |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00)                              |
            | Number of dependants            | 0                                                         |
            | Sort code                       | 11-11-12                                                  |
            | Account number                  | 11111112                                                  |
            | DOB                             | 18/01/1992                                                |
            | Continuation Course             | Yes                                                       |
            | Original Course Start Date      | 30/10/2015                                                |

        #Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a postgraduate doctor or dentist in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | Application raised date         | 31/05/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/06/2016 |
            | Accommodation fees already paid | 100        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
            | DOB                             | 01/01/1976 |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following result
            | Outcome                         | Passed                                                    |
            | Account holder name             | Laura Taylor                                              |
            | Total funds required            | £16,090.00                                                |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                                  |
            | Course dates checked            | 30/05/2016 to 30/06/2016                                  |
            | Student type                    | Tier 4 (General) student (postgraduate doctor or dentist) |
            | In London                       | Yes                                                       |
            | Course length                   | 2 (limited to 9)                                          |
            | Accommodation fees already paid | £100.00 (limited to £1,265.00)                            |
            | Number of dependants            | 0                                                         |
            | Sort code                       | 22-22-23                                                  |
            | Account number                  | 22222223                                                  |
            | DOB                             | 01/01/1976                                                |
            | Continuation Course             | Yes                                                       |
            | Original Course Start Date      | 30/10/2015                                                |
