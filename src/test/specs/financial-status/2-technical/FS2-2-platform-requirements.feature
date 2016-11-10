Feature: Capabilities required for platform integration and support eg healthchecks, logging, auditing

  Scenario: Health check shows UP even when API server not reachable
    Given the api is unreachable
    Then the health check response status should be 200

