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
    Application Raised Date - numbers only
    Continuation Course - Yes/No

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And caseworker is using the financial status service ui
        And caseworker is on page t4/suso/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        And the default details are
            | Application raised date         | 01/06/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 20/04/2016 |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 0          |
            | Continuation Course             | Yes        |

######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | End Date                        |  |
            | In London                       |  |
            | Course start date               |  |
            | Course end date                 |  |
            | Accommodation fees already paid |  |
            | Dependants                      |  |
            | Continuation Course             |  |
        Then the service displays the following error message
            | validation-error-summary-text | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The start date of course is invalid            |
            | The end date of course is invalid              |
            | The accommodation fees already paid is invalid |
            | The number of dependants is invalid            |
            | The course continuation option is invalid             |

######################### Validation on the End Date Field #########################

    Scenario: Case Worker does NOT enter End Date
        When the financial status check is performed with
            | End Date |  |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Case Worker enters invalid End Date - in the future
        When the financial status check is performed with
            | End Date | 30/05/2099 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        When the financial status check is performed with
            | End Date | 30/0d/2016 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |


######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | In London |  |
        Then the service displays the following error message
            | In London-error | Select an option |


######################### Validation on the Course start / end fields #########################
    Scenario: Case Worker does NOT enter Course start date
        When the financial status check is performed with
            | Course start date |  |
        Then the service displays the following error message
            | Course Start Date-error | Enter a valid start date of course |

    Scenario: Case Worker does NOT enter Course end date
        When the financial status check is performed with
            | Course end date |  |
        Then the service displays the following error message
            | Course End Date-error | Enter a valid end date of course |

    Scenario: Case Worker enters invalid Course start date - not numbers 0-9
        When the financial status check is performed with
            | Course start date | 30/1d/2016 |
        Then the service displays the following error message
            | Course Start Date-error | Enter a valid start date of course |

    Scenario: Case Worker enters invalid Course Length - same day
        When the financial status check is performed with
            | Course start date | 30/05/2016 |
            | Course end date   | 30/05/2016 |
        Then the service displays the following error message
            | Course End Date-error | Enter a valid course length |

    Scenario: Case Worker enters invalid Course Length - end before start
        When the financial status check is performed with
            | Course start date | 30/05/2016 |
            | Course end date   | 30/04/2016 |
        Then the service displays the following error message
            | Course End Date-error | Enter a valid course length |

######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        When the financial status check is performed with
            | Accommodation fees already paid |  |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        When the financial status check is performed with
            | Accommodation fees already paid | A |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |


 ######################### Validation on the Dependants Field #########################
    Scenario: Case Worker does NOT enter Dependants
        When the financial status check is performed with
            | Dependants |  |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - not numbers 0-9
        When the financial status check is performed with
            | Dependants | A |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - negative
        When the financial status check is performed with
            | Dependants | -1 |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - fractional
        When the financial status check is performed with
            | Dependants | 1.1 |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |


        ######################### Validation on Continuation Course Field #########################

    Scenario: Case Worker does NOT enter Continuation Course
        When the financial status check is performed with
            | Continuation Course |  |
        Then the service displays the following error message
            | Continuation Course-error | Select an option |