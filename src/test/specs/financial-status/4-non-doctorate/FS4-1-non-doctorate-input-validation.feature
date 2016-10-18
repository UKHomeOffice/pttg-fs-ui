Feature: Show clear error details when inputs are invalid

    Fields mandatory to fill in:
    End Date - Format should be dd/mm/yyyy
    Total Funds Required - Format should be
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Date of birth - should be dd/mm/yyyy (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Dependants - should always be 0 for courses of six months or less ####

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen
        And the default details are
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
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
            | Total tuition fees              |  |
            | Tuition fees already paid       |  |
            | Accommodation fees already paid |  |
            | Number of dependants            |  |
            | Sort code                       |  |
            | Account number                  |  |
            | DOB                             |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The start date of course is invalid            |
            | The end date of course is invalid              |
            | The total tuition fees is invalid              |
            | The tuition fees already paid is invalid       |
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
            | End Date                        | 30/05/2099 |
        Then the service displays the following error message
            | End Date-error                   | Enter a valid end date           |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        When the financial status check is performed with
            | End Date                        | 30/0d/2016 |
        Then the service displays the following error message
            | End Date-error                   | Enter a valid end date           |



######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        When the financial status check is performed with
            | Sort code                       |            |
        Then the service displays the following error message
            | sort Code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        When the financial status check is performed with
            | Sort code                       | 11-11-1    |
        Then the service displays the following error message
            | sort Code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        When the financial status check is performed with
            | Sort code                       | 00-00-00   |
        Then the service displays the following error message
            | sort Code-error                  | Enter a valid sort code          |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        When the financial status check is performed with
            | Sort code                       | 11-11-1q   |
        Then the service displays the following error message
            | sort Code-error                  | Enter a valid sort code          |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        When the financial status check is performed with
            | Account number                  |            |
        Then the service displays the following error message
            | account number-error             | Enter a valid account number     |

    Scenario: Case Worker enters invalid Account Number - too short
        When the financial status check is performed with
            | Account number                  | 1111111    |
        Then the service displays the following error message
            | account number-error             | Enter a valid account number     |

    Scenario: Case Worker enters invalid Account Number - all 0's
        When the financial status check is performed with
            | Account number                  | 00000000   |
        Then the service displays the following error message
            | account number-error             | Enter a valid account number     |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        When the financial status check is performed with
            | Account number                  | 111a1111   |
        Then the service displays the following error message
            | account number-error             | Enter a valid account number     |


######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | In London                       |            |
        Then the service displays the following error message
            | In London-error                  | Select an option                 |


######################### Validation on the Course start / end fields #########################
    Scenario: Case Worker does NOT enter Course start date
        When the financial status check is performed with
            | Course start date               |            |
        Then the service displays the following error message
            | Course Start Date-error          | Enter a valid start date of course  |

    Scenario: Case Worker does NOT enter Course end date
        When the financial status check is performed with
            | Course end date                 |            |
        Then the service displays the following error message
            | Course End Date-error            | Enter a valid end date of course    |

    Scenario: Case Worker enters invalid Course start date - not numbers 0-9
        When the financial status check is performed with
            | Course start date               | 30/0d/2016 |
        Then the service displays the following error message
            | Course Start Date-error          | Enter a valid start date of course  |

    Scenario: Case Worker enters invalid Course Length - same day
        When the financial status check is performed with
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/05/2016 |
        Then the service displays the following error message
            | Course End Date-error           | Enter a valid course length      |

    Scenario: Case Worker enters invalid Course Length - end before start
        When the financial status check is performed with
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/04/2016 |
        Then the service displays the following error message
            | Course End Date-error              | Enter a valid course length      |

######################### Validation on the Total tuition fees Field #########################
    Scenario: Case Worker does NOT enter Total tuition fees
        When the financial status check is performed with
            | Total tuition fees              |            |
        Then the service displays the following error message
            | total tuition fees-error         | Enter a valid total tuition fees |

    Scenario: Case Worker enters invalid Total tuition fees - not numbers 0-9
        When the financial status check is performed with
            | Total tuition fees              | A          |
        Then the service displays the following error message
            | total tuition fees-error         | Enter a valid total tuition fees |

######################### Validation on the Tuition fees already paid Field #########################
    Scenario: Case Worker does NOT enter Tuition fees already paid
        When the financial status check is performed with
            | Tuition fees already paid       |            |
        Then the service displays the following error message
            | tuition fees already paid-error  | Enter a valid tuition fees already paid |

    Scenario: Case Worker enters invalid Tuition fees already paid - not numbers 0-9
        When the financial status check is performed with
            | Tuition fees already paid       | A          |
        Then the service displays the following error message
            | tuition fees already paid-error  | Enter a valid tuition fees already paid |

######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        When the financial status check is performed with
            | Accommodation fees already paid |            |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        When the financial status check is performed with
            | Accommodation fees already paid | A          |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

######################### Validation on the number of dependants Field #########################
    Scenario: Case Worker does NOT enter number of dependants
        When the financial status check is performed with
            | Number of dependants            |            |
        Then the service displays the following error message
            | number Of Dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - not numbers 0-9
        When the financial status check is performed with
            | Number of dependants            | A          |
        Then the service displays the following error message
            | number Of Dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - negative
        When the financial status check is performed with
            | Number of dependants            | -1         |
        Then the service displays the following error message
            | number Of Dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - fractional
        When the financial status check is performed with
            | Number of dependants            | 1.1        |
        Then the service displays the following error message
            | number Of Dependants-error       | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - course length 6 months or less ######### RM
        When the financial status check is performed with
             | Course start date               | 30/05/2016 |
             | Course end date                 | 30/10/2016 |
             | Number of dependants            | 1          |
         Then the service displays the following error message
             | number Of Dependants-error      | Main applicants cannot be accompanied by dependants on courses of 6 months or less |

    Scenario: Case Worker enters invalid number of dependants - course length 6 months or less, then changes course length to above 6 months
        Given the financial status check is performed with
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/10/2016 |
            | Number of dependants            | 1          |
        When these fields are updated with
            | Course end date                 | 30/05/2017 |
        And the submit button is clicked
        Then the service displays the following page content
            | Page dynamic heading | Invalid or inaccessible account |

           ######################### Validation on the Date of birth Field #########################

    Scenario: Case Worker does NOT enter Date of birth
        When the financial status check is performed with
            | DOB                             |            |
        Then the service displays the following error message
            | dob-error                        | Enter a valid date of birth      |

    Scenario: Case Worker enters invalid Date of birth - in the future
        When the financial status check is performed with
            | DOB                             | 25/08/2099 |
        Then the service displays the following error message
            | dob-error                        | Enter a valid date of birth      |

    Scenario: Case Worker enters invalid Date og birth - not numbers 0-9
        When the financial status check is performed with
            | DOB                  | 25/0@/1986 |
        Then the service displays the following error message
            | dob-error                        | Enter a valid date of birth      |
