Feature: Pass - Tier 4 (General) Student In Country (single current account and no dependants)

    Requirement to meet Tier 4 pass

    Applicant does not have the required closing balance every day for a consecutive 28 day period
    ending on (and including) the Maintenance Period End Date

    Scenario: Shelly is a general student and does not have sufficient financial funds
    (On a daily basis the closing balance in her account is < than the total funds required at £2349.99)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 30/05/2016 |
            | Total Funds Required        | 2350       |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Total Funds Required       | £2,350                   |
            | Maintenance Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code                  | 13-56-09                 |
            | Account Number             | 23568498                 |

