package steps

import cucumber.api.Scenario
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.digital.ho.proving.financial.Service

import static groovyx.net.http.ContentType.JSON

/**
 * @Author Home Office Digital
 */
class TestDataLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(TestDataLoader.class);

    def dataDirTagName = "@DataDir="
    def dataDirPath = "src/test/resources/account-data"
    def dataDirName

    def restClient

    def testDataFiles = [:]

    TestDataLoader(String host, int port) {

        restClient = new RESTClient("http://$host:$port/financialstatus/v1/")

        restClient.handler.failure = { response, data ->
            assert false : "Rest call failed with response status: $response.status and body: $data"
        }
    }

    def loadTestDataFiles(Scenario scenario) {

        def dataDirTag = scenario.getSourceTagNames().find {
            it.startsWith(dataDirTagName)
        }

        if (dataDirTag != null) {
            dataDirName = dataDirTag - dataDirTagName
            loadTestDataFilesFrom(dataDirName)

        } else {
            println ''
            LOGGER.warn('WARNING: No data directory specified. Tag the feature to specify the directory containing test data files, eg @DataDir=name')
        }
    }

    private def loadTestDataFilesFrom(String dataDir) {

        println ''
        LOGGER.debug("Loading test data files from: $dataDir")

        def dir = new File("$dataDirPath/$dataDir")

        if (!dir.isDirectory()) {
            println ''
            LOGGER.warn("WARNING: $dir.absolutePath is not a directory. No test data files will be loaded")
            return
        }

        testDataFiles = dir.listFiles().collectEntries {
            [(it.name - '.json'): it.text]
        }

        int count = testDataFiles.size()
        println ''
        LOGGER.debug("Loaded $count data files")
    }


    def loadTestData(String accountNumber) {

        println ''
        LOGGER.debug("Loading test data for $accountNumber")

        def json = testDataFiles[accountNumber]

        if (json == null) {
            assert false: "No test data file was loaded for account number $accountNumber from directory $dataDirName\r\n" +
                "You can create one using a copy of src/test/resources/account-data/example.json"
        }

        postTestData(accountNumber, json)
    }

    private def postTestData(String accountNumber, String json) {

        println ''
        LOGGER.debug("Posting test data for $accountNumber")

        def response = restClient.post(
            path: "accounts",
            body: json,
            contentType: JSON
        )

        assert response.status == 200: "Error posting test data to stub. Response status: $response.status"

        println ''
        LOGGER.debug("Completed posting test data for $accountNumber")
    }

    def clearTestData() {

        if(testDataFiles.size() <= 0){
            LOGGER.debug("No test data files were loaded, so not going to try to delete from stub")
            return
        }

        println ''
        LOGGER.debug("Clearing test data")

        def response = restClient.delete(
            path: "accounts"
        )

        assert response.status == 200: "Error deleting test data via stub. Response status: $response.status"
    }

}
