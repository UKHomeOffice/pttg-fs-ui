Feature: Show clear error details when inputs are invalid

    Acceptance criteria

    The same validations for in and out of London

    Fields mandatory to fill in:
    End date of 28-day period
    In London - Yes or No options (mandatory)
    Course length - 1-2 months (mandatory)
    Accommodation fees already paid - numbers only. Highest amount £1,265. Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Background:
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen


######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | End Date                        |  |
            | In London                       |  |
            | Course start date               |  |
            | Course end date                 |  |
            | Accommodation fees already paid |  |
            | Number of dependants            |  |
            | Sort code                       |  |
            | Account number                  |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The course start date is invalid               |
            | The course end date is invalid                 |
            | The accommodation fees already paid is invalid |
            | The number of dependants is invalid            |
            | The account number is invalid                  |
            | The sort code is invalid                       |

######################### Validation on the End Date Field #########################

    Scenario: Case Worker does NOT enter End Date
        When the financial status check is performed with
            | End Date                        |            |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | end-date-error                   | Enter a valid end date           |

    Scenario: Case Worker enters invalid End Date - in the future
        When the financial status check is performed with
            | End Date                        | 30/05/2099 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | end-date-error                   | Enter a valid end date           |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/0d/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | end-date-error                   | Enter a valid end date           |

######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       |            |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | sort-code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-1    |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | sort-code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 00-00-00   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | sort-code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-1q   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | sort-code-error                  | Enter a valid sort code          |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  |            |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | account-number-error             | Enter a valid account number     |

    Scenario: Case Worker enters invalid Account Number - too short
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 1111111    |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | account-number-error             | Enter a valid account number     |


    Scenario: Case Worker enters invalid Account Number - all 0's
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 00000000   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | account-number-error             | Enter a valid account number     |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 111a1111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | account-number-error             | Enter a valid account number     |


######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       |            |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | in-london-error                  | Select an option                 |


######################### Validation on the Course start /end fields #########################
    Scenario: Case Worker does NOT enter Course start
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | true       |
            | Course start date               |            |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | course-start-date-error          | Enter a valid course start date  |

    Scenario: Case Worker does NOT enter Course end
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | true       |
            | Course start date               | 30/05/2016 |
            | Course end date                 |            |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | course-end-date-error            | Enter a valid course end date    |

    Scenario: Case Worker enters invalid course start date - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/0d/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | course-start-date-error          | Enter a valid course start date  |


    Scenario: Case Worker enters invalid Course Length - same day
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/05/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | course-length-error              | Enter a valid course length      |

    Scenario: Case Worker enters invalid Course Length - end date is before the start
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/04/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information |
            | course-length-error              | Enter a valid course length      |


######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid |            |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading      | There's some invalid information              |
            | accommodation-fees-already-paid-error | Enter a valid accommodation fees already paid |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | A          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading      | There's some invalid information              |
            | accommodation-fees-already-paid-error | Enter a valid accommodation fees already paid |

 ######################### Validation on the number of dependants Field #########################
    Scenario: Case Worker does NOT enter number of dependants
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            |            |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information   |
            | number-of-dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | A          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information   |
            | number-of-dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - negative
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | -1         |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information   |
            | number-of-dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - fractional
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 1.1        |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information   |
            | number-of-dependants-error       | Enter a valid number of dependants |
