Feature: Feedback form

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And the api daily balance response will Pass
        And the api condition codes response will be 2-3-1
        And caseworker is using the financial status service ui
        And caseworker is on page t4/application/status/main/general
        And the default details are
            | Application raised date    | 31/05/2016 |
            | End date                   | 30/05/2016 |
            | In London                  | Yes        |
            | Course start date          | 30/05/2016 |
            | Course end date            | 30/11/2016 |
            | Tuition fees               | 8500.00    |
            | Tuition fees paid          | 0          |
            | Accommodation fees paid    | 0          |
            | Dependants                 | 1          |
            | Continuation Course        | No         |
            | Original Course Start Date | 30/10/2015 |
            | Course type                | main       |
            | Course institution         | true       |
            | DOB                        | 21/09/1981 |
            | Sort code                  | 11-11-11   |
            | Account number             | 11111111   |


    Scenario: Feedback form is present on result page
        Given the api daily balance response will Fail-low-balance
        And the api condition codes response will be 2-3-1
        When the financial status check is performed
        Then the service displays the following result
            | feedbackheading | Feedback                                        |
            | match-label     | Did the FSPS result match the paper assessment? |
            | match-yes-label | Yes                                             |
            | match-no-label  | No                                              |
            | submit btn      | Submit and start a new search                   |

    Scenario: Yes or No must be selected on the feedback form
        Given the financial status check is performed
        When the submit button is clicked
        Then the service displays the following result
            | match-error | Select an option |
        And the error summary list contains the text
            | The "Did the FSPS result match the paper assessment?" option is invalid |
        And the following are hidden
            | match comment     |
            | match other       |
            | calculation-label |
            | ltrdate-label     |
            | name-label        |
            | balances-label    |

    Scenario: Yes is selected on the feedback form no other data is required
        Given the financial status check is performed
        When the feedback form is completed
            | match | Yes |
        And the submit button is clicked
        Then the service displays the following result
            | feedbackthanks | Thank you for supplying feedback on this service. |

    @ignore
    Scenario: User clicks on the Begin a new search button after completing feedback
        Given the financial status check is performed
        When the feedback form is completed
            | match       | no                |
            | caseref     | 12345678         |
            | calculation | checked           |
            | match other | It all went wrong |
        And the submit button is clicked
        And the new search button is clicked
        Then the service displays the following page content
            | Page title | Check financial status |


    Scenario Outline: Invalid Case IDs
      #Given Caseworker is using the Income Proving Service Case Worker Tool
        Given the financial status check is performed
        And the feedback form is completed
            | match   | No    |
            | caseref | <ref> |
        When the submit button is clicked
        Then the service displays the following result
            | caseref-error | Enter a valid "Case ID" |
        Examples:
            | ref        |
            | 1234567    |
            | 222        |
            | 2222222T   |
            | ninechars   |
            | 012345678   |
            | 0234567   |
            | 0234567666 |
            | 23456789 ! |


    Scenario Outline: Valid Case IDs
      #Given Caseworker is using the Income Proving Service Case Worker Tool
        Given the financial status check is performed
        And the feedback form is completed
            | match   | No    |
            | caseref | 12345678 |
        When the submit button is clicked
        Then the following are hidden
            | caseref-error |


    Scenario: When No is selected and result is Passed then why not option 'balances' should not be shown
        Given the financial status check is performed
        When the feedback form is completed
            | match | No |
        Then the following are visible
            | caseref           |
            | match other       |
            | calculation-label |
            | ltrdate-label     |
            | name-label        |
        And the service displays the following result
            | match-label   | Did the FSPS result match the paper assessment?                           |
            | caseref-label | Case ID                                                                   |
            | whynot        | Why do you think that the FSPS result did not match the paper assessment? |
        And the following are hidden
            | balances-label |


    Scenario: When No is selected and result is NOT Passed then why not option 'balances' should be shown
        Given the api daily balance response will Fail-low-balance
        And the financial status check is performed
        When the feedback form is completed
            | match | No |
        Then the following are visible
            | balances-label |


    Scenario: Validate that a case ref and comment are left
        Given the financial status check is performed
        And the feedback form is completed
            | match | No |
        When the submit button is clicked
        Then the service displays the following result
            | caseref-error     | Enter a valid "Case ID" |
            | match other-error | Please provide comments |


    Scenario: When Yes is selected and result is NOT Passed then case reference, checkboxes and text area should be displayed
        Given the api daily balance response will Fail-low-balance
        And the financial status check is performed
        When the feedback form is completed
            | match | Yes |
        Then the following are hidden
            | match comment     |
            | match other       |
            | calculation-label |
            | ltrdate-label     |
            | name-label        |
            | balances-label    |


    Scenario: Neither checkbox or other is complete so show messages
        Given the financial status check is performed
        When the feedback form is completed
            | match   | No        |
            | caseref | 12345678 |
        And the submit button is clicked
        Then the service displays the following result
            | whynot-error      | Select one or more from below |
            | match other-error | Please provide comments       |


    Scenario: A checkbox reason is chosen so no errors for other should be shown
        Given the financial status check is performed
        When the feedback form is completed
            | match       | No      |
            | calculation | checked |
        And the submit button is clicked
        Then the following are hidden
            | whynot-error      |
            | match other-error |


    Scenario: Other is complete so checkboxes are not required
        Given the financial status check is performed
        When the feedback form is completed
            | match       | No                |
            | match other | It all went wrong |
        And the submit button is clicked
        Then the following are hidden
            | whynot-error      |
            | match other-error |


