Feature: Edit search button to return UI populated with current values (for all routes on all output pages - pass & non passed)

########################################################################################################################
    Background:
        Given caseworker is on page t4/nondoctorate/consent


######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        When the financial status check is performed with
            | Sort code |  |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        When the financial status check is performed with
            | Sort code | 11-11-1 |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        When the financial status check is performed with
            | Sort code | 00-00-00 |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        When the financial status check is performed with
            | Sort code | 11-11-1q |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        When the financial status check is performed with
            | Account number |  |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |

    Scenario: Case Worker enters invalid Account Number - too short
        When the financial status check is performed with
            | Account number | 1111111 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |


    Scenario: Case Worker enters invalid Account Number - all 0's
        When the financial status check is performed with
            | Account number | 00000000 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        When the financial status check is performed with
            | Account number | 111a1111 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |

######################### Validation on the Date of birth Field #########################

    Scenario: Case Worker does NOT enter Date of birth
        When the financial status check is performed with
            | DOB |  |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |

    Scenario: Case Worker enters invalid Date of birth - in the future
        When the financial status check is performed with
            | DOB | 25/09/2099 |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |

    Scenario: Case Worker enters invalid Date of birth - not numbers 0-9
        When the financial status check is performed with
            | DOB | 25/08/198x |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |
