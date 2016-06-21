@DataDir=FS1
Feature: Pass - Tier 4 (General) Student In Country (single current account and no dependants)

    Requirement to meet Tier 4 pass

    Applicant has the required closing balance every day for a consecutive 28 day period
    ending on (and including) the End Date

    Scenario: Shelly is a general student and has sufficient financial funds
    (On a daily basis the closing balance in her account is >= than the threshold required at £2350)

        Given caseworker is using the financial status service ui
        Given the test data for account 11111111
        When the financial status check is performed with
            | End Date             | 30/05/2016 |
            | Total Funds Required | 2030       |
            | Sort Code            | 11-11-11   |
            | Account Number       | 11111111   |
        Then the service displays the following result
            | Outcome               | Passed                   |
            | Total Funds Required  | £2,030                   |
            | 28-day Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code             | 11-11-11                 |
            | Account Number        | 11111111                 |

    Scenario: Brian is a general student and has sufficient financial funds
    (On a daily basis the closing balance in his account is = the total funds required at £2030)

        Given caseworker is using the financial status service ui
        Given  the test data for account 22222222
        When the financial status check is performed with
            | End Date             | 30/05/2016 |
            | Total Funds Required | 2030       |
            | Sort Code            | 22-22-22   |
            | Account Number       | 22222222   |
        Then the service displays the following result
            | Outcome               | Passed                   |
            | Total Funds Required  | £2,030                   |
            | 28-day Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code             | 22-22-22                 |
            | Account Number        | 22222222                 |

