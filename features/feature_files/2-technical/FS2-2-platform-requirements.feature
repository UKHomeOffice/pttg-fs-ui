Feature: Capabilities required for platform integration and support eg healthchecks, logging, auditing

    Scenario: Health check shows UP even when API server not reachable
        Given the api is unreachable
        Then the readiness response status should be 200

    Scenario: Liveness check responds with success as long as the app is running, even if the api is unreachable
        Given the api is unreachable
        Then the liveness response status should be 200
