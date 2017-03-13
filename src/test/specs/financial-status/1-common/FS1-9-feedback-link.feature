Feature: Add feedback header and 'feedback’ hyperlink to the tool

    SO THAT I can collect feedback about the Financial Status Check Tool service
    AS A Proving Things Team
    WOULD LIKE A ‘feedback’ banner and hyperlink to be placed directly under all page headers

    ########################################################################

    Background:

        Given caseworker is using the financial status service ui
        And the api health check response has status 200
        And caseworker is using the financial status service ui


#    Scenario: Feedback banner on each page
#        Then the service displays the following page content
#            | Beta messsage | This is a new service – your feedback will help us to improve it. |
#        And the service has the following links
#            | feedback link | feedback | financialstatus@digital.homeoffice.gov.uk |

####

#        When the service displays the following page banner 'This is a new service - your feedback will help us improve it'
#        Then the word feedback is a mailto hyperlink

#
#    Scenario: Hyperlink generates blank email with a recipient of financialstatus@digital.homeoffice.gov.uk
#        When the feedback hyperlink in the banner is selected
#        Then a blank email is opened with a pre-populated 'to' recipient of financialstatus@digital.homeoffice.gov.uk
#        And has a pre-populated title of ‘Check financial status service feedback’
#
#
#    Scenario: Return the user to the page they were on
#        Given the feedback link in the banner is selected
#        When the user sends the email
#        Then the user returns to the page that they were on in the service



