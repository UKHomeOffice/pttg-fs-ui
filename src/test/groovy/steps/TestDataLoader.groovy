package steps

import cucumber.api.Scenario
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.net.http.ContentType.JSON

/**
 * @Author Home Office Digital
 */
class TestDataLoader {

    /*
      * You can manually insert test data using POSTMAN (or similar) by POSTing the json data to
      *
      * http://localhost:8082/financialstatus/v1/accounts
      *
      * using content-type = application/json
      *
      * You can clear all test data by executing DELETE against the same URL
     */

    private static Logger LOGGER = LoggerFactory.getLogger(TestDataLoader.class);

    def dataDirTagName = "@DataDir="
    def dataDirPath = "src/test/resources/account-data"
    def dataDirName
    def dataDir

    def restClient

    TestDataLoader(String host, int port) {

        restClient = new RESTClient("http://$host:$port/financialstatus/v1/")

        restClient.handler.failure = { response, data ->
            assert false: "Rest call failed with response status: $response.status and body: $data"
        }
    }

    def prepareFor(Scenario scenario) {

        def dataDirTag = scenario.getSourceTagNames().find {
            it.startsWith(dataDirTagName)
        }

        if (dataDirTag != null) {
            dataDirName = dataDirTag - dataDirTagName

        } else {
            println ''
            LOGGER.warn('WARNING: No data directory specified. Tag the feature to specify the directory containing test data files, eg @DataDir=name')
        }

        dataDir = new File("$dataDirPath/$dataDirName")

        if (!dataDir.isDirectory()) {
            println ''
            LOGGER.warn("WARNING: $dataDir.absolutePath is not a directory. No test data files will be loaded")
        }
    }

    def loadTestData(String fileName) {

        def json = jsonFromFile(fileName)

        if (json == null) {
            assert false: "No test data file was loaded for $fileName from directory $dataDirName - " +
                "You can create one using a copy of src/test/resources/account-data/example.json"
        }

        postTestData(fileName, json)
    }

    private def jsonFromFile(String fileName) {

        println ''
        LOGGER.debug("Loading test data for $fileName from: $dataDir")

        def file = dataDir.listFiles().find {
            it.name.contains(fileName)
        }

        if (file == null) {
            return null
        }

        return file.text
    }

    private def postTestData(String fileName, String json) {

        println ''
        LOGGER.debug("Posting test data for $fileName")

        def response = restClient.post(
            path: "accounts",
            body: json,
            contentType: JSON
        )

        assert response.status == 200: "Error posting test data to stub. Response status: $response.status"

        println ''
        LOGGER.debug("Completed posting test data for $fileName")
    }

    def clearTestData() {

        // todo don't bother if we know we haven't sent anything

        println ''
        LOGGER.debug("Clearing test data")

        def response = restClient.delete(
            path: "accounts"
        )

        assert response.status == 200: "Error deleting test data via stub. Response status: $response.status"
    }

}
