Feature: Pass - Tier 4 (General) Student In Country (single current account and no dependants)

    Requirement to meet Tier 4 pass

    Applicant has the required closing balance every day for a consecutive 28 day period
    ending on (and including) the Maintenance Period End Date

    Scenario: Shelly is a general student and has sufficient financial funds
    (On a daily basis the closing balance in her account is >= than the threshold required at £2350)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 30/05/2016 |
            | Total Funds Required        | 2350       |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total Funds Required       | £2,350                   |
            | Maintenance Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code                  | 13-56-09                 |
            | Account Number             | 23568498                 |


    Scenario: Brian is a general student and has sufficient financial funds
    (On a daily basis the closing balance in his account is = the total funds required at £2030)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 30/05/2016 |
            | Total Funds Required        | 2030       |
            | Sort Code                   | 14-93-02   |
            | Account Number              | 63428593   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total Funds Required       | £2,030                   |
            | Maintenance Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code                  | 14-93-02                 |
            | Account Number             | 63428593                 |
