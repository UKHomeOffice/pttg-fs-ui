Feature: Tool identifies if Applicant meets minimum Financial Requirement
    Pass Total Funds Required Calculation - Tier 4 (General) student (single current account and no dependants)

    Fields mandatory to fill in:
    Maintenance Period End Date - Format should be dd/mm/yyyy
    Total Funds Required - Format should be
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

######################### Validation on the Maintenance Period End Date Field #########################

    Scenario: Case Worker does NOT enter Maintenance Period End Date
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date |          |
            | Total Funds Required        | 2350.00  |
            | Sort Code                   | 13-56-09 |
            | Account Number              | 23568498 |
        Then the service displays the following message
            | Error Message | Please provide a valid Maintenance Period End Date |
            | Error Field   | maintenance-period-end-date-error                  |

    Scenario: Case Worker enters invalid Maintenance Period End Date - in the future
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 27/07/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Maintenance Period End Date |
            | Error Field   | maintenance-period-end-date-error                  |

    Scenario: Case Worker enters invalid Maintenance Period End date - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/0d/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Maintenance Period End Date |
            | Error Field   | maintenance-period-end-date-error                  |

######################### Validation on the Total Funds Required field #########################

    Scenario: Case Worker does NOT enter Total Funds Required
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        |            |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Total Funds Required |
            | Error Field   | total-funds-required-error                  |

    Scenario: Case Worker enters invalid Total Funds Required - just 0
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 0          |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Total Funds Required |
            | Error Field   | total-funds-required-error                  |

    Scenario: Case Worker enters invalid Total Funds Required - not numbers 0-9 (letters)
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 23g50.00   |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Total Funds Required |
            | Error Field   | total-funds-required-error                  |

    Scenario: Case Worker enters invalid Total Funds Required - not numbers 0-9 (negative)
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | -2350.00   |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Total Funds Required |
            | Error Field   | total-funds-required-error                  |

######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   |            |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Sort Code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-0    |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Sort Code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 00-00-00   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Sort Code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-0q   |
            | Account Number              | 23568498   |
        Then the service displays the following message
            | Error Message | Please provide a valid Sort Code |
            | Error Field   | sort-code-error                  |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              |            |
        Then the service displays the following message
            | Error Message | Please provide a valid Account Number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - too short
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 2356849    |
        Then the service displays the following message
            | Error Message | Please provide a valid Account Number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - too long
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 235684988  |
        Then the service displays the following message
            | Error Message | Please provide a valid Account Number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - all 0's
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 00000000   |
        Then the service displays the following message
            | Error Message | Please provide a valid Account Number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Maintenance Period End Date | 01/06/2016 |
            | Total Funds Required        | 2350.00    |
            | Sort Code                   | 13-56-09   |
            | Account Number              | 23568a98   |
        Then the service displays the following message
            | Error Message | Please provide a valid Account Number |
            | Error Field   | account-number-error                  |
