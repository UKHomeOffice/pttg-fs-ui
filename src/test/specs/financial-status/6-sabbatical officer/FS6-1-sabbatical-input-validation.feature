Feature: Show clear error details when inputs are invalid

    Acceptance criteria

    The same validations for in and out of London

    Fields mandatory to fill in:
    End date of 28-day period
    In London - Yes or No options (mandatory)
    Course start date - numbers only. Cannot be the same as course end date
    Course end date - numbers only. Cannot be before the course start date
    Course length - 1-2 months (mandatory)
    Accommodation fees already paid - numbers only. Highest amount £1,265. Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Date of birth - should be dd/mm/yyyy (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Background:
        Given caseworker is using the financial status service ui
        And the sso student type is chosen
        And the default details are
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 20/04/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
            | DOB                             | 27/07/1981 |

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
            | DOB                             |  |
        Then the service displays the following error message
            | validation-error-summary-text                  | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The start date of course is invalid            |
            | The end date of course is invalid              |
            | The accommodation fees already paid is invalid |
            | The number of dependants is invalid            |
            | The account number is invalid                  |
            | The sort code is invalid                       |
            | The date of birth is invalid                   |

######################### Validation on the End Date Field #########################

    Scenario: Case Worker does NOT enter End Date
        When the financial status check is performed with
            | End Date                        |                                     |
        Then the service displays the following error message
            | End Date-error                  | Enter a valid end date              |

    Scenario: Case Worker enters invalid End Date - in the future
        When the financial status check is performed with
            | End Date                        | 30/05/2099                          |
        Then the service displays the following error message
            | End Date-error                  | Enter a valid end date              |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/0d/2016                          |
        Then the service displays the following error message
            | End Date-error                  | Enter a valid end date              |



######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        When the financial status check is performed with
            | Sort code                       |                                     |
        Then the service displays the following error message
            | sort Code-error                 | Enter a valid sort code             |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        When the financial status check is performed with
            | Sort code                       | 11-11-1    |
        Then the service displays the following error message
            | sort Code-error                 | Enter a valid sort code             |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        When the financial status check is performed with
            | Sort code                       | 00-00-00                            |
        Then the service displays the following error message
            | sort Code-error                 | Enter a valid sort code             |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        When the financial status check is performed with
            | Sort code                       | 11-11-1q                            |
        Then the service displays the following error message
            | sort Code-error                 | Enter a valid sort code             |

######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        When the financial status check is performed with
            | Account number                  |                                     |
        Then the service displays the following error message
            | account number-error            | Enter a valid account number        |

    Scenario: Case Worker enters invalid Account Number - too short
        When the financial status check is performed with
            | Account number                  | 1111111                             |
        Then the service displays the following error message
            | account number-error            | Enter a valid account number        |

    Scenario: Case Worker enters invalid Account Number - all 0's
        When the financial status check is performed with
            | Account number                  | 00000000                            |
        Then the service displays the following error message
            | account number-error            | Enter a valid account number        |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        When the financial status check is performed with
            | Account number                  | 111a1111                            |
        Then the service displays the following error message
            | account number-error            | Enter a valid account number        |


######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | In London                       |                                     |
        Then the service displays the following error message
            | In London-error                 | Select an option                    |


######################### Validation on the Course start / end fields #########################
    Scenario: Case Worker does NOT enter Course start date
        When the financial status check is performed with
            | Course start date               |                                     |
        Then the service displays the following error message
            | Course Start Date-error         | Enter a valid start date of course  |

    Scenario: Case Worker does NOT enter Course end date
        When the financial status check is performed with
            | Course end date                 |                                     |
        Then the service displays the following error message
            | Course End Date-error           | Enter a valid end date of course    |

    Scenario: Case Worker enters invalid Course start date - not numbers 0-9
        When the financial status check is performed with
            | Course start date               | 30/1d/2016                          |
        Then the service displays the following error message
            | Course Start Date-error         | Enter a valid start date of course  |

    Scenario: Case Worker enters invalid Course Length - same day
        When the financial status check is performed with
            | Course start date               | 30/05/2016                          |
            | Course end date                 | 30/05/2016                          |
        Then the service displays the following error message
            | Course End Date-error           | Enter a valid course length         |

    Scenario: Case Worker enters invalid Course Length - end before start
        When the financial status check is performed with
            | Course start date               | 30/05/2016                          |
            | Course end date                 | 30/04/2016                          |
        Then the service displays the following error message
            | Course End Date-error           | Enter a valid course length         |

######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        When the financial status check is performed with
            | Accommodation fees already paid       |                                               |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        When the financial status check is performed with
            | Accommodation fees already paid       | A                                             |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |


 ######################### Validation on the number of dependants Field #########################
    Scenario: Case Worker does NOT enter number of dependants
        When the financial status check is performed with
            | Number of dependants       |                                      |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants   |

    Scenario: Case Worker enters invalid number of dependants - not numbers 0-9
        When the financial status check is performed with
            | Number of dependants       | A                                    |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants   |

    Scenario: Case Worker enters invalid number of dependants - negative
        When the financial status check is performed with
            | Number of dependants       | -1                                   |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants   |

    Scenario: Case Worker enters invalid number of dependants - fractional
        When the financial status check is performed with
            | Number of dependants       | 1.1                                  |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants   |

 ######################### Validation on the Date of birth Field #########################

    Scenario: Case Worker does NOT enter Date of birth
        When the financial status check is performed with
            | DOB                             |                                 |
        Then the service displays the following error message
            | dob-error                       | Enter a valid date of birth     |

    Scenario: Case Worker enters invalid Date of birth - in the future
        When the financial status check is performed with
            | DOB                             | 01/09/2099                      |
        Then the service displays the following error message
            | dob-error                       | Enter a valid date of birth     |

    Scenario: Case Worker enters invalid Date og birth - not numbers 0-9
        When the financial status check is performed with
            | DOB                             | @1/07/1986                      |
        Then the service displays the following error message
            | dob-error                       | Enter a valid date of birth     |
