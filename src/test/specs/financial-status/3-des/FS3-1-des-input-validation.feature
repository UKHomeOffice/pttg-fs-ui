Feature: Show clear error details when inputs are invalid

    Acceptance criteria

    The same validations for in and out of London

    Fields mandatory to fill in:
    Application Raised Date - numbers only
    End Date - Format should be dd/mm/yyyy and the End date has to be less than 32 days before the application raised date
    Dependants - should always be 0 for courses of six months or less ####
    In London - Yes or No options (mandatory)
    Accommodation fees already paid - numbers only. Highest amount £1,265. Format should not contain commas or currency symbols
    Continuation Course - Yes or No

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And caseworker is on page t4/des/calc/details

######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | Application Raised Date         |  |
            | End Date                        |  |
            | In London                       |  |
            | Accommodation fees already paid |  |
            | Dependants                      |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The application raised date is invalid         |
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The accommodation fees already paid is invalid |
            | The number of dependants is invalid            |



######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | In London |  |
        Then the service displays the following error message
            | In London-error | Select an option |


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


           ######################### Validation on the Application Raised Date Field #########################

    Scenario: Case Worker does NOT enter Application Raised date
        When the financial status check is performed with
            | Application Raised Date |  |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

    Scenario: Case Worker enters invalid Course start date - not numbers 0-9
        When the financial status check is performed with
            | Application Raised Date | 30/1d/2016 |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

    Scenario: Case Worker enters Original Course Start Date - in the future
        When the financial status check is performed with
            | Application Raised Date | 30/05/2099 |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

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

    Scenario: Caseworker enters end date GREATER than 31 days of the Application Raised Date
        When the financial status check is performed with
            | End Date                | 01/02/2016 |
            | Application raised date | 31/01/2016 |
        Then the service displays the following error message
            | End Date-error | End date cannot be after application raised date |

    Scenario: Caseworker enters end date LESS THAN than 31 days of the Application Raised Date
        When the financial status check is performed with
            | End Date                | 31/12/2015 |
            | Application raised date | 31/01/2016 |
        Then the service displays the following error message
            | End Date-error | End date is not within 31 days of application raised date |

