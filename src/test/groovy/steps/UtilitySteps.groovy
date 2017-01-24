package steps

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import java.text.SimpleDateFormat

/**
 * @Author Home Office Digital
 */
class UtilitySteps {

    // todo pull in similar stuff from other projects
    // todo move this into a common jar

    def static toCamelCase(String s) {
        if (s.isEmpty()) return ""
        def words = s.tokenize(" ")*.toLowerCase()*.capitalize().join("")
        words[0].toLowerCase() + words.substring(1)
    }

    def static parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        Date date = sdf.parse(dateString)
        date
    }

    // todo parseIsoDate

    def static clickRadioButton(WebDriver driver, RadioButtonConfig radioConfig, String value) {

        def choice = value.toLowerCase()

        if (radioConfig.options.containsKey(choice)) {
            String id = radioConfig.options.get(choice) + '-label'
            println('\n\nRadio option ' + id)
            By byCss = By.cssSelector("[id='$id']")
            def btn = driver.findElement(byCss)
            if (btn.isDisplayed()) {
                btn.click()
            }
        } else {
            println ('\n\nclickRadioButton CANNOT FIND ' + choice)
        }

    }

    def static class RadioButtonConfig {
        def options = [:]

        def withOption(String choice, String id) {
            options.put(choice.toLowerCase(), id)
            return this
        }
    }
}
